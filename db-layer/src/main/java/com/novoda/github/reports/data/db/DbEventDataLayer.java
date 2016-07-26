package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.builder.EventPullRequestQueryBuilder;
import com.novoda.github.reports.data.db.builder.EventUserAssignmentsQueryBuilder;
import com.novoda.github.reports.data.db.converter.AggregatedUserStatsConverter;
import com.novoda.github.reports.data.db.converter.PullRequestStatsConverter;
import com.novoda.github.reports.data.db.converter.UserAssignmentsStatsConverter;
import com.novoda.github.reports.data.db.tables.records.EventRecord;
import com.novoda.github.reports.data.model.*;
import org.jooq.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.novoda.github.reports.data.db.DatabaseHelper.dateToTimestamp;
import static com.novoda.github.reports.data.db.Tables.EVENT;
import static com.novoda.github.reports.data.db.builder.EventUserAssignmentsQueryBuilder.USERNAME_FIELD;

public class DbEventDataLayer extends DbDataLayer<Event, EventRecord> implements EventDataLayer {

    private final PullRequestStatsConverter pullRequestStatsConverter;
    private final UserAssignmentsStatsConverter usersAssignmentsConverter;
    private final AggregatedUserStatsConverter aggregatedUserStatsConverter;

    public static DbEventDataLayer newInstance(ConnectionManager connectionManager) {
        return new DbEventDataLayer(
                connectionManager,
                new PullRequestStatsConverter(),
                new UserAssignmentsStatsConverter(),
                new AggregatedUserStatsConverter()
        );
    }

    private DbEventDataLayer(ConnectionManager connectionManager,
                             PullRequestStatsConverter pullRequestStatsConverter,
                             UserAssignmentsStatsConverter userAssignmentsStatsConverter,
                             AggregatedUserStatsConverter aggregatedUserStatsConverter) {

        super(connectionManager);
        this.pullRequestStatsConverter = pullRequestStatsConverter;
        this.usersAssignmentsConverter = userAssignmentsStatsConverter;
        this.aggregatedUserStatsConverter = aggregatedUserStatsConverter;
    }

    @Override
    InsertOnDuplicateSetMoreStep<EventRecord> buildUpdateOrInsertListQuery(DSLContext create, Event element) {
        Timestamp date = dateToTimestamp(element.date());
        return create.insertInto(
                EVENT,
                EVENT._ID,
                EVENT.REPOSITORY_ID,
                EVENT.AUTHOR_USER_ID,
                EVENT.OWNER_USER_ID,
                EVENT.EVENT_TYPE_ID,
                EVENT.DATE
        )
                .values(
                        element.id(),
                        element.repositoryId(),
                        element.authorUserId(),
                        element.ownerUserId(),
                        element.eventType().getValue(),
                        date
                )
                .onDuplicateKeyUpdate()
                .set(EVENT.AUTHOR_USER_ID, element.authorUserId())
                .set(EVENT.OWNER_USER_ID, element.ownerUserId())
                .set(EVENT.EVENT_TYPE_ID, element.eventType().getValue())
                .set(EVENT.DATE, date);
    }

    @Override
    public PullRequestStats getStats(Date from,
                                     Date to,
                                     List<String> repositories,
                                     List<String> organisationUsers,
                                     List<String> assignedUsers,
                                     PullRequestStatsGroupBy groupBy,
                                     Boolean withAverage)
            throws DataLayerException {

        try {
            Connection connection = getNewConnection();
            DSLContext create = getNewDSLContext(connection);

            PullRequestStatsParameters parameters = new PullRequestStatsParameters(
                    create,
                    from,
                    to,
                    repositories,
                    organisationUsers,
                    assignedUsers,
                    groupBy,
                    withAverage
            );
            EventPullRequestQueryBuilder statsQueryBuilder = EventPullRequestQueryBuilder.newInstance(parameters);

            Map<String, ? extends Result<? extends Record>> groupedStats = statsQueryBuilder.getStats();

            return pullRequestStatsConverter.convert(groupedStats);

        } catch (SQLException e) {
            throw new DataLayerException(e);
        }

    }

    @Override
    public PullRequestStats getOrganisationStats(Date from,
                                                 Date to,
                                                 List<String> repositories,
                                                 List<String> organisationUsers,
                                                 PullRequestStatsGroupBy groupBy,
                                                 Boolean withAverage)
            throws DataLayerException {

        try {
            Connection connection = getNewConnection();
            DSLContext create = getNewDSLContext(connection);

            PullRequestStatsParameters parameters = new PullRequestStatsParameters(
                    create,
                    from,
                    to,
                    repositories,
                    organisationUsers,
                    groupBy,
                    withAverage
            );
            EventPullRequestQueryBuilder pullRequestStatsQueryBuilder = EventPullRequestQueryBuilder.newInstance(parameters);

            Map<String, ? extends Result<? extends Record>> groupedStats = pullRequestStatsQueryBuilder.getOrganisationStats();

            return pullRequestStatsConverter.convert(groupedStats);

        } catch (SQLException e) {
            throw new DataLayerException(e);
        }
    }

    @Override
    public UserAssignmentsStats getUserAssignmentsStats(Date from,
                                                        Date to,
                                                        Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException {

        try {
            Map<String, ? extends Result<? extends Record>> resultsGroupedByUsername =
                    getUserAssignmentsStatsQuery(from, to, usersAssignments)
                            .fetchGroups(USERNAME_FIELD);

            return usersAssignmentsConverter.convert(resultsGroupedByUsername);

        } catch (SQLException e) {
            throw new DataLayerException(e);
        }
    }

    @Override
    public AggregatedStats getAggregatedUserAssignmentsStats(Date from,
                                                             Date to,
                                                             Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException {

        try {
            Map<String, ? extends Result<? extends Record>> resultsGroupedByUsername =
                    getUserAssignmentsStatsQuery(from, to, usersAssignments)
                            .having(EVENT.EVENT_TYPE_ID.isNotNull())
                            .fetchGroups(USERNAME_FIELD);

            return aggregatedUserStatsConverter.convert(resultsGroupedByUsername);

        } catch (SQLException e) {
            throw new DataLayerException(e);
        }
    }

    private SelectHavingStep<? extends Record> getUserAssignmentsStatsQuery(Date from,
                                                                            Date to,
                                                                            Map<String, List<UserAssignments>> usersAssignments)
            throws SQLException, DataLayerException {

        Connection connection = getNewConnection();
        DSLContext create = getNewDSLContext(connection);

        UserAssignmentsStatsParameters parameters = new UserAssignmentsStatsParameters(from, to, usersAssignments, create);
        EventUserAssignmentsQueryBuilder userAssignmentsQueryBuilder = EventUserAssignmentsQueryBuilder
                .newInstance(parameters);

        return userAssignmentsQueryBuilder.getStats();
    }

}
