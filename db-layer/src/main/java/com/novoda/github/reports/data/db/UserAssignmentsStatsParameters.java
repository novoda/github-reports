package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.model.UserAssignments;
import org.jooq.DSLContext;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserAssignmentsStatsParameters {

    private final Date from;
    private final Date to;
    private final Map<String, List<UserAssignments>> usersAssignments;
    private final DSLContext context;

    public UserAssignmentsStatsParameters(Date from,
                                          Date to,
                                          Map<String, List<UserAssignments>> usersAssignments,
                                          DSLContext context) {

        this.from = from;
        this.to = to;
        this.usersAssignments = usersAssignments;
        this.context = context;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public Map<String, List<UserAssignments>> getUsersAssignments() {
        return usersAssignments;
    }

    public DSLContext getContext() {
        return context;
    }
}
