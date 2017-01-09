package com.novoda.github.reports.sheets.convert;

import com.novoda.github.reports.sheets.sheet.Entry;

public class ContentHeaderRemover {
    public Entry removeFrom(Entry entry) {
        int colonIndex = entry.getContent().indexOf(':');
        String headerlessContent = entry.getContent().substring(colonIndex + 1).trim();
        return new Entry(entry.getTitle(), headerlessContent);
    }
}
