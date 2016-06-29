package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.builder.DbEventMergedCountQueryBuilder;
import com.novoda.github.reports.data.db.builder.DbEventUserQueryBuilder;
import com.novoda.github.reports.data.db.tables.records.EventRecord;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.PullRequestStats;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.Record4;
import org.jooq.SelectOrderByStep;
import org.jooq.conf.ParamType;

import static com.novoda.github.reports.data.db.DatabaseHelper.dateToTimestamp;
import static com.novoda.github.reports.data.db.Tables.EVENT;

public class DbEventDataLayer extends DbDataLayer<Event, EventRecord> implements EventDataLayer {

    public static DbEventDataLayer newInstance(ConnectionManager connectionManager) {
        return new DbEventDataLayer(connectionManager);
    }

    private DbEventDataLayer(ConnectionManager connectionManager) {
        super(connectionManager);
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
                                     List<String> teamUsers,
                                     List<String> assignedUsers,
                                     List<String> filterUsers,
                                     PullRequestStatsGroupBy groupBy,
                                     boolean withAverage) throws DataLayerException {

        try {
            Connection connection = getNewConnection();
            DSLContext create = getNewDSLContext(connection);

            PullRequestStatsParameters parameters = new PullRequestStatsParameters(
                    create,
                    from,
                    to,
                    repositories,
                    teamUsers,
                    assignedUsers,
                    filterUsers,
                    groupBy,
                    withAverage
            );

            DbEventUserQueryBuilder userQueryBuilder = new DbEventUserQueryBuilder(parameters);
            DbEventMergedCountQueryBuilder mergedCountQueryBuilder = new DbEventMergedCountQueryBuilder(parameters, userQueryBuilder);

            SelectOrderByStep<Record4<BigDecimal, Long, String, String>> mergedQuery = mergedCountQueryBuilder.getStats();

            // TODO: remove after getting all stats, this is only needed for debug purposes
            String mergedSql = mergedQuery.getSQL(ParamType.INLINED);
            System.out.println(mergedSql);

        } catch (SQLException e) {
            throw new DataLayerException(e);
        }

        return null;
    }

}
