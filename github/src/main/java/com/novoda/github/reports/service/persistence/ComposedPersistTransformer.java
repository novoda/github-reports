package com.novoda.github.reports.service.persistence;

import rx.Observable;

interface ComposedPersistTransformer<T> extends Observable.Transformer<T, T> {
}
