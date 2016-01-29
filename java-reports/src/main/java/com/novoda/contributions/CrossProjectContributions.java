package com.novoda.contributions;

import java.io.IOException;
import java.util.List;

/**
 * https://github.com/floatschedule/api
 */
public class CrossProjectContributions {

    public static void main(String[] args) throws IOException {
        String floatAccessToken = args[0];
        FloatContributionTracker floatContributionTracker = new FloatContributionTracker(floatAccessToken);
        // Input needed - Date Range
        List<CraftsmanWithTasks> result = floatContributionTracker.track("2016-01-11", "2016-01-18");
        System.out.println("Complete " + result);
    }


}

//October
//Ataul  The Times (1/10/16 - 12/10/16) , All4 (13/10/16 - 15/10/16), The Times (15/10/16 , 31/10/16)
//Paul    All4 (1/10/16 - 31/10/16)
//
//October
//The Times, All4, Oddschecker
//
//Paul has commented on?  The times(1/10/16 - 31/10/16) / Oddschecker(1/10/16 - 31/10/16)
//Ataul has commented on? , Oddschecker(1/10/16 - 31/10/16), All4 (1/10/16 - 12/10/16), The Times (13/10/16 - 15/10/16), All4 (15/10/16 , 31/10/16)