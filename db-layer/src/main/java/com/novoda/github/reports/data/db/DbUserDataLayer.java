package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.tables.records.UserRecord;
import com.novoda.github.reports.data.model.EventStats;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.data.model.UserStats;
import com.novoda.github.reports.util.StringHelper;

import java.math.BigInteger;
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
import static com.novoda.github.reports.data.db.Tables.*;

public class DbUserDataLayer extends DbDataLayer<User, UserRecord> implements UserDataLayer {

    private static final Condition USER_AUTHOR_ON_CONDITION = EVENT.AUTHOR_USER_ID.eq(USER._ID);
    private static final Condition USER_OWNER_ON_CONDITION = EVENT.OWNER_USER_ID.eq(USER._ID);

    public static DbUserDataLayer newInstance(ConnectionManager connectionManager) {
        return new DbUserDataLayer(connectionManager);
    }

    private DbUserDataLayer(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    InsertOnDuplicateSetMoreStep<UserRecord> buildUpdateOrInsertListQuery(DSLContext create, User element) {
        return create.insertInto(USER, USER._ID, USER.USERNAME)
                .values(element.id(), element.username())
                .onDuplicateKeyUpdate()
                .set(USER.USERNAME, element.username());
    }

    @Override
    public UserStats getStats(String user, String repo, Date from, Date to) throws DataLayerException {
        Connection connection = null;
        Result<Record2<Integer, Integer>> eventsResult;
        Result<Record1<Integer>> otherPeopleCommentsResult;
        Result<Record1<Integer>> repositoriesResult;

        try {
            connection = getConnectionManager().getNewConnection();
            DSLContext create = getConnectionManager().getNewDSLContext(connection);

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
            getConnectionManager().attemptCloseConnection(connection);
        }

        return recordsToUserStats(eventsResult, otherPeopleCommentsResult, repositoriesResult, user);
    }

    private static Select<Record2<Integer, Integer>> selectEvents(DSLContext create,
                                                                  Condition authorCondition,
                                                                  Condition betweenCondition,
                                                                  Condition repoCondition) {

        return create
                .select(SELECT_EVENT_TYPE, SELECT_EVENTS_COUNT)
                .from(EVENT)
                .innerJoin(REPOSITORY)
                .on(EVENT_REPOSITORY_JOIN_ON_CONDITION)
                .innerJoin(USER)
                .on(USER_AUTHOR_ON_CONDITION)
                .where(betweenCondition)
                .and(authorCondition)
                .and(repoCondition)
                .groupBy(EVENT.EVENT_TYPE_ID);
    }

    private static Select<Record1<Integer>> selectOtherPeopleComments(DSLContext create,
                                                                      Condition ownerCondition,
                                                                      Condition betweenCondition,
                                                                      Condition repoCondition) {
        return create
                .select(SELECT_EVENTS_COUNT)
                .from(EVENT)
                .innerJoin(REPOSITORY)
                .on(EVENT_REPOSITORY_JOIN_ON_CONDITION)
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

    private static Select<Record1<Integer>> selectRepositories(DSLContext create,
                                                               Condition authorCondition,
                                                               Condition betweenCondition,
                                                               Condition repoCondition) {
        return create
                .select(SELECT_REPOSITORIES_COUNT)
                .from(EVENT)
                .innerJoin(REPOSITORY)
                .on(EVENT_REPOSITORY_JOIN_ON_CONDITION)
                .innerJoin(USER)
                .on(USER_AUTHOR_ON_CONDITION)
                .where(betweenCondition)
                .and(authorCondition)
                .and(repoCondition);
    }

    UserStats recordsToUserStats(Result<Record2<Integer, Integer>> events,
                                 Result<Record1<Integer>> otherPeopleComments,
                                 Result<Record1<Integer>> repositories,
                                 String user) {
        EventStats eventStats = DatabaseHelper.recordsToEventStats(events);

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
                eventStats,
                numberOfOtherPeopleComments,
                numberOfRepositoriesWorkedOn
        );
    }
}
