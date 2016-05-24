package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.UserStats;
import com.novoda.github.reports.util.StringHelper;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.Select;

import static com.novoda.github.reports.data.db.DatabaseHelper.*;
import static com.novoda.github.reports.data.db.Tables.*;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.countDistinct;

public class DbUserDataLayer implements UserDataLayer {

    private static final Condition USER_AUTHOR_ON_CONDITION = EVENT.AUTHOR_USER_ID.eq(USER._ID);
    private static final Condition USER_OWNER_ON_CONDITION = EVENT.OWNER_USER_ID.eq(USER._ID);

    private final ConnectionFactory connectionFactory;

    public DbUserDataLayer(ConnectionFactory connectionFactory) {
       this.connectionFactory = connectionFactory;
    }

    @Override
    public UserStats getStats(String user, String repo, Date from, Date to) throws DataLayerException {
        Connection connection = null;
        Result<Record2<Integer, Integer>> eventsResult;
        Result<Record1<Integer>> otherPeopleCommentsResult;
        Result<Record1<Integer>> repositoriesResult;

        try {
            connection = connectionFactory.getNewConnection();
            DSLContext create = connectionFactory.getNewDSLContext(connection);

            Condition userCondition = USER.USERNAME.equalIgnoreCase(user);
            Condition betweenCondition = conditionalBetween(EVENT.DATE, from, to);
            Condition repoCondition = REPOSITORY.NAME.isNotNull();
            if (!StringHelper.isNullOrEmpty(repo)) {
                repoCondition = REPOSITORY.NAME.equalIgnoreCase(repo);
            }

            eventsResult = selectEvents(create, userCondition, betweenCondition, repoCondition).fetch();
            otherPeopleCommentsResult = selectOtherPeopleComments(create, userCondition, betweenCondition, repoCondition).fetch();
            repositoriesResult = selectRepositories(create, userCondition, betweenCondition, repoCondition).fetch();
        } catch (SQLException e) {
            throw new DataLayerException(e);
        } finally {
            connectionFactory.attemptCloseConnection(connection);
        }

        return recordsToUserStats(eventsResult, otherPeopleCommentsResult, repositoriesResult, user);
    }

    private static Select<Record2<Integer, Integer>> selectEvents(
            DSLContext create,
            Condition authorCondition,
            Condition betweenCondition,
            Condition repoCondition
    ) {
        return create
                .select(EVENT.EVENT_TYPE_ID, count(EVENT.EVENT_TYPE_ID).as(EVENTS_COUNT))
                .from(EVENT)
                .innerJoin(REPOSITORY)
                .on(DatabaseHelper.REPOSITORY_ON_CONDITION)
                .innerJoin(USER)
                .on(USER_AUTHOR_ON_CONDITION)
                .where(betweenCondition)
                .and(authorCondition)
                .and(repoCondition)
                .groupBy(EVENT.EVENT_TYPE_ID);
    }

    private static Select<Record1<Integer>> selectOtherPeopleComments(
            DSLContext create,
            Condition ownerCondition,
            Condition betweenCondition,
            Condition repoCondition
    ) {
        return create
                .select(count(EVENT.EVENT_TYPE_ID).as(EVENTS_COUNT))
                .from(EVENT)
                .innerJoin(REPOSITORY)
                .on(DatabaseHelper.REPOSITORY_ON_CONDITION)
                .innerJoin(USER)
                .on(USER_OWNER_ON_CONDITION)
                .where(betweenCondition)
                .and(repoCondition)
                .and(EVENT.EVENT_TYPE_ID.in(COMMENTED_ISSUES_ID, COMMENTED_PRS_ID))
                // user must be the owner of the event
                .and(ownerCondition)
                // but the owner and the author can't be the same (only retrieve other people's comments)
                .and(EVENT.AUTHOR_USER_ID.ne(EVENT.OWNER_USER_ID));
    }

    private static Select<Record1<Integer>> selectRepositories(
            DSLContext create,
            Condition authorCondition,
            Condition betweenCondition,
            Condition repoCondition
    ) {
        return create
                .select(countDistinct(EVENT.REPOSITORY_ID).as(REPOSITORIES_COUNT))
                .from(EVENT)
                .innerJoin(REPOSITORY)
                .on(REPOSITORY_ON_CONDITION)
                .innerJoin(USER)
                .on(USER_AUTHOR_ON_CONDITION)
                .where(betweenCondition)
                .and(authorCondition)
                .and(repoCondition);
    }

    private UserStats recordsToUserStats(
            Result<Record2<Integer, Integer>> events,
            Result<Record1<Integer>> otherPeopleComments,
            Result<Record1<Integer>> repositories,
            String user
    ) {
        BigInteger numberOfOpenedIssues = BigInteger.ZERO;
        BigInteger numberOfOpenedPullRequests = BigInteger.ZERO;
        BigInteger numberOfCommentedIssues = BigInteger.ZERO;
        BigInteger numberOfMergedPullRequests = BigInteger.ZERO;
        BigInteger numberOfOtherEvents = BigInteger.ZERO;

        for (Record2<Integer, Integer> record : events) {
            Integer key = record.get(EVENT.EVENT_TYPE_ID);
            BigInteger value = record.get(EVENTS_COUNT, BigInteger.class);
            if (key.equals(DatabaseHelper.OPENED_ISSUES_ID)) {
                numberOfOpenedIssues = value;
            } else if (key.equals(DatabaseHelper.OPENED_PRS_ID)) {
                numberOfOpenedPullRequests = value;
            } else if (key.equals(DatabaseHelper.COMMENTED_ISSUES_ID) || key.equals(DatabaseHelper.COMMENTED_PRS_ID)) {
                numberOfCommentedIssues = numberOfCommentedIssues.add(value);
            } else if (key.equals(DatabaseHelper.MERGED_PRS_ID)) {
                numberOfMergedPullRequests = value;
            } else {
                numberOfOtherEvents = numberOfOtherEvents.add(value);
            }
        }

        BigInteger numberOfOtherPeopleComments = BigInteger.ZERO;

        if (otherPeopleComments != null && otherPeopleComments.isNotEmpty()) {
            numberOfOtherPeopleComments = otherPeopleComments.get(0).get(EVENTS_COUNT, BigInteger.class);
        }

        BigInteger numberOfRepositoriesWorkedOn = BigInteger.ZERO;

        if (repositories != null && repositories.isNotEmpty()) {
            numberOfRepositoriesWorkedOn = repositories.get(0).get(REPOSITORIES_COUNT, BigInteger.class);
        }

        return new UserStats(
                user,
                numberOfOpenedIssues,
                numberOfOpenedPullRequests,
                numberOfCommentedIssues,
                numberOfOtherPeopleComments,
                numberOfMergedPullRequests,
                numberOfOtherEvents,
                numberOfRepositoriesWorkedOn
        );
    }
}
