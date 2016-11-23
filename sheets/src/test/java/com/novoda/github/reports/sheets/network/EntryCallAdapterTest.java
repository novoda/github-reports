package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Feed;
import com.novoda.github.reports.sheets.sheet.Sheet;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class EntryCallAdapterTest {

    @Mock
    CallAdapter<Observable<Response<Sheet>>> mockDelegate;

    private TestSubscriber<Entry> testSubscriber;

    @InjectMocks
    private EntryCallAdapter entryCallAdapter;

    private List<Entry> entries;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        testSubscriber = new TestSubscriber<>();

        entries = Collections.singletonList(new Entry("title", "content"));
    }

    @Test
    public void givenADelegate_whenGettingTheMatchingResponseType_thenItIsTheDelegated() throws Exception {
        Type delegateType = mock(Type.class);
        given(mockDelegate.responseType()).willReturn(delegateType);

        Type actual = entryCallAdapter.responseType();

        assertThat(actual, equalTo(delegateType));
    }

    @Test
    public void givenAnEntryCall_whenAdaptingIt_thenEachEntryIsEmitted() throws Exception {
        givenDelegateExecutesCallSuccessfully();
        Call<Observable<Entry>> call = mock(Call.class);

        whenCallGetsAdapted(call);

        testSubscriber.assertReceivedOnNext(entries);
    }

    private void whenCallGetsAdapted(Call<Observable<Entry>> call) {
        Observable<Entry> observable = entryCallAdapter.adapt(call);
        observable
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);
    }

    private void givenDelegateExecutesCallSuccessfully() {
        Sheet sheet = new Sheet(new Feed(entries));
        Response<Sheet> response = Response.success(sheet);
        Observable<Response<Sheet>> observable = Observable.from(Collections.singletonList(response));
        Call anyMatchingCall = Matchers.<Call<Observable<Response<Sheet>>>>any();

        given(mockDelegate.adapt(anyMatchingCall)).willReturn(observable);
    }

}
