package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Sheet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;

class ResponseCallAdapter implements CallAdapter<Observable<Entry>> {

    private final CallAdapter.Factory factory;
    private final Type responseType;
    private final Annotation[] annotations;
    private final Retrofit retrofit;

    ResponseCallAdapter(CallAdapter.Factory factory, Type responseType, Annotation[] annotations, Retrofit retrofit) {
        this.factory = factory;
        this.responseType = responseType;
        this.annotations = annotations;
        this.retrofit = retrofit;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public <R> Observable<Entry> adapt(final Call<R> call) {
        CallAdapter<Observable<Response<Sheet>>> delegate = getDelegateCallAdapter();
        return delegate.adapt(call)
                .flatMap(sheetResponse -> Observable.from(sheetResponse.body().getFeed().getEntries()));
    }

    @SuppressWarnings("unchecked") // we're forced to cast due to having to implement <R> T adapt(Call<R> call)
    private CallAdapter<Observable<Response<Sheet>>> getDelegateCallAdapter() {
        return (CallAdapter<Observable<Response<Sheet>>>) retrofit.nextCallAdapter(factory, responseType, annotations);
    }
}
