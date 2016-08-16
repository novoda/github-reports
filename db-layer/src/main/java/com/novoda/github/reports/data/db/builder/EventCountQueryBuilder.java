package com.novoda.github.reports.data.db.builder;

import com.novoda.github.reports.data.db.PullRequestStatsParameters;
import com.novoda.github.reports.data.db.tables.records.EventRecord;
import org.jooq.*;

import java.math.BigDecimal;

import static com.novoda.github.reports.data.db.DatabaseHelper.*;
import static com.novoda.github.reports.data.db.PullRequestStatsParameters.GROUP_SELECTOR_FIELD;
import static com.novoda.github.reports.data.db.Tables.*;
import static com.novoda.github.reports.data.db.builder.EventUserQueryBuilder.USER_TYPE_FIELD;
import static org.jooq.impl.DSL.*;

public class EventCountQueryBuilder {

    public static final Field<BigDecimal> QUANTITY_FIELD = field("quantity", BigDecimal.class);
    public static final Field<Long> USER_FIELD = field("user_id", Long.class);

    private static final String ALL_RELEVANT_USERS_TABLE = "all_relevant_users";
    private static final String EXTERNAL_USERS_TABLE = "external_users";
    private static final String ORGANISATION_USERS_TABLE = "organisation_users";
    private static final String ASSIGNED_USERS_TABLE = "assigned_users";

    private final PullRequestStatsParameters parameters;
    private final EventUserQueryBuilder userQueryBuilder;
    private final Integer eventIdForCount;
    private final TableField<EventRecord, Long> userIdFieldCountTarget;
    private final OwnerAuthor ownerAuthorConstraint;

    public static EventCountQueryBuilder forMergedCount(PullRequestStatsParameters parameters, EventUserQueryBuilder userQueryBuilder) {
        return new EventCountQueryBuilder(parameters, userQueryBuilder, MERGED_PULL_REQUESTS_ID, EVENT.AUTHOR_USER_ID, OwnerAuthor.NO_CONSTRAINT);
    }

    public static EventCountQueryBuilder forOpenedCount(PullRequestStatsParameters parameters, EventUserQueryBuilder userQueryBuilder) {
        return new EventCountQueryBuilder(parameters, userQueryBuilder, OPENED_PULL_REQUESTS_ID, EVENT.AUTHOR_USER_ID, OwnerAuthor.NO_CONSTRAINT);
    }

    public static EventCountQueryBuilder forOtherPeopleComments(PullRequestStatsParameters parameters, EventUserQueryBuilder userQueryBuilder) {
        return new EventCountQueryBuilder(parameters, userQueryBuilder, COMMENTED_PULL_REQUESTS_ID, EVENT.OWNER_USER_ID, OwnerAuthor.MUST_BE_DIFFERENT);
    }

    public static EventCountQueryBuilder forCommentsOtherPeople(PullRequestStatsParameters parameters, EventUserQueryBuilder userQueryBuilder) {
        return new EventCountQueryBuilder(parameters, userQueryBuilder, COMMENTED_PULL_REQUESTS_ID, EVENT.AUTHOR_USER_ID, OwnerAuthor.MUST_BE_DIFFERENT);
    }

    public static EventCountQueryBuilder forCommentsOwn(PullRequestStatsParameters parameters, EventUserQueryBuilder userQueryBuilder) {
        return new EventCountQueryBuilder(parameters, userQueryBuilder, COMMENTED_PULL_REQUESTS_ID, EVENT.AUTHOR_USER_ID, OwnerAuthor.MUST_BE_SAME);
    }

    public static EventCountQueryBuilder forCommentsAny(PullRequestStatsParameters parameters, EventUserQueryBuilder userQueryBuilder) {
        return new EventCountQueryBuilder(parameters, userQueryBuilder, COMMENTED_PULL_REQUESTS_ID, EVENT.AUTHOR_USER_ID, OwnerAuthor.NO_CONSTRAINT);
    }

    private EventCountQueryBuilder(PullRequestStatsParameters parameters,
                                   EventUserQueryBuilder userQueryBuilder,
                                   Integer eventIdForCount,
                                   TableField<EventRecord, Long> userIdFieldCountTarget,
                                   OwnerAuthor ownerAuthorConstraint) {

        this.parameters = parameters;
        this.userQueryBuilder = userQueryBuilder;
        this.eventIdForCount = eventIdForCount;
        this.userIdFieldCountTarget = userIdFieldCountTarget;
        this.ownerAuthorConstraint = ownerAuthorConstraint;
    }

    public SelectOrderByStep<Record4<BigDecimal, Long, String, String>> getStats() {
        SelectOrderByStep<Record4<BigDecimal, Long, String, String>> allUserStats = getAllUserStats();

        if (parameters.isWithAverage()) {
            allUserStats = allUserStats
                    .union(getAverageExternalUserStats())
                    .union(getAverageOrganisationUserStats())
                    .union(getAverageAssignedUserStats());
        }

        return allUserStats;
    }

    public SelectOrderByStep<Record4<BigDecimal, Long, String, String>> getOrganisationStats() {
        SelectOrderByStep<Record4<BigDecimal, Long, String, String>> organisationUserStats = getOrganisationUserStats();

        if (parameters.isWithAverage()) {
            organisationUserStats = organisationUserStats.union(getAverageOrganisationUserStats());
        }

        return organisationUserStats;
    }

    private SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> getAllUserStats() {
        Table<Record3<Long, String, String>> allRelevantUsersTable = userQueryBuilder.getAllUsers().asTable(ALL_RELEVANT_USERS_TABLE);
        return getUserStatsForRelevantUsers(allRelevantUsersTable);
    }

    private SelectHavingStep<Record4<BigDecimal, Long, String, String>> getAverageExternalUserStats() {
        SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> externalUserStats = getExternalUserStats();

        return getAverageUserStats(externalUserStats, EventUserQueryBuilder.USER_EXTERNAL_ID);
    }

    private SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> getExternalUserStats() {
        Table<Record3<Long, String, String>> externalUsersTable = userQueryBuilder.getExternalUsers().asTable(EXTERNAL_USERS_TABLE);
        return getUserStatsForRelevantUsers(externalUsersTable);
    }

    private SelectHavingStep<Record4<BigDecimal, Long, String, String>> getAverageOrganisationUserStats() {
        SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> organisationUserStats = getOrganisationUserStats();

        return getAverageUserStats(organisationUserStats, EventUserQueryBuilder.USER_ORGANISATION_ID);
    }

    private SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> getOrganisationUserStats() {
        Table<Record3<Long, String, String>> organisationUsersTable = userQueryBuilder.getOrganisationUsers().asTable(ORGANISATION_USERS_TABLE);
        return getUserStatsForRelevantUsers(organisationUsersTable);
    }

    private SelectHavingStep<Record4<BigDecimal, Long, String, String>> getAverageAssignedUserStats() {
        SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> assignedUserStats = getAssignedUserStats();

        return getAverageUserStats(assignedUserStats, EventUserQueryBuilder.USER_ASSIGNED_ID);
    }

    private SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> getAssignedUserStats() {
        Table<Record3<Long, String, String>> assignedUsersTable = userQueryBuilder.getAssignedUsers().asTable(ASSIGNED_USERS_TABLE);
        return getUserStatsForRelevantUsers(assignedUsersTable);
    }

    private SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> getUserStatsForRelevantUsers(Table<Record3<Long, String, String>> userTable) {
        Field<String> groupField = parameters.getGroupFieldForMySqlOnly();

        SelectJoinStep<Record4<BigDecimal, Long, String, String>> selectCount = selectCountEventsGroupBy(groupField);
        SelectJoinStep<Record4<BigDecimal, Long, String, String>> selectCountForRelevantUsers = innerJoinRelevantUsers(selectCount, userTable);
        SelectJoinStep<Record4<BigDecimal, Long, String, String>> selectCountForRelevantRepos = innerJoinMatchingRepos(selectCountForRelevantUsers);

        SelectConditionStep<Record4<BigDecimal, Long, String, String>> whereDateAndOwnerUserConstraintMatch =
                whereAuthorOwnerConstraintIsRespectedAndDateIsInRange(selectCountForRelevantRepos);

        SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> havingSpecificEvents =
                groupByFieldHavingSpecificEventId(whereDateAndOwnerUserConstraintMatch, groupField);

        return havingSpecificEvents;
    }

    private SelectJoinStep<Record4<BigDecimal, Long, String, String>> selectCountEventsGroupBy(Field<String> groupField) {
        return parameters.getContext()
                .select(
                        count(EVENT.EVENT_TYPE_ID).cast(BigDecimal.class).as(QUANTITY_FIELD),
                        userIdFieldCountTarget.as(USER_FIELD),
                        USER_TYPE_FIELD,
                        groupField
                )
                .from(EVENT);
    }

    private SelectJoinStep<Record4<BigDecimal, Long, String, String>> innerJoinRelevantUsers(
            SelectJoinStep<Record4<BigDecimal, Long, String, String>> select,
            Table<Record3<Long, String, String>> table) {

        select = select
                .innerJoin(table)
                .on(userIdFieldCountTarget.eq(table.field(USER._ID)));
        return select;
    }

    private SelectJoinStep<Record4<BigDecimal, Long, String, String>> innerJoinMatchingRepos(
            SelectJoinStep<Record4<BigDecimal, Long, String, String>> select) {

        if (!parameters.getRepositories().isEmpty()) {
            return select
                    .innerJoin(REPOSITORY)
                    .on(REPOSITORY._ID.eq(EVENT.REPOSITORY_ID))
                    .and(REPOSITORY.NAME.in(parameters.getRepositories()));
        }

        return select;
    }

    private SelectConditionStep<Record4<BigDecimal, Long, String, String>> whereAuthorOwnerConstraintIsRespectedAndDateIsInRange(
            SelectJoinStep<Record4<BigDecimal, Long, String, String>> select) {

        SelectConditionStep<Record4<BigDecimal, Long, String, String>> whereClause = select.where(trueCondition());

        whereClause = andOwnerAuthorConstraintIsRespected(whereClause);
        whereClause = andDateIsInRange(whereClause);

        return whereClause;
    }

    private SelectConditionStep<Record4<BigDecimal, Long, String, String>> andOwnerAuthorConstraintIsRespected(
            SelectConditionStep<Record4<BigDecimal, Long, String, String>> whereClause) {

        if (ownerAuthorConstraint == OwnerAuthor.MUST_BE_SAME) {
            whereClause = whereClause.and(EVENT.AUTHOR_USER_ID.eq(EVENT.OWNER_USER_ID));
        } else if (ownerAuthorConstraint == OwnerAuthor.MUST_BE_DIFFERENT) {
            whereClause = whereClause.and(EVENT.AUTHOR_USER_ID.ne(EVENT.OWNER_USER_ID));
        }
        return whereClause;
    }

    private SelectConditionStep<Record4<BigDecimal, Long, String, String>> andDateIsInRange(
            SelectConditionStep<Record4<BigDecimal, Long, String, String>> whereClause) {

        return whereClause.and(conditionalBetween(EVENT.DATE, parameters.getFrom(), parameters.getTo()));
    }

    private SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> groupByFieldHavingSpecificEventId(
            SelectConditionStep<Record4<BigDecimal, Long, String, String>> whereClause,
            Field<String> groupField) {

        return whereClause
                .groupBy(EVENT.EVENT_TYPE_ID, userIdFieldCountTarget, groupField)
                .having(EVENT.EVENT_TYPE_ID.eq(eventIdForCount));
    }

    private SelectHavingStep<Record4<BigDecimal, Long, String, String>> getAverageUserStats(
            SelectHavingConditionStep<Record4<BigDecimal, Long, String, String>> inputSelection,
            Long averageUserId) {

        return parameters.getContext()
                .select(
                        avg(QUANTITY_FIELD).as(QUANTITY_FIELD),
                        val(averageUserId).as(USER_FIELD),
                        USER_TYPE_FIELD,
                        GROUP_SELECTOR_FIELD
                )
                .from(inputSelection)
                .groupBy(GROUP_SELECTOR_FIELD);
    }

}
