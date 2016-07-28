package com.novoda.github.reports.web.hooks.convert;

import com.novoda.github.reports.web.hooks.model.GithubAction;

public class UnsupportedActionException extends Throwable {
    UnsupportedActionException(GithubAction action) {
        super("Unable to convert action: " + action.toString());
    }
}
