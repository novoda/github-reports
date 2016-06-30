package com.novoda.github.reports.data.db.builder;

import com.novoda.github.reports.data.db.PullRequestStatsParameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectOrderByStep;

import static com.novoda.github.reports.data.db.DatabaseHelper.conditionalBetween;
import static com.novoda.github.reports.data.db.Tables.*;
import static org.jooq.impl.DSL.*;

public class DbEventUserQueryBuilder {

    public static final Long USER_EXTERNAL_ID = -4L;
    public static final Long USER_TEAM_ID = -3L;
    public static final Long USER_ASSIGNED_ID = -2L;
    public static final Long USER_FILTER_ID = -1L;

    public static final Field<String> USER_TYPE_FIELD = field("user_type", String.class);

    public static final String USER_EXTERNAL = "EXTERNAL";
    public static final String USER_TEAM = "TEAM";
    public static final String USER_ASSIGNED = "ASSIGNED";
    public static final String USER_FILTER = "FILTER";

    private static final Field<String> USER_TYPE_EXTERNAL = val(USER_EXTERNAL).as(USER_TYPE_FIELD);
    private static final Field<String> USER_TYPE_TEAM = val(USER_TEAM).as(USER_TYPE_FIELD);
    private static final Field<String> USER_TYPE_ASSIGNED = val(USER_ASSIGNED).as(USER_TYPE_FIELD);
    private static final Field<String> USER_TYPE_FILTER = val(USER_FILTER).as(USER_TYPE_FIELD);

    private final PullRequestStatsParameters parameters;

    DbEventUserQueryBuilder(PullRequestStatsParameters parameters) {
        this.parameters = parameters;
    }

    SelectOrderByStep<Record3<Long, String, String>> getAllUsersWithAverage() {
        SelectOrderByStep<Record3<Long, String, String>> users = getExternalUsers()
                .union(getTeamUsers())
                .union(getAssignedUsers())
                .union(getFilterUsers());

        if (parameters.isWithAverage()) {
            users = users
                    .union(buildAverageUser(USER_EXTERNAL_ID, USER_EXTERNAL, USER_TYPE_EXTERNAL))
                    .union(buildAverageUser(USER_TEAM_ID, USER_TEAM, USER_TYPE_TEAM))
                    .union(buildAverageUser(USER_ASSIGNED_ID, USER_ASSIGNED, USER_TYPE_ASSIGNED))
                    .union(buildAverageUser(USER_FILTER_ID, USER_FILTER, USER_TYPE_FILTER));
        }

        return users;
    }

    private Select<Record3<Long, String, String>> buildAverageUser(Long userExternalId, String username, Field<String> userTypeField) {
        return parameters.getContext().select(
                val(userExternalId).as(USER._ID),
                val(username).as(USER.USERNAME),
                userTypeField
        );
    }

    public SelectOrderByStep<Record3<Long, String, String>> getAllUsers() {
        return getExternalUsers()
                .union(getTeamUsers())
                .union(getAssignedUsers())
                .union(getFilterUsers());
    }

    SelectConditionStep<Record3<Long, String, String>> getExternalUsers() {

        Set<String> usernameNotInSet = mergeSets(parameters.getTeamUsers(), parameters.getAssignedUsers(), parameters.getFilterUsers());

        SelectOnConditionStep<Record3<Long, String, String>> select = parameters.getContext()
                .selectDistinct(USER._ID, USER.USERNAME, USER_TYPE_EXTERNAL)
                .from(USER)
                .innerJoin(EVENT)
                .on(USER._ID.eq(EVENT.AUTHOR_USER_ID));

        SelectConditionStep<Record3<Long, String, String>> where = select.where(trueCondition());
        if (!parameters.getRepositories().isEmpty()) {
            where = select
                    .innerJoin(REPOSITORY)
                    .on(REPOSITORY._ID.eq(EVENT.REPOSITORY_ID))
                    .where(REPOSITORY.NAME.in(parameters.getRepositories()));
        }

        where = where
                .and(USER.USERNAME.notIn(usernameNotInSet))
                .and(conditionalBetween(EVENT.DATE, parameters.getFrom(), parameters.getTo()));

        return where;
    }

    SelectConditionStep<Record3<Long, String, String>> getTeamUsers() {
        return getUsersWithUsername(
                parameters.getContext(),
                USER_TYPE_TEAM,
                parameters.getTeamUsers(),
                parameters.getAssignedUsers(),
                parameters.getFilterUsers()
        );
    }

    SelectConditionStep<Record3<Long, String, String>> getAssignedUsers() {
        return getUsersWithUsername(parameters.getContext(), USER_TYPE_ASSIGNED, parameters.getAssignedUsers(), parameters.getFilterUsers());
    }

    SelectConditionStep<Record3<Long, String, String>> getFilterUsers() {
        return getUsersWithUsername(parameters.getContext(), USER_TYPE_FILTER, parameters.getFilterUsers());
    }

    @SafeVarargs
    private final SelectConditionStep<Record3<Long, String, String>> getUsersWithUsername(DSLContext create,
                                                                                          Field<String> userTypeField,
                                                                                          Set<String> usernameInSet,
                                                                                          Set<String>... usernameNotInSets) {

        Set<String> usernameNotInSet = mergeSets(usernameNotInSets);

        SelectConditionStep<Record3<Long, String, String>> where = create
                .select(USER._ID, USER.USERNAME, userTypeField)
                .from(USER)
                .where(USER.USERNAME.notIn(usernameNotInSet));

        if (!usernameInSet.isEmpty()) {
            where = where.and(USER.USERNAME.in(usernameInSet));
        }

        return where;
    }

    @SafeVarargs
    private final Set<String> mergeSets(Set<String>... sets) {
        Set<String> mergedSet = new HashSet<>();
        List<Set<String>> setList = Arrays.asList(sets);
        setList.forEach(mergedSet::addAll);
        return mergedSet;
    }

}
