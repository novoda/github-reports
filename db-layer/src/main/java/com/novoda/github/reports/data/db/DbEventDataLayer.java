package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.builder.DbEventStatsQueryBuilder;
import com.novoda.github.reports.data.db.converter.PullRequestStatsConverter;
import com.novoda.github.reports.data.db.tables.records.EventRecord;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.PullRequestStats;
import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.novoda.github.reports.data.db.DatabaseHelper.dateToTimestamp;
import static com.novoda.github.reports.data.db.Tables.EVENT;

public class DbEventDataLayer extends DbDataLayer<Event, EventRecord> implements EventDataLayer {

    private final PullRequestStatsConverter converter;

    public static DbEventDataLayer newInstance(ConnectionManager connectionManager) {
        return new DbEventDataLayer(connectionManager);
    }

    private DbEventDataLayer(ConnectionManager connectionManager) {
        super(connectionManager);
        converter = new PullRequestStatsConverter();
    }

    @Override
    InsertOnDuplicateSetMoreStep<EventRecord> buildUpdateOrInsertListQuery(DSLContext create, Event element) {
        Timestamp date = dateToTimestamp(element.date());
        return create.insertInto(EVENT, EVENT._ID, EVENT.REPOSITORY_ID, EVENT.AUTHOR_USER_ID, EVENT.OWNER_USER_ID, EVENT.EVENT_TYPE_ID, EVENT.DATE)
                .values(element.id(), element.repositoryId(), element.authorUserId(), element.ownerUserId(), element.eventType().getValue(), date)
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
                                     boolean withAverage)
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
            DbEventStatsQueryBuilder statsQueryBuilder = DbEventStatsQueryBuilder.newInstance(parameters);

            Map<String, ? extends Result<? extends Record>> groupedStats = statsQueryBuilder.getStats();

            return converter.convert(groupedStats);

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
                                                 boolean withAverage)
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
            DbEventStatsQueryBuilder statsQueryBuilder = DbEventStatsQueryBuilder.newInstance(parameters);

            Map<String, ? extends Result<? extends Record>> groupedStats = statsQueryBuilder.getOrganisationStats();

            return converter.convert(groupedStats);

        } catch (SQLException e) {
            throw new DataLayerException(e);
        }
    }

}
