package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.data.model.UserStats;

import java.util.Date;

public interface UserDataLayer extends DataLayer<User> {

    UserStats getStats(String user, String repo, Date from, Date to) throws DataLayerException;

}
