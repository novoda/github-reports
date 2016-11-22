package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Sheet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import rx.Observable;

class BodyCallAdapter implements CallAdapter<Observable<Entry>> {

    private final CallAdapter.Factory factory;
    private final Type responseType;
    private final Annotation[] annotations;
    private final Retrofit retrofit;

    BodyCallAdapter(CallAdapter.Factory factory, Type responseType, Annotation[] annotations, Retrofit retrofit) {
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
        CallAdapter<Observable<Sheet>> delegate = (CallAdapter<Observable<Sheet>>) retrofit.nextCallAdapter(factory, responseType, annotations);
        return delegate.adapt(call)
                .flatMap(sheet -> Observable.from(sheet.getFeed().getEntries()));
    }

}
