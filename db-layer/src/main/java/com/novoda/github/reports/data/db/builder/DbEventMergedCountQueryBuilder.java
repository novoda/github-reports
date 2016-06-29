package com.novoda.github.reports.data.db.builder;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.PullRequestStatsParameters;
import com.novoda.github.reports.data.db.tables.records.EventRecord;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOrderByStep;
import org.jooq.Table;
import org.jooq.TableField;

import static com.novoda.github.reports.data.db.DatabaseHelper.MERGED_PRS_ID;
import static com.novoda.github.reports.data.db.DatabaseHelper.conditionalBetween;
import static com.novoda.github.reports.data.db.Tables.*;
import static org.jooq.impl.DSL.*;

public class DbEventMergedCountQueryBuilder {

    private static final Field<String> GROUP_SELECTOR_FIELD = field("date_group", String.class);
    private static final String GROUP_SELECTOR_SEPARATOR = "-";
    private static final String NULL_SELECTOR = null;

    private static final String ALL_RELEVANT_USERS_TABLE = "all_relevant_users";
    private static final String EXTERNAL_USERS_TABLE = "external_users";
    private static final String TEAM_USERS_TABLE = "team_users";
    private static final String ASSIGNED_USERS_TABLE = "assigned_users";
    private static final String FILTER_USERS_TABLE = "filter_users";

    private static final Field<Integer> MERGED_COUNT_FIELD = field("merged_count", Integer.class);

    private final PullRequestStatsParameters parameters;
    private final DbEventUserQueryBuilder userQueryBuilder;

    public DbEventMergedCountQueryBuilder(PullRequestStatsParameters parameters, DbEventUserQueryBuilder userQueryBuilder) {
        this.parameters = parameters;
        this.userQueryBuilder = userQueryBuilder;
    }

    public SelectOrderByStep<Record3<BigDecimal, Long, String>> getStats() {
        SelectOrderByStep<Record3<BigDecimal, Long, String>> allUserStats = getAllUserStats();

        if (parameters.isWithAverage()) {
            allUserStats = allUserStats
                    .union(getAverageExternalUserStats())
                    .union(getAverageTeamUserStats())
                    .union(getAverageAssignedUserStats())
                    .union(getAverageFilterUserStats());
        }

        return allUserStats;
    }

    private SelectHavingConditionStep<Record3<BigDecimal, Long, String>> getAllUserStats() {
        Table<Record3<Long, String, String>> allRelevantUsersTable = userQueryBuilder.getAllUsers().asTable(ALL_RELEVANT_USERS_TABLE);
        return getUserStatsForRelevantUsers(allRelevantUsersTable);
    }

    private SelectHavingStep<Record3<BigDecimal, Long, String>> getAverageExternalUserStats() {
        Table<Record3<Long, String, String>> externalUsersTable = userQueryBuilder.getExternalUsers().asTable(EXTERNAL_USERS_TABLE);
        SelectHavingConditionStep<Record3<BigDecimal, Long, String>> externalUserStats = getUserStatsForRelevantUsers(externalUsersTable);

        return getAverageUserStats(externalUserStats, DbEventUserQueryBuilder.USER_EXTERNAL_ID);
    }

    private SelectHavingStep<Record3<BigDecimal, Long, String>> getAverageTeamUserStats() {
        Table<Record3<Long, String, String>> teamUsersTable = userQueryBuilder.getTeamUsers().asTable(TEAM_USERS_TABLE);
        SelectHavingConditionStep<Record3<BigDecimal, Long, String>> teamUserStats = getUserStatsForRelevantUsers(teamUsersTable);

        return getAverageUserStats(teamUserStats, DbEventUserQueryBuilder.USER_TEAM_ID);
    }

    private SelectHavingStep<Record3<BigDecimal, Long, String>> getAverageAssignedUserStats() {
        Table<Record3<Long, String, String>> assignedUsersTable = userQueryBuilder.getAssignedUsers().asTable(ASSIGNED_USERS_TABLE);
        SelectHavingConditionStep<Record3<BigDecimal, Long, String>> assignedUserStats = getUserStatsForRelevantUsers(assignedUsersTable);

        return getAverageUserStats(assignedUserStats, DbEventUserQueryBuilder.USER_ASSIGNED_ID);
    }

    private SelectHavingStep<Record3<BigDecimal, Long, String>> getAverageFilterUserStats() {
        Table<Record3<Long, String, String>> filterUsersTable = userQueryBuilder.getFilterUsers().asTable(FILTER_USERS_TABLE);
        SelectHavingConditionStep<Record3<BigDecimal, Long, String>> filterUserStats = getUserStatsForRelevantUsers(filterUsersTable);

        return getAverageUserStats(filterUserStats, DbEventUserQueryBuilder.USER_FILTER_ID);
    }

    private SelectHavingConditionStep<Record3<BigDecimal, Long, String>> getUserStatsForRelevantUsers(Table<Record3<Long, String, String>> table) {
        Field<String> groupField = getGroupFieldForMySQLOnly(parameters.getGroupBy());

        SelectJoinStep<Record3<BigDecimal, Long, String>> selectCount = selectCountEventsGroupBy(groupField);
        SelectJoinStep<Record3<BigDecimal, Long, String>> selectCountForRelevantUsers = innerJoinRelevantUsers(selectCount, table);
        SelectConditionStep<Record3<BigDecimal, Long, String>> whereRepoAndDateMatch = whereRepoMatchesAndDateIsInRange(selectCountForRelevantUsers);
        SelectHavingConditionStep<Record3<BigDecimal, Long, String>> havingMergeEvents = groupByFieldHavingMergedEvents(whereRepoAndDateMatch, groupField);

        return havingMergeEvents;
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

    private SelectJoinStep<Record3<BigDecimal, Long, String>> selectCountEventsGroupBy(Field<String> groupField) {
        return parameters.getContext()
                .select(count(EVENT.EVENT_TYPE_ID).cast(BigDecimal.class).as(MERGED_COUNT_FIELD), EVENT.AUTHOR_USER_ID, groupField)
                .from(EVENT);
    }

    private SelectJoinStep<Record3<BigDecimal, Long, String>> innerJoinRelevantUsers(SelectJoinStep<Record3<BigDecimal, Long, String>> select,
                                                                                     Table<Record3<Long, String, String>> table) {

        select = select
                .innerJoin(table)
                .on(EVENT.AUTHOR_USER_ID.eq(table.field(USER._ID)));
        return select;
    }

    private SelectConditionStep<Record3<BigDecimal, Long, String>> whereRepoMatchesAndDateIsInRange(
            SelectJoinStep<Record3<BigDecimal, Long, String>> select) {

        SelectConditionStep<Record3<BigDecimal, Long, String>> where = select.where(trueCondition());

        if (!parameters.getRepositories().isEmpty()) {
            where = select
                    .innerJoin(REPOSITORY)
                    .on(REPOSITORY._ID.eq(EVENT.REPOSITORY_ID))
                    .where(REPOSITORY.NAME.in(parameters.getRepositories()));
        }

        return where.and(conditionalBetween(EVENT.DATE, parameters.getFrom(), parameters.getTo()));
    }

    private SelectHavingConditionStep<Record3<BigDecimal, Long, String>> groupByFieldHavingMergedEvents(
            SelectConditionStep<Record3<BigDecimal, Long, String>> where,
            Field<String> groupField) {

        return where
                .groupBy(EVENT.EVENT_TYPE_ID, EVENT.AUTHOR_USER_ID, groupField)
                .having(EVENT.EVENT_TYPE_ID.eq(MERGED_PRS_ID));
    }

    private SelectHavingStep<Record3<BigDecimal, Long, String>> getAverageUserStats(
            SelectHavingConditionStep<Record3<BigDecimal, Long, String>> inputSelection,
            Long averageUserId) {

        return parameters.getContext()
                .select(avg(MERGED_COUNT_FIELD).as(MERGED_COUNT_FIELD), val(averageUserId).as(EVENT.AUTHOR_USER_ID), GROUP_SELECTOR_FIELD)
                .from(inputSelection)
                .groupBy(GROUP_SELECTOR_FIELD);
    }

}
