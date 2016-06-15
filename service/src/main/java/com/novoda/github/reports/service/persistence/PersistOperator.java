package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.DataLayerException;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

class PersistOperator<T, R> implements Observable.Operator<List<T>, List<T>> {

    private final DataLayer<R> dataLayer;
    private final Converter<T, R> converter;

    PersistOperator(DataLayer<R> dataLayer,
                    Converter<T, R> converter) {
        this.dataLayer = dataLayer;
        this.converter = converter;
    }

    @Override
    public Subscriber<? super List<T>> call(Subscriber<? super List<T>> subscriber) {
        return new SafeSubscriber<>(new Subscriber<List<T>>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(List<T> elements) {
                try {
                    dataLayer.updateOrInsert(converter.convertListFrom(elements));
                    subscriber.onNext(elements);
                } catch (ConverterException | DataLayerException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
