package com.novoda.github.reports.sheets.network;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;

class ResponseCallAdapter implements CallAdapter<Observable<?>> {

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
    public <R> Observable<Response<R>> adapt(final Call<R> call) {
        CallAdapter<Observable<Response<R>>> delegate = (CallAdapter<Observable<Response<R>>>) retrofit.nextCallAdapter(factory, responseType, annotations);
        return delegate.adapt(call);
    }
}
