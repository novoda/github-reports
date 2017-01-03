package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.ValueRemover;
import com.novoda.github.reports.sheets.sheet.Entry;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SheetsServiceClientTest {

    private static final String ANY_DOCUMENT_ID = "1rMeGnlugO312to0loBwN3x0QTvAxoHwv4Pe_SYXR1YE";

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

        sheetsServiceClient.getEntries(ANY_DOCUMENT_ID)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(entries);
    }

    @Test
    public void givenServiceReturnsEntries_whenQueryingForEntries_thenValueRemoverIsAppliedToEachKey() {
        given(mockSheetsApiService.getEntries(anyString())).willReturn(apiObservable);

        sheetsServiceClient.getEntries(ANY_DOCUMENT_ID)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        verify(mockValueRemover, times(entries.size())).removeFrom(any(Entry.class));
    }

    @Test
    public void givenServiceReturnsEntries_whenQueryingForEntries_thenTheRightIdIsUsed() {
        given(mockSheetsApiService.getEntries(anyString())).willReturn(apiObservable);

        sheetsServiceClient.getEntries(ANY_DOCUMENT_ID)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        assertThatTheRightIdIsUsed();
    }

    private List<Entry> givenEntries() {
        Entry entry = new Entry("key", "value");
        return Collections.singletonList(entry);
    }

    private void assertThatTheRightIdIsUsed() {
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSheetsApiService).getEntries(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(ANY_DOCUMENT_ID);
    }

}
