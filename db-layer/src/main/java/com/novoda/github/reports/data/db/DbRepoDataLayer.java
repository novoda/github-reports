package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.Select;

import static com.novoda.github.reports.data.db.Tables.EVENT;
import static com.novoda.github.reports.data.db.Tables.REPOSITORY;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.countDistinct;

public class DbRepoDataLayer implements RepoDataLayer {

    private final ConnectionFactory connectionFactory;

    public DbRepoDataLayer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ProjectRepoStats getStats(String repo, Date from, Date to) throws DataLayerException {
        Connection connection = null;
        Result<Record2<Integer, Integer>> eventsResult;
        Result<Record1<Integer>> peopleResult;

        try {
            connection = connectionFactory.getNewConnection();
            DSLContext create = connectionFactory.getNewDSLContext(connection);

            Condition betweenCondition = DatabaseHelper.conditionalBetween(EVENT.DATE, from, to);
            Condition repoCondition = REPOSITORY.NAME.equalIgnoreCase(repo);

            eventsResult = selectEvents(create, betweenCondition, repoCondition).fetch();
            peopleResult = selectPeople(create, betweenCondition, repoCondition).fetch();
        } catch (SQLException e) {
            throw new DataLayerException(e);
        } finally {
            connectionFactory.attemptCloseConnection(connection);
        }

        return DatabaseHelper.recordsToProjectRepoStats(eventsResult, peopleResult, repo);
    }

    private static Select<Record2<Integer, Integer>> selectEvents(DSLContext create, Condition betweenCondition, Condition repoCondition) {
        return create
                .select(EVENT.EVENT_TYPE_ID, count(EVENT.EVENT_TYPE_ID).as(DatabaseHelper.EVENTS_COUNT))
                .from(EVENT).innerJoin(REPOSITORY)
                .on(DatabaseHelper.REPOSITORY_ON_CONDITION)
                .where(betweenCondition)
                .and(repoCondition)
                .groupBy(EVENT.EVENT_TYPE_ID);
    }

    private static Select<Record1<Integer>> selectPeople(DSLContext create, Condition betweenCondition, Condition repoCondition) {
        return create
                .select(countDistinct(EVENT.AUTHOR_USER_ID).as(DatabaseHelper.PEOPLE_COUNT))
                .from(EVENT).innerJoin(REPOSITORY)
                .on(DatabaseHelper.REPOSITORY_ON_CONDITION)
                .where(betweenCondition)
                .and(repoCondition);
    }

}
