package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.tables.records.EventRecord;
import com.novoda.github.reports.data.model.DatabaseEvent;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;

import static com.novoda.github.reports.data.db.DatabaseHelper.dateToTimestamp;
import static com.novoda.github.reports.data.db.Tables.EVENT;

public class DbEventDataLayer extends DbDataLayer<DatabaseEvent, EventRecord> implements EventDataLayer {

    public static DbEventDataLayer newInstance(ConnectionManager connectionManager) {
        return new DbEventDataLayer(connectionManager);
    }

    private DbEventDataLayer(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    InsertOnDuplicateSetMoreStep<EventRecord> buildUpdateOrInsertListQuery(DSLContext create, DatabaseEvent element) {
        Timestamp date = dateToTimestamp(element.date());
        return create.insertInto(EVENT, EVENT._ID, EVENT.REPOSITORY_ID, EVENT.AUTHOR_USER_ID, EVENT.OWNER_USER_ID, EVENT.EVENT_TYPE_ID, EVENT.DATE)
                .values(element.id(), element.repositoryId(), element.authorUserId(), element.ownerUserId(), element.eventType().getValue(), date)
                .onDuplicateKeyUpdate()
                .set(EVENT.AUTHOR_USER_ID, element.authorUserId())
                .set(EVENT.OWNER_USER_ID, element.ownerUserId())
                .set(EVENT.EVENT_TYPE_ID, element.eventType().getValue())
                .set(EVENT.DATE, date);
    }
}
