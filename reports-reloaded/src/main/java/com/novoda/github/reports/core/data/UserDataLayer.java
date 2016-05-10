package com.novoda.github.reports.core.data;

import com.novoda.github.reports.core.stats.UserStats;

import java.util.Date;

public interface UserDataLayer {
    UserStats getStats(String user, String repo, Date from, Date to);
}
