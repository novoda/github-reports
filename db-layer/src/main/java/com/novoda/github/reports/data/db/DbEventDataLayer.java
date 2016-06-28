package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.tables.records.EventRecord;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.PullRequestStats;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;

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
                                     List<String> projectUsers,
                                     List<String> users,
                                     PullRequestStatsGroupBy groupBy,
                                     boolean withAverage) {

        // TODO: implement method
        return null;
    }
}
