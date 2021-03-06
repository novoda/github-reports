package com.novoda.github.reports.data.db.builder;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.db.UserAssignmentsStatsParameters;
import com.novoda.github.reports.data.model.UserAssignments;
import org.jooq.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.novoda.github.reports.data.db.DatabaseHelper.conditionalBetween;
import static com.novoda.github.reports.data.db.DatabaseHelper.dateToTimestamp;
import static com.novoda.github.reports.data.db.DatabaseHelper.findInSetForMySqlOnly;
import static com.novoda.github.reports.data.db.Tables.*;
import static org.jooq.impl.DSL.*;

public class EventUserAssignmentsQueryBuilder {

    public static final Field<Timestamp> DATE_FROM_FIELD = field("date_from", Timestamp.class);
    public static final Field<Timestamp> DATE_TO_FIELD = field("date_to", Timestamp.class);
    public static final Field<String> PROJECT_ASSIGNED_FIELD = field("project_assigned", String.class);
    public static final Field<String> REPOSITORIES_ASSIGNED_FIELD = field("repositories_assigned", String.class);
    public static final Field<String> USERNAME_FIELD = field("user_username", String.class);
    public static final Field<String> REPOSITORY_WORKED_NAME_FIELD = field("repository_worked_name", String.class);
    public static final Field<Integer> COUNT_EVENT_FIELD = field("count_event", Integer.class);

    private static final Field<Boolean> WAS_SCHEDULED_WORK_FIELD = field("was_scheduled_work", Boolean.class);

    private static final Integer NO_ELEMENT_FOUND_IN_SET = 0;

    private final UserAssignmentsStatsParameters parameters;

    public static EventUserAssignmentsQueryBuilder newInstance(UserAssignmentsStatsParameters parameters) {
        return new EventUserAssignmentsQueryBuilder(parameters);
    }

    private EventUserAssignmentsQueryBuilder(UserAssignmentsStatsParameters parameters) {
        this.parameters = parameters;
    }

    public SelectHavingStep<? extends Record> getStats() throws DataLayerException {

        try {
            SelectJoinStep<? extends Record> userAssignments = selectUserAssignments();
            SelectOnConditionStep<? extends Record> withUsers = leftJoinUser(userAssignments);
            SelectOnConditionStep<? extends Record> withUsersAndEvents = leftJoinEvent(withUsers);
            SelectOnConditionStep<? extends Record> withUsersAndEventsAndRepositories = leftJoinRepository(withUsersAndEvents);
            SelectHavingStep<? extends Record> query = groupByAll(withUsersAndEventsAndRepositories);

            return query;
        } catch (NoUserAssignmentsException e) {
            throw new DataLayerException(e);
        }
    }

    private SelectJoinStep<Record9<String, Timestamp, Timestamp, String, String, String, Integer, Boolean, Integer>> selectUserAssignments()
            throws NoUserAssignmentsException {

        return parameters.getContext()
                .select(
                        USERNAME_FIELD,
                        DATE_FROM_FIELD,
                        DATE_TO_FIELD,
                        PROJECT_ASSIGNED_FIELD,
                        REPOSITORIES_ASSIGNED_FIELD,
                        REPOSITORY.NAME.as(REPOSITORY_WORKED_NAME_FIELD),
                        EVENT.EVENT_TYPE_ID,
                        wasScheduledWork().as(WAS_SCHEDULED_WORK_FIELD),
                        count(EVENT.EVENT_TYPE_ID).as(COUNT_EVENT_FIELD)
                )
                .from(userAssignments());
    }

    private Field<Boolean> wasScheduledWork() {
        return when(
                findInSetForMySqlOnly(REPOSITORY.NAME, REPOSITORIES_ASSIGNED_FIELD).greaterThan(NO_ELEMENT_FOUND_IN_SET),
                true
        ).otherwise(false);
    }

    private SelectOrderByStep<Record5<Timestamp, Timestamp, String, String, String>> userAssignments()
            throws NoUserAssignmentsException {

        return parameters.getUsersAssignments()
                .entrySet()
                .stream()
                .flatMap(toUserAssignmentQueries())
                .collect(toUnion())
                .orElseThrow(NoUserAssignmentsException::new);
    }

    private Function<Map.Entry<String, List<UserAssignments>>, Stream<SelectSelectStep<Record5<Timestamp, Timestamp, String, String, String>>>> toUserAssignmentQueries() {
        return entry -> {
            String username = entry.getKey();
            return entry.getValue()
                    .stream()
                    .map(toUserAssignmentQuery(username));
        };
    }

    private Function<UserAssignments, SelectSelectStep<Record5<Timestamp, Timestamp, String, String, String>>> toUserAssignmentQuery(String username) {
        return userAssignment -> {
            String repositoriesSet = makeAssignedRepositoriesSet(userAssignment);
            Timestamp assignmentStartTimestamp = dateToTimestamp(userAssignment.assignmentStart());
            Timestamp assignmentEndTimestamp = dateToTimestamp(userAssignment.assignmentEnd());
            return parameters.getContext()
                    .select(
                            val(assignmentStartTimestamp).as(DATE_FROM_FIELD),
                            val(assignmentEndTimestamp).as(DATE_TO_FIELD),
                            val(userAssignment.assignedProject()).as(PROJECT_ASSIGNED_FIELD),
                            val(repositoriesSet).as(REPOSITORIES_ASSIGNED_FIELD),
                            val(username).as(USERNAME_FIELD)
                    );
        };
    }

    private String makeAssignedRepositoriesSet(UserAssignments userAssignment) {
        return userAssignment
                .assignedRepositories()
                .stream()
                .collect(Collectors.joining(","));
    }

    private Collector<
            SelectOrderByStep<Record5<Timestamp, Timestamp, String, String, String>>,
            ?,
            Optional<SelectOrderByStep<Record5<Timestamp, Timestamp, String, String, String>>>> toUnion() {

        return Collectors.reducing(SelectUnionStep::union);
    }

    private SelectOnConditionStep<? extends Record> leftJoinUser(SelectJoinStep<? extends Record> select) {
        return select
                .leftJoin(USER)
                .on(USERNAME_FIELD.eq(USER.USERNAME));
    }

    private SelectOnConditionStep<? extends Record> leftJoinEvent(SelectOnConditionStep<? extends Record> select) {
        return select
                .leftJoin(EVENT)
                .on(USER._ID.eq(EVENT.AUTHOR_USER_ID))
                .and(conditionalBetween(EVENT.DATE, DATE_FROM_FIELD, DATE_TO_FIELD));
    }

    private SelectOnConditionStep<? extends Record> leftJoinRepository(SelectOnConditionStep<? extends Record> select) {
        return select
                .leftJoin(REPOSITORY)
                .on(EVENT.REPOSITORY_ID.eq(REPOSITORY._ID));
    }

    private SelectHavingStep<? extends Record> groupByAll(SelectOnConditionStep<? extends Record> select) {
        return select
                .groupBy(
                        USERNAME_FIELD,
                        DATE_FROM_FIELD,
                        DATE_TO_FIELD,
                        PROJECT_ASSIGNED_FIELD,
                        REPOSITORIES_ASSIGNED_FIELD,
                        REPOSITORY_WORKED_NAME_FIELD,
                        EVENT.EVENT_TYPE_ID
                );
    }

}
