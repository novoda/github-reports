package com.novoda.github.reports.batch.network;

import java.util.List;
import java.util.Optional;

import retrofit2.Response;
import rx.Observable;

public class PagedTransformer<T> implements Observable.Transformer<Response<List<T>>, Response<List<T>>> {

    private final NextPageExtractor nextPageExtractor;
    private final PageRecursor<T> recursor;

    public static <T> PagedTransformer<T> newInstance(PageRecursor<T> recursor) {
        NextPageExtractor nextPageExtractor = new NextPageExtractor();
        return new PagedTransformer<>(nextPageExtractor, recursor);
    }

    private PagedTransformer(NextPageExtractor nextPageExtractor, PageRecursor<T> recursor) {
        this.nextPageExtractor = nextPageExtractor;
        this.recursor = recursor;
    }

    @Override
    public Observable<Response<List<T>>> call(Observable<Response<List<T>>> observable) {
        return observable.concatMap(response -> {
            Optional<Integer> nextPage = nextPageExtractor.getNextPageFrom(response);
            Observable<Response<List<T>>> result = Observable.just(response);
            if (nextPage.isPresent()) {
                return result.mergeWith(recursor.recurse(nextPage.get()));
            }
            return result;
        });
    }

    @FunctionalInterface
    public interface PageRecursor<T> {
        Observable<Response<List<T>>> recurse(Integer nextPage);
    }
}
