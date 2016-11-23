package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.ValueRemover;
import com.novoda.github.reports.sheets.sheet.Entry;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SheetsServiceClientTest {

    @Mock
    SheetsApiService mockSheetsApiService;

    @Mock
    ValueRemover<Entry> mockValueRemover;

    private TestSubscriber<Entry> testSubscriber;

    private List<Entry> entries;

    private Observable<Entry> apiObservable;

    private SheetsServiceClient sheetsServiceClient;

    @Before
    public void setUp() {
        initMocks(this);

        when(mockValueRemover.removeFrom(any(Entry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        testSubscriber = new TestSubscriber<>();

        entries = givenEntries();
        apiObservable = Observable.from(entries);

        sheetsServiceClient = new SheetsServiceClient(mockSheetsApiService, mockValueRemover);
    }

    @Test
    public void givenServiceReturnsEntries_whenQueryingForEntries_thenEachDocumentEntryIsEmitted() {
        given(mockSheetsApiService.getEntries(anyString())).willReturn(apiObservable);

        sheetsServiceClient.getEntries()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(entries);
    }

    @Test
    public void givenServiceReturnsEntries_whenQueryingForEntries_thenValueRemoverIsAppliedToEachKey() {
        given(mockSheetsApiService.getEntries(anyString())).willReturn(apiObservable);

        sheetsServiceClient.getEntries()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        verify(mockValueRemover, times(entries.size())).removeFrom(any(Entry.class));
    }

    private List<Entry> givenEntries() {
        Entry entry = new Entry("key", "value");
        return Collections.singletonList(entry);
    }

}
