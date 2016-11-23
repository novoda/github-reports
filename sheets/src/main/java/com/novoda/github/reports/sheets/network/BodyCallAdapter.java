package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Sheet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Func1;

class BodyCallAdapter implements CallAdapter<Observable<Entry>> {

    private final CallAdapter.Factory factoryToSkip;
    private final Type responseType;
    private final Annotation[] annotations;
    private final Retrofit retrofit;

    BodyCallAdapter(CallAdapter.Factory factoryToSkip, Type responseType, Annotation[] annotations, Retrofit retrofit) {
        this.factoryToSkip = factoryToSkip;
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
        CallAdapter<Observable<Sheet>> delegate = getDelegateCallAdapter();
        return delegate.adapt(call)
                .flatMap(toEntries());
    }

    private Func1<Sheet, Observable<Entry>> toEntries() {
        return sheet -> Observable.from(sheet.getFeed().getEntries());
    }

    @SuppressWarnings("unchecked") // we're forced to cast due to having to implement <R> T adapt(Call<R> call)
    private CallAdapter<Observable<Sheet>> getDelegateCallAdapter() {
        return (CallAdapter<Observable<Sheet>>) retrofit.nextCallAdapter(factoryToSkip, responseType, annotations);
    }

}
