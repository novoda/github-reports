package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.tables.records.EventRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableField;

import java.sql.Timestamp;
import java.util.*;

import static com.novoda.github.reports.data.db.Tables.EVENT;
import static org.jooq.impl.DSL.*;

public class PullRequestStatsParameters {

    public static final Field<String> GROUP_SELECTOR_FIELD = field("date_group", String.class);
    private static final String GROUP_SELECTOR_SEPARATOR = "-";
    private static final String ALL_TIME_SELECTOR = "ALL";

    private final DSLContext context;
    private final Date from;
    private final Date to;
    private final Set<String> repositories;
    private final Set<String> organisationUsers;
    private final Set<String> assignedUsers;
    private final EventDataLayer.PullRequestStatsGroupBy groupBy;
    private final Boolean withAverage;

    public PullRequestStatsParameters(DSLContext context,
                                      Date from,
                                      Date to,
                                      List<String> repositories,
                                      List<String> organisationUsers,
                                      List<String> assignedUsers,
                                      EventDataLayer.PullRequestStatsGroupBy groupBy,
                                      Boolean withAverage) {

        this(
                context,
                from,
                to,
                listToSet(repositories),
                listToSet(organisationUsers),
                listToSet(assignedUsers),
                groupBy,
                withAverage
        );
    }

    public PullRequestStatsParameters(DSLContext context,
                                      Date from,
                                      Date to,
                                      List<String> repositories,
                                      List<String> organisationUsers,
                                      EventDataLayer.PullRequestStatsGroupBy groupBy,
                                      Boolean withAverage) {

        this(
                context,
                from,
                to,
                listToSet(repositories),
                listToSet(organisationUsers),
                Collections.emptySet(),
                groupBy,
                withAverage
        );
    }

    private static Set<String> listToSet(List<String> list) {
        if (list == null) {
            return Collections.emptySet();
        }
        Set<String> set = new HashSet<>(list.size());
        set.addAll(list);
        return set;
    }

    private PullRequestStatsParameters(DSLContext context,
                                       Date from,
                                       Date to,
                                       Set<String> repositories,
                                       Set<String> organisationUsers,
                                       Set<String> assignedUsers,
                                       EventDataLayer.PullRequestStatsGroupBy groupBy,
                                       Boolean withAverage) {

        this.context = context;
        this.from = from;
        this.to = to;
        this.repositories = repositories;
        this.organisationUsers = organisationUsers;
        this.assignedUsers = assignedUsers;
        this.groupBy = groupBy;
        this.withAverage = withAverage;
    }

    public DSLContext getContext() {
        return context;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public Set<String> getRepositories() {
        return repositories;
    }

    public Set<String> getOrganisationUsers() {
        return organisationUsers;
    }

    public Set<String> getAssignedUsers() {
        return assignedUsers;
    }

    public EventDataLayer.PullRequestStatsGroupBy getGroupBy() {
        return groupBy;
    }

    public boolean isWithAverage() {
        return withAverage != null && withAverage;
    }

    public Field<String> getGroupFieldForMySqlOnly() {
        Field<String> groupField = year(EVENT.DATE).concat(GROUP_SELECTOR_SEPARATOR);

        if (groupBy == EventDataLayer.PullRequestStatsGroupBy.MONTH) {
            Field<String> month = cast(month(EVENT.DATE), String.class);
            Field<String> paddedMonth = leftPad(month);
            groupField = groupField.concat(paddedMonth);
        } else if (groupBy == EventDataLayer.PullRequestStatsGroupBy.WEEK) {
            Field<String> paddedWeek = leftPad(week(EVENT.DATE));
            groupField = groupField.concat(paddedWeek);
        } else {
            groupField = val(ALL_TIME_SELECTOR);
        }

        return groupField.as(GROUP_SELECTOR_FIELD);
    }

    /**
     * Formats a date field retrieving the week number in the date year (1-52).
     * This method is currently implemented for MySQL and may not work on other DBMSs.
     *
     * @param date The date field to format
     * @return A {@link String} representing the week number of the date field.
     */
    private Field<String> week(TableField<EventRecord, Timestamp> date) {
        return field("DATE_FORMAT({0}, \"%v\")", String.class, date);
    }

    private Field<String> leftPad(Field<String> field) {
        return lpad(field, 2, "0");
    }
}
