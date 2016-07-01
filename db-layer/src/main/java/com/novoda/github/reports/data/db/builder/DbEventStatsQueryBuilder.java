package com.novoda.github.reports.data.db.builder;

import com.novoda.github.reports.data.db.PullRequestStatsParameters;

import java.math.BigDecimal;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SelectSeekStep2;

import static com.novoda.github.reports.data.db.DatabaseHelper.conditionalBetween;
import static com.novoda.github.reports.data.db.PullRequestStatsParameters.GROUP_SELECTOR_FIELD;
import static com.novoda.github.reports.data.db.Tables.EVENT;
import static com.novoda.github.reports.data.db.Tables.USER;
import static com.novoda.github.reports.data.db.builder.DbEventCountQueryBuilder.QUANTITY_FIELD;
import static com.novoda.github.reports.data.db.builder.DbEventCountQueryBuilder.USER_FIELD;
import static com.novoda.github.reports.data.db.builder.DbEventUserQueryBuilder.USER_TYPE_FIELD;
import static org.jooq.impl.DSL.field;

public class DbEventStatsQueryBuilder {

    public static final Field<Long> USER_ID_FIELD = field("user_id", Long.class);
    public static final Field<String> USER_NAME_FIELD = field("user_name", String.class);
    public static final Field<BigDecimal> MERGED_FIELD = field("merged", BigDecimal.class);
    public static final Field<BigDecimal> OPENED_FIELD = field("opened", BigDecimal.class);
    public static final Field<BigDecimal> OTHER_PEOPLE_COMMENTS_FIELD = field("other_people_comments", BigDecimal.class);
    public static final Field<BigDecimal> COMMENTS_OTHER_PEOPLE_FIELD = field("comments_other_people", BigDecimal.class);
    public static final Field<BigDecimal> COMMENTS_OWN_FIELD = field("comments_own", BigDecimal.class);
    public static final Field<BigDecimal> COMMENTS_ANY_FIELD = field("comments_any", BigDecimal.class);
    public static final Field<BigDecimal> AVG_OTHER_PEOPLE_COMMENTS_FIELD = field("avg_other_people_comments", BigDecimal.class);
    public static final Field<BigDecimal> AVG_COMMENTS_OTHER_PEOPLE_FIELD = field("avg_comments_other_people", BigDecimal.class);

    private static final BigDecimal DEFAULT_QUANTITY = BigDecimal.valueOf(0);

    private final PullRequestStatsParameters parameters;
    private final DbEventUserQueryBuilder userQueryBuilder;
    private final DbEventCountQueryBuilder mergedPrsQueryBuilder;
    private final DbEventCountQueryBuilder openedPrsQueryBuilder;
    private final DbEventCountQueryBuilder otherPeopleCommentsOnUserPrsQueryBuilder;
    private final DbEventCountQueryBuilder commentsOtherPeoplePrsQueryBuilder;
    private final DbEventCountQueryBuilder commentsOwnPrsQueryBuilder;
    private final DbEventCountQueryBuilder commentsAnyPrsQueryBuilder;

    public static DbEventStatsQueryBuilder newInstance(PullRequestStatsParameters parameters) {
        DbEventUserQueryBuilder userQueryBuilder = new DbEventUserQueryBuilder(parameters);
        DbEventCountQueryBuilder mergedPrsQueryBuilder = DbEventCountQueryBuilder.forMergedCount(parameters, userQueryBuilder);
        DbEventCountQueryBuilder openedPrsQueryBuilder = DbEventCountQueryBuilder.forOpenedCount(parameters, userQueryBuilder);
        DbEventCountQueryBuilder otherPeopleCommentsOnUserPrsQueryBuilder = DbEventCountQueryBuilder.forOtherPeopleComments(parameters, userQueryBuilder);
        DbEventCountQueryBuilder commentsOtherPeoplePrsQueryBuilder = DbEventCountQueryBuilder.forCommentsOtherPeople(parameters, userQueryBuilder);
        DbEventCountQueryBuilder commentsOwnPrsQueryBuilder = DbEventCountQueryBuilder.forCommentsOwn(parameters, userQueryBuilder);
        DbEventCountQueryBuilder commentsAnyPrsQueryBuilder = DbEventCountQueryBuilder.forCommentsAny(parameters, userQueryBuilder);

        return new DbEventStatsQueryBuilder(
                parameters,
                userQueryBuilder,
                mergedPrsQueryBuilder,
                openedPrsQueryBuilder,
                otherPeopleCommentsOnUserPrsQueryBuilder,
                commentsOtherPeoplePrsQueryBuilder,
                commentsOwnPrsQueryBuilder,
                commentsAnyPrsQueryBuilder
        );
    }

    private DbEventStatsQueryBuilder(PullRequestStatsParameters parameters,
                                     DbEventUserQueryBuilder userQueryBuilder,
                                     DbEventCountQueryBuilder mergedPrsQueryBuilder,
                                     DbEventCountQueryBuilder openedPrsQueryBuilder,
                                     DbEventCountQueryBuilder otherPeopleCommentsOnUserPrsQueryBuilder,
                                     DbEventCountQueryBuilder commentsOtherPeoplePrsQueryBuilder,
                                     DbEventCountQueryBuilder commentsOwnPrsQueryBuilder,
                                     DbEventCountQueryBuilder commentsAnyPrsQueryBuilder) {

        this.parameters = parameters;
        this.userQueryBuilder = userQueryBuilder;
        this.mergedPrsQueryBuilder = mergedPrsQueryBuilder;
        this.openedPrsQueryBuilder = openedPrsQueryBuilder;
        this.otherPeopleCommentsOnUserPrsQueryBuilder = otherPeopleCommentsOnUserPrsQueryBuilder;
        this.commentsOtherPeoplePrsQueryBuilder = commentsOtherPeoplePrsQueryBuilder;
        this.commentsOwnPrsQueryBuilder = commentsOwnPrsQueryBuilder;
        this.commentsAnyPrsQueryBuilder = commentsAnyPrsQueryBuilder;
    }

    public Map<String, ? extends Result<? extends Record>> getStats() {

        SelectOrderByStep<Record4<BigDecimal, Long, String, String>> mergedQuery = mergedPrsQueryBuilder.getStats();
        SelectOrderByStep<Record4<BigDecimal, Long, String, String>> openedQuery = openedPrsQueryBuilder.getStats();
        SelectOrderByStep<Record4<BigDecimal, Long, String, String>> otherPeopleCommentsQuery = otherPeopleCommentsOnUserPrsQueryBuilder.getStats();
        SelectOrderByStep<Record4<BigDecimal, Long, String, String>> commentsOtherPeopleQuery = commentsOtherPeoplePrsQueryBuilder.getStats();
        SelectOrderByStep<Record4<BigDecimal, Long, String, String>> commentsOwnQuery = commentsOwnPrsQueryBuilder.getStats();
        SelectOrderByStep<Record4<BigDecimal, Long, String, String>> commentsAnyQuery = commentsAnyPrsQueryBuilder.getStats();

        SelectOrderByStep<Record3<Long, String, String>> allUsers = userQueryBuilder.getAllUsersWithAverage();
        SelectConditionStep<Record1<String>> dateGroups = selectDateGroups(parameters.getContext(), parameters);

        SelectJoinStep<? extends Record> select = selectUsers(
                parameters.getContext(),
                mergedQuery,
                openedQuery,
                otherPeopleCommentsQuery,
                commentsOtherPeopleQuery,
                commentsOwnQuery,
                commentsAnyQuery,
                allUsers,
                dateGroups
        );
        SelectOnConditionStep<? extends Record> allStats = leftJoinWithAllStats(
                select,
                mergedQuery,
                openedQuery,
                otherPeopleCommentsQuery,
                commentsOtherPeopleQuery,
                commentsOwnQuery,
                commentsAnyQuery,
                allUsers,
                dateGroups
        );
        SelectSeekStep2<? extends Record, String, String> orderedStats = allStats.orderBy(GROUP_SELECTOR_FIELD, USER_NAME_FIELD);

        return orderedStats.fetchGroups(GROUP_SELECTOR_FIELD);
    }

    private SelectConditionStep<Record1<String>> selectDateGroups(DSLContext create, PullRequestStatsParameters parameters) {
        Field<String> groupField = parameters.getGroupFieldForMySQLOnly();
        Condition whereClause = conditionalBetween(EVENT.DATE, parameters.getFrom(), parameters.getTo());

        return create
                .selectDistinct(groupField)
                .from(EVENT)
                .where(whereClause);
    }

    private SelectJoinStep<? extends Record> selectUsers(DSLContext create,
                                                         SelectOrderByStep<Record4<BigDecimal, Long, String, String>> mergedQuery,
                                                         SelectOrderByStep<Record4<BigDecimal, Long, String, String>> openedQuery,
                                                         SelectOrderByStep<Record4<BigDecimal, Long, String, String>> otherPeopleCommentsQuery,
                                                         SelectOrderByStep<Record4<BigDecimal, Long, String, String>> commentsOtherPeopleQuery,
                                                         SelectOrderByStep<Record4<BigDecimal, Long, String, String>> commentsOwnQuery,
                                                         SelectOrderByStep<Record4<BigDecimal, Long, String, String>> commentsAnyQuery,
                                                         SelectOrderByStep<Record3<Long, String, String>> allUsers,
                                                         SelectConditionStep<Record1<String>> dateGroups) {

        return create
                .select(
                        dateGroups.field(GROUP_SELECTOR_FIELD).as(GROUP_SELECTOR_FIELD),
                        allUsers.field(USER._ID).as(USER_ID_FIELD),
                        allUsers.field(USER.USERNAME).as(USER_NAME_FIELD),
                        allUsers.field(USER_TYPE_FIELD).as(USER_TYPE_FIELD),
                        zeroIfNull(mergedQuery.field(QUANTITY_FIELD)).as(MERGED_FIELD),
                        zeroIfNull(openedQuery.field(QUANTITY_FIELD)).as(OPENED_FIELD),
                        zeroIfNull(otherPeopleCommentsQuery.field(QUANTITY_FIELD)).as(OTHER_PEOPLE_COMMENTS_FIELD),
                        zeroIfNull(commentsOtherPeopleQuery.field(QUANTITY_FIELD)).as(COMMENTS_OTHER_PEOPLE_FIELD),
                        zeroIfNull(commentsOwnQuery.field(QUANTITY_FIELD)).as(COMMENTS_OWN_FIELD),
                        zeroIfNull(commentsAnyQuery.field(QUANTITY_FIELD)).as(COMMENTS_ANY_FIELD),
                        zeroIfNull(otherPeopleCommentsQuery.field(QUANTITY_FIELD).div(openedQuery.field(QUANTITY_FIELD)))
                                .as(AVG_OTHER_PEOPLE_COMMENTS_FIELD),
                        zeroIfNull(commentsOtherPeopleQuery.field(QUANTITY_FIELD).div(mergedQuery.field(QUANTITY_FIELD)))
                                .as(AVG_COMMENTS_OTHER_PEOPLE_FIELD)
                )
                .from(allUsers)
                .crossJoin(dateGroups);
    }

    private Field<BigDecimal> zeroIfNull(Field<BigDecimal> field) {
        return field.nvl(DEFAULT_QUANTITY);
    }

    private SelectOnConditionStep<? extends Record> leftJoinWithAllStats(
            SelectJoinStep<? extends Record> select,
            SelectOrderByStep<Record4<BigDecimal, Long, String, String>> mergedQuery,
            SelectOrderByStep<Record4<BigDecimal, Long, String, String>> openedQuery,
            SelectOrderByStep<Record4<BigDecimal, Long, String, String>> otherPeopleCommentsQuery,
            SelectOrderByStep<Record4<BigDecimal, Long, String, String>> commentsOtherPeopleQuery,
            SelectOrderByStep<Record4<BigDecimal, Long, String, String>> commentsOwnQuery,
            SelectOrderByStep<Record4<BigDecimal, Long, String, String>> commentsAnyQuery,
            SelectOrderByStep<Record3<Long, String, String>> allUsers,
            SelectConditionStep<Record1<String>> dateGroups) {

        SelectOnConditionStep<? extends Record> withMerged =
                leftJoinWithStats(select, allUsers, mergedQuery, dateGroups);
        SelectOnConditionStep<? extends Record> withOpened =
                leftJoinWithStats(withMerged, allUsers, openedQuery, dateGroups);
        SelectOnConditionStep<? extends Record> withOtherPeopleComments =
                leftJoinWithStats(withOpened, allUsers, otherPeopleCommentsQuery, dateGroups);
        SelectOnConditionStep<? extends Record> withCommentsOtherPeople =
                leftJoinWithStats(withOtherPeopleComments, allUsers, commentsOtherPeopleQuery, dateGroups);
        SelectOnConditionStep<? extends Record> withCommentsOwn =
                leftJoinWithStats(withCommentsOtherPeople, allUsers, commentsOwnQuery, dateGroups);
        SelectOnConditionStep<? extends Record> withCommentsAll =
                leftJoinWithStats(withCommentsOwn, allUsers, commentsAnyQuery, dateGroups);

        return withCommentsAll;
    }

    private SelectOnConditionStep<? extends Record> leftJoinWithStats(SelectJoinStep<? extends Record> select,
                                                                      SelectOrderByStep<Record3<Long, String, String>> users,
                                                                      SelectOrderByStep<Record4<BigDecimal, Long, String, String>> leftJoinQuery,
                                                                      SelectConditionStep<Record1<String>> dateGroups) {

        return select
                .leftJoin(leftJoinQuery)
                .on(users.field(USER._ID).eq(leftJoinQuery.field(USER_FIELD)))
                .and(dateGroups.field(GROUP_SELECTOR_FIELD).eq(leftJoinQuery.field(GROUP_SELECTOR_FIELD)));
    }

}
