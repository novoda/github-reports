package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.tables.records.EventRecord;
import com.novoda.github.reports.data.model.Event;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;

import static com.novoda.github.reports.data.db.DatabaseHelper.dateToTimestamp;
import static com.novoda.github.reports.data.db.Tables.EVENT;

public class DbEventDataLayer implements EventDataLayer {

    private final ConnectionManager connectionManager;

    public static DbEventDataLayer newInstance(ConnectionManager connectionManager) {
        return new DbEventDataLayer(connectionManager);
    }

    private DbEventDataLayer(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Event updateOrInsert(Event event) throws DataLayerException {
        return DatabaseHelper.updateOrInsert(this::updateOrInsert, connectionManager, event);
    }

    private InsertOnDuplicateSetMoreStep<EventRecord> updateOrInsert(DSLContext create, Event event) {
        Timestamp date = dateToTimestamp(event.date());
        return create.insertInto(EVENT, EVENT._ID, EVENT.REPOSITORY_ID, EVENT.AUTHOR_USER_ID, EVENT.OWNER_USER_ID, EVENT.EVENT_TYPE_ID, EVENT.DATE)
                .values(event.id(), event.repositoryId(), event.authorUserId(), event.ownerUserId(), event.eventType().getValue(), date)
                .onDuplicateKeyUpdate()
                .set(EVENT.AUTHOR_USER_ID, event.authorUserId())
                .set(EVENT.OWNER_USER_ID, event.ownerUserId())
                .set(EVENT.EVENT_TYPE_ID, event.eventType().getValue())
                .set(EVENT.DATE, date);
    }
}
