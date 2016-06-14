package com.novoda.github.reports.service.persistence;

import rx.Observable;

interface ComposedPersitTransformer<T> extends Observable.Transformer<T, T> {
}
