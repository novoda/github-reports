package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.UserStats;

import java.util.Date;

@FunctionalInterface
public interface UserDataLayer {

    UserStats getStats(String user, String repo, Date from, Date to);

}
