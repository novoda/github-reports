package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.TableField;

import static com.novoda.github.reports.data.db.ConnectionManager.*;
import static com.novoda.github.reports.data.db.Tables.EVENT;
import static com.novoda.github.reports.data.db.Tables.REPOSITORY;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.countDistinct;

public class DbRepoDataLayer implements RepoDataLayer {

    private static final String EVENTS_COUNT = "events_count";
    private static final String PEOPLE_COUNT = "people_count";
    private static final Condition ON_CONDITION = EVENT.REPOSITORY_ID.eq(REPOSITORY._ID);

    private static final Integer NUMBER_OPENED_ISSUES_ID = 100;
    private static final Integer NUMBER_OPENED_PRS_ID = 200;
    private static final Integer NUMBER_COMMENTED_ISSUES_ID = 102;
    private static final Integer NUMBER_COMMENTED_PRS_ID = 202;
    private static final Integer NUMBER_MERGED_PRS_ID = 207;

    @Override
    public ProjectRepoStats getStats(String repo, Date from, Date to) throws DataLayerException {
        Connection connection = null;
        Result<Record2<Integer, Integer>> eventsResult;
        Result<Record1<Integer>> peopleResult;

        try {
            connection = getNewConnection();
            DSLContext create = getNewDSLContext(connection);

            Condition betweenCondition = conditionalBetween(EVENT.DATE, from, to);
            Condition repoCondition = REPOSITORY.NAME.equalIgnoreCase(repo);

            eventsResult = create
                    .select(EVENT.EVENT_TYPE_ID, count(EVENT.EVENT_TYPE_ID).as(EVENTS_COUNT))
                    .from(EVENT).innerJoin(REPOSITORY)
                    .on(ON_CONDITION)
                    .where(betweenCondition)
                    .and(repoCondition)
                    .groupBy(EVENT.EVENT_TYPE_ID)
                    .fetch();

            peopleResult = create
                    .select(countDistinct(EVENT.AUTHOR_USER_ID).as(PEOPLE_COUNT))
                    .from(EVENT).innerJoin(REPOSITORY)
                    .on(ON_CONDITION)
                    .where(betweenCondition)
                    .and(repoCondition)
                    .fetch();

        } catch (SQLException e) {
            throw new DataLayerException(e);
        } finally {
            attemptCloseConnection(connection);
        }

        return recordsToStats(eventsResult, peopleResult, repo);
    }

    private static Condition conditionalBetween(TableField<?, Timestamp> field, Date from, Date to) {
        Condition condition = field.isNotNull();
        if (from != null) {
            Timestamp fromTimestamp = dateToTimestamp(from);
            condition = condition.and(field.greaterOrEqual(fromTimestamp));
        }
        if (to != null) {
            Timestamp toTimestamp = dateToTimestamp(to);
            condition = condition.and(field.lessOrEqual(toTimestamp));
        }
        return condition;
    }

    private static Timestamp dateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    private static ProjectRepoStats recordsToStats(Result<Record2<Integer, Integer>> events, Result<Record1<Integer>> people, String repo) {
        if (events == null && people == null) {
            return null;
        }

        BigDecimal numberOfOpenedIssues = BigDecimal.valueOf(0);
        BigDecimal numberOfOpenedPullRequests = BigDecimal.valueOf(0);
        BigDecimal numberOfCommentedIssues = BigDecimal.valueOf(0);
        BigDecimal numberOfMergedPullRequests = BigDecimal.valueOf(0);
        BigDecimal numberOfOtherEvents = BigDecimal.valueOf(0);

        for (Record2<Integer, Integer> record : events) {
            Integer key = record.get(EVENT.EVENT_TYPE_ID);
            BigDecimal value = BigDecimal.valueOf(record.get(EVENTS_COUNT, Integer.class));
            if (key.equals(NUMBER_OPENED_ISSUES_ID)) {
                numberOfOpenedIssues = value;
            } else if (key.equals(NUMBER_OPENED_PRS_ID)) {
                numberOfOpenedPullRequests = value;
            } else if (key.equals(NUMBER_COMMENTED_ISSUES_ID) || key.equals(NUMBER_COMMENTED_PRS_ID)) {
                numberOfCommentedIssues = value;
            } else if (key.equals(NUMBER_MERGED_PRS_ID)) {
                numberOfMergedPullRequests = value;
            } else {
                numberOfOtherEvents = numberOfOtherEvents.add(value);
            }
        }

        BigDecimal numberOfParticipatingUsers = BigDecimal.valueOf(0);

        if (people != null && people.isNotEmpty()) {
            numberOfParticipatingUsers = people.get(0).get(PEOPLE_COUNT, BigDecimal.class);
        }

        return new ProjectRepoStats(
                repo,
                numberOfOpenedIssues,
                numberOfOpenedPullRequests,
                numberOfCommentedIssues,
                numberOfMergedPullRequests,
                numberOfOtherEvents,
                numberOfParticipatingUsers
        );
    }
}
