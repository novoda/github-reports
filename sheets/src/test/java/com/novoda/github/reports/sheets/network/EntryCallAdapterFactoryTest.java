package com.novoda.github.reports.sheets.network;

import com.google.gson.reflect.TypeToken;
import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Sheet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class EntryCallAdapterFactoryTest {

    private EntryCallAdapterFactory callAdapterFactory;

    @Before
    public void setUp() {
        callAdapterFactory = new EntryCallAdapterFactory();
    }

    @Test
    public void givenANonObservableType_whenGettingACallAdapter_thenItIsNull() {

        CallAdapter actual = callAdapterFactory.get(Future.class, anyAnnotations(), anyRetrofit());

        assertThat(actual, nullValue());
    }

    @Test
    public void givenANonParameterizedType_whenGettingACallAdapter_thenItIsNull() {

        CallAdapter actual = callAdapterFactory.get(String.class, anyAnnotations(), anyRetrofit());

        assertThat(actual, nullValue());
    }

    @Test
    public void givenAParameterTypeThatIsNotEntry_whenGettingACallAdapter_thenItIsNull() {
        Type type = new TypeToken<Observable<Response<Sheet>>>() {}.getType();

        CallAdapter actual = callAdapterFactory.get(type, anyAnnotations(), anyRetrofit());

        assertThat(actual, nullValue());
    }

    @Test
    public void givenACorrectParameterType_whenGettingACallAdapter_thenItIsTheRightOne() throws Exception {
        Type type = new TypeToken<Observable<Entry>>() {}.getType();

        CallAdapter actual = callAdapterFactory.get(type, anyAnnotations(), givenRetrofit());

        assertThat(actual, instanceOf(EntryCallAdapter.class));
    }

    private Annotation[] anyAnnotations() {
        return new Annotation[0];
    }

    private Retrofit anyRetrofit() {
        return null;
    }

    private Retrofit givenRetrofit() {
        CallAdapter.Factory factory = mock(CallAdapter.Factory.class);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.novoda.com/")
                .addCallAdapterFactory(factory)
                .build();

        Type sheetType = new TypeToken<Observable<Response<Sheet>>>() {}.getType();
        given(factory.get(sheetType, anyAnnotations(), retrofit)).willReturn(mock(CallAdapter.class));
        return retrofit;
    }

}
