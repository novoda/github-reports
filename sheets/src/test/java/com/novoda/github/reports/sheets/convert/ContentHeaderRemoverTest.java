package com.novoda.github.reports.sheets.convert;

import com.novoda.github.reports.sheets.sheet.Entry;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContentHeaderRemoverTest {

    private static final String HEADER = "content:";

    private final ContentHeaderRemover contentHeaderRemover = new ContentHeaderRemover();

    @Test
    public void givenAnEntryHavingContentWithHeader_whenRemoving_thenItIsRemoved() {
        Entry entry = new Entry("title", HEADER + " text");

        Entry cleanEntry = contentHeaderRemover.removeFrom(entry);

        assertThat(cleanEntry.getContent()).isEqualTo("text");
    }

    @Test
    public void givenAnEntryHavingContentWithHeaderWithExtraColon_whenRemoving_thenItIsRemoved() {
        Entry entry = new Entry("title", HEADER + " text: extra");

        Entry cleanEntry = contentHeaderRemover.removeFrom(entry);

        assertThat(cleanEntry.getContent()).isEqualTo("text: extra");
    }

    @Test
    public void givenAnEntryNotHavingHeader_whenRemoving_thenNothingIsRemoved() {
        Entry entry = new Entry("title", "text");

        Entry cleanEntry = contentHeaderRemover.removeFrom(entry);

        assertThat(cleanEntry.getContent()).isEqualTo("text");
    }

    @Test
    public void givenAnEntryHavingOnlyTheHeader_whenRemoving_thenEverythingIsRemoved() {
        Entry entry = new Entry("title", HEADER);

        Entry cleanEntry = contentHeaderRemover.removeFrom(entry);

        assertThat(cleanEntry.getContent()).isEqualTo("");
    }
}
