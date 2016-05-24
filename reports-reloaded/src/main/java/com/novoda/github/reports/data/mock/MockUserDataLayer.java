package com.novoda.github.reports.data.mock;

import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.UserStats;

import java.math.BigDecimal;
import java.util.Date;

public class MockUserDataLayer implements UserDataLayer {
    @Override
    public UserStats getStats(String user, String repo, Date from, Date to) {
        return new UserStats(
                user,
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(123),
                BigDecimal.valueOf(896),
                BigDecimal.valueOf(65),
                BigDecimal.valueOf(437),
                BigDecimal.valueOf(7)
        );
    }
}
