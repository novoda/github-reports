package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.web.hooks.model.GithubAction;

class UnsupportedActionException extends Exception {
    UnsupportedActionException(GithubAction action) {
        super("Unable to convert action: " + action.toString());
    }
}
