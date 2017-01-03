package com.novoda.github.reports.sheets.convert;

import com.novoda.github.reports.sheets.sheet.Entry;

// TODO @RUI test!
public class GithubRepositoryNameRemover implements ValueRemover<Entry> {

    private static final String TEXT_TO_REMOVE = "githubrepositoryname: ";

    // TODO @RUI consider default implementation of removeFrom() + textToRemove() mandatory implementation

    @Override
    public Entry removeFrom(Entry entry) {
        return new Entry(entry.getTitle(), entry.getContent().replace(TEXT_TO_REMOVE, ""));
    }
}
