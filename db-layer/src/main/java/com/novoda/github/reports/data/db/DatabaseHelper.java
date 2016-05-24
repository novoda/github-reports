package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.TableField;

import static com.novoda.github.reports.data.db.Tables.EVENT;
import static com.novoda.github.reports.data.db.Tables.REPOSITORY;

class DatabaseHelper {

    static final String EVENTS_COUNT = "events_count";
    static final String PEOPLE_COUNT = "people_count";
    static final String REPOSITORIES_COUNT = "repositories_count";
    static final Condition REPOSITORY_ON_CONDITION = EVENT.REPOSITORY_ID.eq(REPOSITORY._ID);

    static final Integer OPENED_ISSUES_ID = 100;
    static final Integer OPENED_PRS_ID = 200;
    static final Integer COMMENTED_ISSUES_ID = 102;
    static final Integer COMMENTED_PRS_ID = 202;
    static final Integer MERGED_PRS_ID = 207;

    static Condition conditionalBetween(TableField<?, Timestamp> field, Date from, Date to) {
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

    static ProjectRepoStats recordsToProjectRepoStats(Result<Record2<Integer, Integer>> events, Result<Record1<Integer>> people, String projectOrRepoName) {
        BigInteger numberOfOpenedIssues = BigInteger.ZERO;
        BigInteger numberOfOpenedPullRequests = BigInteger.ZERO;
        BigInteger numberOfCommentedIssues = BigInteger.ZERO;
        BigInteger numberOfMergedPullRequests = BigInteger.ZERO;
        BigInteger numberOfOtherEvents = BigInteger.ZERO;

        for (Record2<Integer, Integer> record : events) {
            Integer key = record.get(EVENT.EVENT_TYPE_ID);
            Integer intValue = record.get(EVENTS_COUNT, Integer.class);
            BigInteger value = BigInteger.valueOf(intValue);
            if (key.equals(OPENED_ISSUES_ID)) {
                numberOfOpenedIssues = value;
            } else if (key.equals(OPENED_PRS_ID)) {
                numberOfOpenedPullRequests = value;
            } else if (key.equals(COMMENTED_ISSUES_ID) || key.equals(COMMENTED_PRS_ID)) {
                numberOfCommentedIssues = numberOfCommentedIssues.add(value);
            } else if (key.equals(MERGED_PRS_ID)) {
                numberOfMergedPullRequests = value;
            } else {
                numberOfOtherEvents = numberOfOtherEvents.add(value);
            }
        }

        BigInteger numberOfParticipatingUsers = BigInteger.ZERO;

        if (people != null && people.isNotEmpty()) {
            numberOfParticipatingUsers = people.get(0).get(PEOPLE_COUNT, BigInteger.class);
        }

        return new ProjectRepoStats(
                projectOrRepoName,
                numberOfOpenedIssues,
                numberOfOpenedPullRequests,
                numberOfCommentedIssues,
                numberOfMergedPullRequests,
                numberOfOtherEvents,
                numberOfParticipatingUsers
        );
    }
}
