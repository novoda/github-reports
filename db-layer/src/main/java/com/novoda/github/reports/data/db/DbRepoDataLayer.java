package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.db.tables.records.RepositoryRecord;
import com.novoda.github.reports.data.model.ProjectRepoStats;
import com.novoda.github.reports.data.model.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.Select;

import static com.novoda.github.reports.data.db.DatabaseHelper.*;
import static com.novoda.github.reports.data.db.Tables.EVENT;
import static com.novoda.github.reports.data.db.Tables.REPOSITORY;

public class DbRepoDataLayer extends DbDataLayer<Repository, RepositoryRecord> implements RepoDataLayer {

    public static DbRepoDataLayer newInstance(ConnectionManager connectionManager) {
        return new DbRepoDataLayer(connectionManager);
    }

    private DbRepoDataLayer(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    InsertOnDuplicateSetMoreStep<RepositoryRecord> buildUpdateOrInsertListQuery(DSLContext create, Repository element) {
        Byte isPrivate = boolToByte(element.isPrivate());
        return create.insertInto(REPOSITORY, REPOSITORY._ID, REPOSITORY.NAME, REPOSITORY.PRIVATE)
                .values(element.id(), element.name(), isPrivate)
                .onDuplicateKeyUpdate()
                .set(REPOSITORY.NAME, element.name())
                .set(REPOSITORY.PRIVATE, isPrivate);
    }

    @Override
    public ProjectRepoStats getStats(String repo, Date from, Date to) throws DataLayerException {
        Connection connection = null;
        Result<Record2<Integer, Integer>> eventsResult;
        Result<Record1<Integer>> peopleResult;

        try {
            connection = getNewConnection();
            DSLContext create = getNewDSLContext(connection);

            Condition betweenCondition = DatabaseHelper.conditionalBetween(EVENT.DATE, from, to);
            Condition repoCondition = REPOSITORY.NAME.equalIgnoreCase(repo);

            eventsResult = selectEvents(create, betweenCondition, repoCondition).fetch();
            peopleResult = selectPeople(create, betweenCondition, repoCondition).fetch();
        } catch (SQLException e) {
            throw new DataLayerException(e);
        } finally {
            attemptCloseConnection(connection);
        }

        return DatabaseHelper.recordsToProjectRepoStats(eventsResult, peopleResult, repo);
    }

    private static Select<Record2<Integer, Integer>> selectEvents(DSLContext create, Condition betweenCondition, Condition repoCondition) {
        return create
                .select(SELECT_EVENT_TYPE, SELECT_EVENTS_COUNT)
                .from(EVENT)
                .innerJoin(REPOSITORY)
                .on(EVENT_REPOSITORY_JOIN_ON_CONDITION)
                .where(betweenCondition)
                .and(repoCondition)
                .groupBy(EVENT.EVENT_TYPE_ID);
    }

    private static Select<Record1<Integer>> selectPeople(DSLContext create, Condition betweenCondition, Condition repoCondition) {
        return create
                .select(SELECT_PEOPLE_COUNT)
                .from(EVENT)
                .innerJoin(REPOSITORY)
                .on(EVENT_REPOSITORY_JOIN_ON_CONDITION)
                .where(betweenCondition)
                .and(repoCondition);
    }

}
