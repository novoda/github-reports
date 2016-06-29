package com.novoda.github.reports.data.db.builder;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.PullRequestStatsParameters;
import com.novoda.github.reports.data.db.tables.records.EventRecord;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOrderByStep;
import org.jooq.Table;
import org.jooq.TableField;

import static com.novoda.github.reports.data.db.DatabaseHelper.MERGED_PRS_ID;
import static com.novoda.github.reports.data.db.DatabaseHelper.OPENED_PRS_ID;
import static com.novoda.github.reports.data.db.DatabaseHelper.conditionalBetween;
import static com.novoda.github.reports.data.db.Tables.*;
import static com.novoda.github.reports.data.db.builder.DbEventUserQueryBuilder.USER_TYPE_FIELD;
import static org.jooq.impl.DSL.*;

public class DbEventCountForAuthorQueryBuilder {

    private static final Field<String> GROUP_SELECTOR_FIELD = field("date_group", String.class);
    private static final String GROUP_SELECTOR_SEPARATOR = "-";
    private static final String NULL_SELECTOR = null;

    private static final String ALL_RELEVANT_USERS_TABLE = "all_relevant_users";
    private static final String EXTERNAL_USERS_TABLE = "external_users";
    private static final String TEAM_USERS_TABLE = "team_users";
    private static final String ASSIGNED_USERS_TABLE = "assigned_users";
    private static final String FILTER_USERS_TABLE = "filter_users";

    private static final Field<Integer> COUNT_FIELD = field("count_field", Integer.class);

    private final PullRequestStatsParameters parameters;
    private final DbEventUserQueryBuilder userQueryBuilder;
    private final Integer eventIdForCount;

    public static DbEventCountForAuthorQueryBuilder newMergedCountQueryBuilderInstance(PullRequestStatsParameters parameters,
                                                                                       DbEventUserQueryBuilder userQueryBuilder) {

        return new DbEventCountForAuthorQueryBuilder(parameters, userQueryBuilder, MERGED_PRS_ID);
    }

    public static DbEventCountForAuthorQueryBuilder newOpenedCountQueryBuilderInstance(PullRequestStatsParameters parameters,
                                                                                       DbEventUserQueryBuilder userQueryBuilder) {

        return new DbEventCountForAuthorQueryBuilder(parameters, userQueryBuilder, OPENED_PRS_ID);
    }

    private DbEventCountForAuthorQueryBuilder(PullRequestStatsParameters parameters,
                                              DbEventUserQueryBuilder userQueryBuilder,
                                              Integer eventIdForCount) {

        this.parameters = parameters;
        this.userQueryBuilder = userQueryBuilder;
        this.eventIdForCount = eventIdForCount;
    }

    public SelectOrderByStep<Record4<BigDecimal, Long, String, String>> getStats() {
        SelectOrderByStep<Record4<BigDecimal, Long, String, String>> allUserStats = getAllUserStats();

        if (parameters.isWithAverage()) {
            allUserStats = allUserStats
                    .union(getAverageExternalUserStats())
                    .union(getAverageTeamUserStats())
                    .union(getAverageAssignedUserStats())
                    .union(getAverageFilterUserStats());
        }

        return allUserStats;
    }

    private SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> getAllUserStats() {
        Table<Record3<Long, String, String>> allRelevantUsersTable = userQueryBuilder.getAllUsers().asTable(ALL_RELEVANT_USERS_TABLE);
        return getUserStatsForRelevantUsers(allRelevantUsersTable);
    }

    private SelectHavingStep<Record4<BigDecimal, Long, String, String>> getAverageExternalUserStats() {
        Table<Record3<Long, String, String>> externalUsersTable = userQueryBuilder.getExternalUsers().asTable(EXTERNAL_USERS_TABLE);
        SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> externalUserStats = getUserStatsForRelevantUsers(externalUsersTable);

        return getAverageUserStats(externalUserStats, DbEventUserQueryBuilder.USER_EXTERNAL_ID);
    }

    private SelectHavingStep<Record4<BigDecimal, Long, String, String>> getAverageTeamUserStats() {
        Table<Record3<Long, String, String>> teamUsersTable = userQueryBuilder.getTeamUsers().asTable(TEAM_USERS_TABLE);
        SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> teamUserStats = getUserStatsForRelevantUsers(teamUsersTable);

        return getAverageUserStats(teamUserStats, DbEventUserQueryBuilder.USER_TEAM_ID);
    }

    private SelectHavingStep<Record4<BigDecimal, Long, String, String>> getAverageAssignedUserStats() {
        Table<Record3<Long, String, String>> assignedUsersTable = userQueryBuilder.getAssignedUsers().asTable(ASSIGNED_USERS_TABLE);
        SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> assignedUserStats = getUserStatsForRelevantUsers(assignedUsersTable);

        return getAverageUserStats(assignedUserStats, DbEventUserQueryBuilder.USER_ASSIGNED_ID);
    }

    private SelectHavingStep<Record4<BigDecimal, Long, String, String>> getAverageFilterUserStats() {
        Table<Record3<Long, String, String>> filterUsersTable = userQueryBuilder.getFilterUsers().asTable(FILTER_USERS_TABLE);
        SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> filterUserStats = getUserStatsForRelevantUsers(filterUsersTable);

        return getAverageUserStats(filterUserStats, DbEventUserQueryBuilder.USER_FILTER_ID);
    }

    private SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> getUserStatsForRelevantUsers(Table<Record3<Long, String, String>> table) {
        Field<String> groupField = getGroupFieldForMySQLOnly(parameters.getGroupBy());

        SelectJoinStep<Record4<BigDecimal, Long, String, String>> selectCount = selectCountEventsGroupBy(groupField);
        SelectJoinStep<Record4<BigDecimal, Long, String, String>> selectCountForRelevantUsers = innerJoinRelevantUsers(selectCount, table);
        SelectConditionStep<Record4<BigDecimal, Long, String, String>> whereRepoAndDateMatch = whereRepoMatchesAndDateIsInRange(selectCountForRelevantUsers);
        SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> havingSpecificEvents = groupByFieldHavingSpecificEventId(whereRepoAndDateMatch, groupField);

        return havingSpecificEvents;
    }

    private Field<String> getGroupFieldForMySQLOnly(EventDataLayer.PullRequestStatsGroupBy groupBy) {
        Field<String> groupField = year(EVENT.DATE).concat(GROUP_SELECTOR_SEPARATOR);

        if (groupBy == EventDataLayer.PullRequestStatsGroupBy.MONTH) {
            groupField = groupField.concat(month(EVENT.DATE));
        } else if (groupBy == EventDataLayer.PullRequestStatsGroupBy.WEEK) {
            groupField = groupField.concat(week(EVENT.DATE));
        } else {
            groupField = val(NULL_SELECTOR);
        }

        return groupField.as(GROUP_SELECTOR_FIELD);
    }

    /**
     * Formats a date field retrieving the week number in the date year (1-52).
     * This method is currently implemented for MySQL and may not work on other DBMSs.
     *
     * @param date The date field to format
     * @return A {@link String} representing the week number of the date field.
     */
    private Field<String> week(TableField<EventRecord, Timestamp> date) {
        return field("DATE_FORMAT({0}, \"%Y" + GROUP_SELECTOR_SEPARATOR + "%v\")", String.class, date);
    }

    private SelectJoinStep<Record4<BigDecimal, Long, String, String>> selectCountEventsGroupBy(Field<String> groupField) {
        return parameters.getContext()
                .select(count(EVENT.EVENT_TYPE_ID).cast(BigDecimal.class).as(COUNT_FIELD), EVENT.AUTHOR_USER_ID, USER_TYPE_FIELD, groupField)
                .from(EVENT);
    }

    private SelectJoinStep<Record4<BigDecimal, Long, String, String>> innerJoinRelevantUsers(SelectJoinStep<Record4<BigDecimal, Long, String, String>> select,
                                                                                             Table<Record3<Long, String, String>> table) {

        select = select
                .innerJoin(table)
                .on(EVENT.AUTHOR_USER_ID.eq(table.field(USER._ID)));
        return select;
    }

    private SelectConditionStep<Record4<BigDecimal, Long, String, String>> whereRepoMatchesAndDateIsInRange(
            SelectJoinStep<Record4<BigDecimal, Long, String, String>> select) {

        SelectConditionStep<Record4<BigDecimal, Long, String, String>> where = select.where(trueCondition());

        if (!parameters.getRepositories().isEmpty()) {
            where = select
                    .innerJoin(REPOSITORY)
                    .on(REPOSITORY._ID.eq(EVENT.REPOSITORY_ID))
                    .where(REPOSITORY.NAME.in(parameters.getRepositories()));
        }

        return where.and(conditionalBetween(EVENT.DATE, parameters.getFrom(), parameters.getTo()));
    }

    private SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> groupByFieldHavingSpecificEventId(
            SelectConditionStep<Record4<BigDecimal, Long, String, String>> where,
            Field<String> groupField) {

        return where
                .groupBy(EVENT.EVENT_TYPE_ID, EVENT.AUTHOR_USER_ID, groupField)
                .having(EVENT.EVENT_TYPE_ID.eq(eventIdForCount));
    }

    private SelectHavingStep<Record4<BigDecimal, Long, String, String>> getAverageUserStats(
            SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> inputSelection,
            Long averageUserId) {

        return parameters.getContext()
                .select(
                        avg(COUNT_FIELD).as(COUNT_FIELD),
                        val(averageUserId).as(EVENT.AUTHOR_USER_ID),
                        USER_TYPE_FIELD,
                        GROUP_SELECTOR_FIELD
                )
                .from(inputSelection)
                .groupBy(GROUP_SELECTOR_FIELD);
    }

}
