package com.novoda.github.reports.batch.aws;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func0;

public class ResponsePersistTransformer<T> implements Observable.Transformer<Response<List<T>>, Response<List<T>>> {

    private final Observable.Transformer<T, T> persistTransformer;

    public ResponsePersistTransformer(Observable.Transformer<T, T> persistTransformer) {
        this.persistTransformer = persistTransformer;
    }

    @Override
    public Observable<Response<List<T>>> call(Observable<Response<List<T>>> responseObservable) {
        return responseObservable.flatMap(
                listResponse -> Observable
                        .from(listResponse.body())
                        .compose(persistTransformer)
                        .collect((Func0<List<T>>) ArrayList::new, List::add)
                        .map(list -> Response.success(list, listResponse.headers())));
    }

}
