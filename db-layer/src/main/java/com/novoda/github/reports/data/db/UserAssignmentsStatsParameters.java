package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.model.UserAssignments;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Map;

public class UserAssignmentsStatsParameters {

    private final Map<String, List<UserAssignments>> usersAssignments;
    private final DSLContext context;

    public UserAssignmentsStatsParameters(Map<String, List<UserAssignments>> usersAssignments, DSLContext context) {
        this.usersAssignments = usersAssignments;
        this.context = context;
    }

    public Map<String, List<UserAssignments>> getUsersAssignments() {
        return usersAssignments;
    }

    public DSLContext getContext() {
        return context;
    }
}
