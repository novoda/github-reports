package com.novoda.core.data;

import com.novoda.core.stats.UserStats;

import java.util.Date;

public interface UserDataLayer {
    UserStats getStats(String user, String repo, Date from, Date to);
}
