package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.db.tables.records.EventRecord;
import com.novoda.github.reports.data.model.EventStats;
import com.novoda.github.reports.data.model.ProjectRepoStats;
import org.jooq.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import static com.novoda.github.reports.data.db.Tables.EVENT;
import static com.novoda.github.reports.data.db.Tables.REPOSITORY;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.countDistinct;

class DatabaseHelper {

    static {
        LogHelper.turnOffJooqAd();
    }

    static final String EVENTS_COUNT = "events_count";
    static final String REPOSITORIES_COUNT = "repositories_count";
    private static final String PEOPLE_COUNT = "people_count";

    static final TableField<EventRecord, Integer> SELECT_EVENT_TYPE = EVENT.EVENT_TYPE_ID;
    static final Field<Integer> SELECT_PEOPLE_COUNT = countDistinct(EVENT.AUTHOR_USER_ID).as(PEOPLE_COUNT);
    static final Field<Integer> SELECT_EVENTS_COUNT = count(EVENT.EVENT_TYPE_ID).as(EVENTS_COUNT);
    static final Field<Integer> SELECT_REPOSITORIES_COUNT = countDistinct(EVENT.REPOSITORY_ID).as(REPOSITORIES_COUNT);

    static final Condition EVENT_REPOSITORY_JOIN_ON_CONDITION = EVENT.REPOSITORY_ID.eq(REPOSITORY._ID);

    static final Integer OPENED_ISSUES_ID = 100;
    static final Integer OPENED_PRS_ID = 200;
    static final Integer COMMENTED_ISSUES_ID = 102;
    static final Integer COMMENTED_PRS_ID = 202;
    static final Integer MERGED_PRS_ID = 205;

    private static final byte FALSE_BYTE = 0;
    private static final byte TRUE_BYTE = 1;

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

    static Timestamp dateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    static EventStats recordsToEventStats(Result<Record2<Integer, Integer>> events) {
        BigInteger numberOfOpenedIssues = BigInteger.ZERO;
        BigInteger numberOfOpenedPullRequests = BigInteger.ZERO;
        BigInteger numberOfCommentedIssues = BigInteger.ZERO;
        BigInteger numberOfMergedPullRequests = BigInteger.ZERO;
        BigInteger numberOfOtherEvents = BigInteger.ZERO;

        if (events != null) {
            for (Record2<Integer, Integer> record : events) {
                Integer key = record.get(EVENT.EVENT_TYPE_ID);
                BigInteger value = record.get(EVENTS_COUNT, BigInteger.class);
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
        }

        return new EventStats(
                numberOfOpenedIssues,
                numberOfOpenedPullRequests,
                numberOfCommentedIssues,
                numberOfMergedPullRequests,
                numberOfOtherEvents
        );
    }

    static ProjectRepoStats recordsToProjectRepoStats(Result<Record2<Integer, Integer>> events,
                                                      Result<Record1<Integer>> people,
                                                      String projectOrRepoName) {
        EventStats eventStats = recordsToEventStats(events);

        BigInteger numberOfParticipatingUsers = BigInteger.ZERO;

        if (people != null && people.isNotEmpty()) {
            numberOfParticipatingUsers = people.get(0).get(PEOPLE_COUNT, BigInteger.class);
        }

        return new ProjectRepoStats(
                projectOrRepoName,
                eventStats,
                numberOfParticipatingUsers
        );
    }

    static Byte boolToByte(boolean value) {
        return value ? TRUE_BYTE : FALSE_BYTE;
    }

    static boolean byteToBool(Byte value) {
        return TRUE_BYTE == value;
    }
}
