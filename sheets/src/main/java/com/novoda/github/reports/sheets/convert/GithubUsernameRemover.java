package com.novoda.github.reports.sheets.convert;

import com.novoda.github.reports.sheets.sheet.Entry;

public class GithubUsernameRemover implements ValueRemover<Entry> {

    private static final String TEXT_TO_REMOVE = "githubusername: ";

    @Override
    public Entry removeFrom(Entry entry) {
        return new Entry(entry.getTitle(), entry.getContent().replace(TEXT_TO_REMOVE, ""));
    }
}
