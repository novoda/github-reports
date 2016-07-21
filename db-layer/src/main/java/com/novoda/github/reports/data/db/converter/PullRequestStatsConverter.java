package com.novoda.github.reports.data.db.converter;

import com.novoda.github.reports.data.model.PullRequestStats;
import com.novoda.github.reports.data.model.PullRequestStatsGroup;
import com.novoda.github.reports.data.model.PullRequestStatsUser;
import org.jooq.Record;
import org.jooq.Result;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.novoda.github.reports.data.db.builder.EventPullRequestQueryBuilder.*;
import static com.novoda.github.reports.data.db.builder.EventUserQueryBuilder.*;

public class PullRequestStatsConverter {

    private static final PullRequestStatsUser NO_AVERAGE_USER = null;

    public PullRequestStats convert(Map<String, ? extends Result<? extends Record>> recordsMap) {

        List<PullRequestStatsGroup> groups = recordsMap.entrySet()
                .stream()
                .map(group -> convertGroup(group.getKey(), group.getValue()))
                .collect(Collectors.toList());

        return PullRequestStats.builder()
                .groups(groups)
                .build();
    }

    private PullRequestStatsGroup convertGroup(String groupName, Result<? extends Record> records) {

        if (records.isEmpty()) {
            return null;
        }

        List<PullRequestStatsUser> usersList = records.stream().map(this::convertUser).collect(Collectors.toList());

        List<PullRequestStatsUser> users = usersList.stream()
                .filter(user -> user.id() > 0)
                .collect(Collectors.toList());
        PullRequestStatsUser externalAverage = usersList.stream()
                .filter(user -> Objects.equals(user.id(), USER_EXTERNAL_ID))
                .findFirst()
                .orElse(NO_AVERAGE_USER);
        PullRequestStatsUser teamAverage = usersList.stream()
                .filter(user -> Objects.equals(user.id(), USER_ORGANISATION_ID))
                .findFirst()
                .orElse(NO_AVERAGE_USER);
        PullRequestStatsUser assignedAverage = usersList.stream()
                .filter(user -> Objects.equals(user.id(), USER_ASSIGNED_ID))
                .findFirst()
                .orElse(NO_AVERAGE_USER);

        return PullRequestStatsGroup.builder()
                .name(groupName)
                .users(users)
                .externalAverage(externalAverage)
                .organisationAverage(teamAverage)
                .assignedAverage(assignedAverage)
                .build();

    }

    private PullRequestStatsUser convertUser(Record record) {

        return PullRequestStatsUser.builder()
                .id(record.getValue(USER_ID_FIELD))
                .username(record.getValue(USER_NAME_FIELD))
                .type(convertUserType(record.getValue(USER_TYPE_FIELD)))
                .mergedPrs(record.getValue(MERGED_FIELD))
                .openedPrs(record.getValue(OPENED_FIELD))
                .otherPeopleCommentsOnUserPrs(record.getValue(OTHER_PEOPLE_COMMENTS_FIELD))
                .userCommentsOnOtherPeoplePrs(record.getValue(COMMENTS_OTHER_PEOPLE_FIELD))
                .commentsOnOwnPrs(record.getValue(COMMENTS_OWN_FIELD))
                .commentsOnAllPrs(record.getValue(COMMENTS_ANY_FIELD))
                .averageOtherPeopleCommentsOnUserPrs(record.getValue(AVG_OTHER_PEOPLE_COMMENTS_FIELD))
                .averageUserCommentsOnMergedPrs(record.getValue(AVG_COMMENTS_OTHER_PEOPLE_FIELD))
                .build();
    }

    private PullRequestStatsUser.UserType convertUserType(String userType) {
        if (USER_EXTERNAL.equals(userType)) {
            return PullRequestStatsUser.UserType.EXTERNAL;
        }
        if (USER_ORGANISATION.equals(userType)) {
            return PullRequestStatsUser.UserType.ORGANISATION;
        }
        if (USER_ASSIGNED.equals(userType)) {
            return PullRequestStatsUser.UserType.ASSIGNED;
        }

        throw new IllegalArgumentException(String.format("No such user type \"%s\".", userType));
    }

}
