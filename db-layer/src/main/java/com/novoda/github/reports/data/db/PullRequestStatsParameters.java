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
    private static final String NULL_SELECTOR = null;

    private final DSLContext context;
    private final Date from;
    private final Date to;
    private final Set<String> repositories;
    private final Set<String> organisationUsers;
    private final Set<String> assignedUsers;
    private final Set<String> filterUsers;
    private final EventDataLayer.PullRequestStatsGroupBy groupBy;
    private final boolean withAverage;

    public PullRequestStatsParameters(DSLContext context,
                                      Date from,
                                      Date to,
                                      List<String> repositories,
                                      List<String> organisationUsers,
                                      List<String> assignedUsers,
                                      List<String> filterUsers,
                                      EventDataLayer.PullRequestStatsGroupBy groupBy,
                                      boolean withAverage) {

        this(
                context,
                from,
                to,
                listToSet(repositories),
                listToSet(organisationUsers),
                listToSet(assignedUsers),
                listToSet(filterUsers),
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
                                      boolean withAverage) {

        this(
                context,
                from,
                to,
                listToSet(repositories),
                listToSet(organisationUsers),
                Collections.emptySet(),
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
                                       Set<String> filterUsers,
                                       EventDataLayer.PullRequestStatsGroupBy groupBy,
                                       boolean withAverage) {

        this.context = context;
        this.from = from;
        this.to = to;
        this.repositories = repositories;
        this.organisationUsers = organisationUsers;
        this.assignedUsers = assignedUsers;
        this.filterUsers = filterUsers;
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

    public boolean hasOrganisationUsers() {
        return isSetWithValues(organisationUsers);
    }

    private boolean isSetWithValues(Set set) {
        return !(set == null || set.isEmpty());
    }

    public Set<String> getAssignedUsers() {
        return assignedUsers;
    }

    public boolean hasAssignedUsers() {
        return isSetWithValues(assignedUsers);
    }

    public Set<String> getFilterUsers() {
        return filterUsers;
    }

    public boolean hasFilterUsers() {
        return isSetWithValues(filterUsers);
    }

    public EventDataLayer.PullRequestStatsGroupBy getGroupBy() {
        return groupBy;
    }

    public boolean isWithAverage() {
        return withAverage;
    }

    public Field<String> getGroupFieldForMySQLOnly() {
        Field<String> groupField = year(EVENT.DATE).concat(GROUP_SELECTOR_SEPARATOR);

        if (groupBy == EventDataLayer.PullRequestStatsGroupBy.MONTH) {
            groupField = groupField.concat(month(EVENT.DATE));
        } else if (groupBy == EventDataLayer.PullRequestStatsGroupBy.WEEK) {
            groupField = groupField.concat(week(EVENT.DATE));
        } else {
            groupField = val(NULL_SELECTOR);
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
        return field("DATE_FORMAT({0}, \"%Y" + GROUP_SELECTOR_SEPARATOR + "%v\")", String.class, date);
    }
}
