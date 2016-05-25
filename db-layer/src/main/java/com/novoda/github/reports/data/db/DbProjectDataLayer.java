package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.ProjectDataLayer;
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

import static com.novoda.github.reports.data.db.DatabaseHelper.conditionalBetween;
import static com.novoda.github.reports.data.db.DatabaseHelper.recordsToProjectRepoStats;
import static com.novoda.github.reports.data.db.Tables.*;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.countDistinct;

public class DbProjectDataLayer implements ProjectDataLayer {

    private static final Condition PROJECT_REPOSITORY_ON_CONDITION = EVENT.REPOSITORY_ID.eq(PROJECT_REPOSITORY.REPOSITORY_ID);
    private static final Condition PROJECT_ON_CONDITION = PROJECT_REPOSITORY.PROJECT_ID.eq(PROJECT._ID);

    private final ConnectionFactory connectionFactory;

    public DbProjectDataLayer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ProjectRepoStats getStats(String project, Date from, Date to) throws DataLayerException {
        Connection connection = null;
        Result<Record2<Integer, Integer>> eventsResult;
        Result<Record1<Integer>> peopleResult;

        try {
            connection = connectionFactory.getNewConnection();
            DSLContext create = connectionFactory.getNewDSLContext(connection);

            Condition betweenCondition = conditionalBetween(EVENT.DATE, from, to);
            Condition projectCondition = PROJECT.NAME.equalIgnoreCase(project);

            eventsResult = selectEvents(create, betweenCondition, projectCondition).fetch();
            peopleResult = selectPeople(create, betweenCondition, projectCondition).fetch();
        } catch (SQLException e) {
            throw new DataLayerException(e);
        } finally {
            connectionFactory.attemptCloseConnection(connection);
        }

        return DatabaseHelper.recordsToProjectRepoStats(eventsResult, peopleResult, project);
    }

    private static Select<Record2<Integer, Integer>> selectEvents(DSLContext create, Condition betweenCondition, Condition projectCondition) {
        return create
                .select(EVENT.EVENT_TYPE_ID, count(EVENT.EVENT_TYPE_ID).as(DatabaseHelper.EVENTS_COUNT))
                .from(EVENT)
                .innerJoin(PROJECT_REPOSITORY)
                .on(PROJECT_REPOSITORY_ON_CONDITION)
                .innerJoin(PROJECT)
                .on(PROJECT_ON_CONDITION)
                .where(betweenCondition)
                .and(projectCondition)
                .groupBy(EVENT.EVENT_TYPE_ID);
    }

    private static Select<Record1<Integer>> selectPeople(DSLContext create, Condition betweenCondition, Condition projectCondition) {
        return create
                .select(countDistinct(EVENT.AUTHOR_USER_ID).as(DatabaseHelper.PEOPLE_COUNT))
                .from(EVENT)
                .innerJoin(PROJECT_REPOSITORY)
                .on(PROJECT_REPOSITORY_ON_CONDITION)
                .innerJoin(PROJECT)
                .on(PROJECT_ON_CONDITION)
                .where(betweenCondition)
                .and(projectCondition);
    }
}
