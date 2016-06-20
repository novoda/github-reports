package com.novoda.github.reports.lambda.persistence;

import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.ConverterException;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

public class PersistOperator<T, R> implements Observable.Operator<Response<List<T>>, Response<List<T>>> {

    private final DataLayer<R> dataLayer;
    private final Converter<T, R> converter;

    public PersistOperator(DataLayer<R> dataLayer, Converter<T, R> converter) {
        this.dataLayer = dataLayer;
        this.converter = converter;
    }

    @Override
    public Subscriber<? super Response<List<T>>> call(Subscriber<? super Response<List<T>>> subscriber) {
        return new SafeSubscriber<>(new Subscriber<Response<List<T>>>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(Response<List<T>> response) {
                try {
                    List<T> elements = response.body();
                    dataLayer.updateOrInsert(converter.convertListFrom(elements));
                    subscriber.onNext(response);
                } catch (ConverterException | DataLayerException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
