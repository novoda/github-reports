package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.DataLayerException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.Query;
import org.jooq.Record;

abstract class DbDataLayer<T, D extends Record> implements DataLayer<T> {

    private final ConnectionManager connectionManager;

    DbDataLayer(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public T updateOrInsert(T element) throws DataLayerException {
        return updateOrInsert(Collections.singletonList(element)).get(0);
    }

    @Override
    public List<T> updateOrInsert(List<T> elements) throws DataLayerException {
        Connection connection = null;

        try {
            connection = connectionManager.getNewConnection();
            DSLContext create = connectionManager.getNewDSLContext(connection);

            List<? extends Query> queries = elements.stream()
                    .map(element -> buildUpdateOrInsertListQuery(create, element))
                    .collect(Collectors.toList());
            int[] results = create.batch(queries).execute();
            for (int result : results) {
                if (result <= 0) {
                    throw new SQLException("Could not update or insert the element.");
                }
                if (result > 1) {
                    throw new SQLException("More than 1 element was updated, check your DB constraints.");
                }
            }
        } catch (SQLException e) {
            throw new DataLayerException(e);
        } finally {
            connectionManager.attemptCloseConnection(connection);
        }

        return elements;
    }

    Connection getNewConnection() throws SQLException {
        return connectionManager.getNewConnection();
    }

    DSLContext getNewDSLContext(Connection connection) {
        return connectionManager.getNewDSLContext(connection);
    }

    void attemptCloseConnection(Connection connection) {
        connectionManager.attemptCloseConnection(connection);
    }

    abstract InsertOnDuplicateSetMoreStep<D> buildUpdateOrInsertListQuery(DSLContext create, T element);

}
