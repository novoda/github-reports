package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.web.hooks.model.GithubAction;

public class UnsupportedActionException extends Throwable {
    UnsupportedActionException(GithubAction action) {
        super("Unable to convert action: " + action.toString());
    }
}
