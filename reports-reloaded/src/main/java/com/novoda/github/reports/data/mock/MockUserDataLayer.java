package com.novoda.github.reports.data.mock;

import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.UserStats;

import java.math.BigInteger;
import java.util.Date;

public class MockUserDataLayer implements UserDataLayer {
    @Override
    public UserStats getStats(String user, String repo, Date from, Date to) {
        return new UserStats(
                user,
                BigInteger.valueOf(43),
                BigInteger.valueOf(7),
                BigInteger.valueOf(28),
                BigInteger.valueOf(120),
                BigInteger.valueOf(12),
                BigInteger.valueOf(15),
                BigInteger.valueOf(2)
        );
    }
}
