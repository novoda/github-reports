package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.properties.DocumentIdReader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserSheetsServiceClientTest {

    private static final String ANY_DOCUMENT_ID = "1rMeGnlugO312to0loBwN3x0QTvAxoHwv4Pe_SYXR1YE";

    @Mock
    SheetsServiceClient mockSheetsServiceClient;

    @Mock
    DocumentIdReader mockDocumentIdReader;

    @InjectMocks
    private UserSheetsServiceClient userSheetsServiceClient;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(mockDocumentIdReader.getUsersDocumentId()).thenReturn(ANY_DOCUMENT_ID);
    }

    @Test
    public void givenServiceReturnsEntries_whenQueryingForEntries_thenDocumentIdReaderIsUsedToGetTheId() {
        given(mockSheetsServiceClient.getEntries(ANY_DOCUMENT_ID)).willReturn(Observable.empty());

        userSheetsServiceClient.getUserEntries()
                .subscribeOn(Schedulers.immediate())
                .subscribe(new TestSubscriber<>());

        verify(mockDocumentIdReader).getUsersDocumentId();
    }

}
