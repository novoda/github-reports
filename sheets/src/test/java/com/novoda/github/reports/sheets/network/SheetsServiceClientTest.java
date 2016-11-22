package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Content;
import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Feed;
import com.novoda.github.reports.sheets.sheet.Sheet;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

public class SheetsServiceClientTest {

    @Mock
    SheetsApiService mockSheetsApiService;

    private TestSubscriber<Entry> testSubscriber;

    private List<Entry> entries;

    private Observable<Response<Sheet>> apiObservable;

    private SheetsServiceClient sheetsServiceClient;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        testSubscriber = new TestSubscriber<>();

        entries = givenEntries();
        Response<Sheet> response = Response.success(givenASheetWith(entries));
        apiObservable = Observable.from(Collections.singletonList(response));

        sheetsServiceClient = new SheetsServiceClient(mockSheetsApiService);
    }

    @Test
    public void givenServiceReturnsDocument_whenQueryingForADocument_thenEachDocumentEntryIsEmitted() throws Exception {
        given(mockSheetsApiService.getDocument(anyString())).willReturn(apiObservable);

        sheetsServiceClient.getEntries()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(entries);
    }

    private Sheet givenASheetWith(List<Entry> entries) {
        Feed feed = new Feed(entries);
        return new Sheet(feed);
    }

    private List<Entry> givenEntries() {
        Content key = new Content("keyType", "keyValue");
        Content value = new Content("valueType", "valueValue");
        Entry entry = new Entry(key, value);
        return Collections.singletonList(entry);
    }

}
