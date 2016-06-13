package com.novoda.github.reports.service.persistence;

import rx.Observable;

interface Persister<T> extends Observable.Transformer<T, T> {

}
