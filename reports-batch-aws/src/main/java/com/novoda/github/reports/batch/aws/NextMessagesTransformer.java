package com.novoda.github.reports.batch.aws;

import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

public abstract class NextMessagesTransformer<T, M extends AmazonQueueMessage> implements Observable.Transformer<Response<List<T>>, AmazonQueueMessage> {

    private final NextPageExtractor nextPageExtractor;
    private final LastPageExtractor lastPageExtractor;

    protected final M currentMessage;

    protected NextMessagesTransformer(M currentMessage, NextPageExtractor nextPageExtractor, LastPageExtractor lastPageExtractor) {
        this.nextPageExtractor = nextPageExtractor;
        this.lastPageExtractor = lastPageExtractor;
        this.currentMessage = currentMessage;
    }

    @Override
    public Observable<AmazonQueueMessage> call(Observable<Response<List<T>>> observable) {
        return observable.flatMap(new Func1<Response<List<T>>, Observable<AmazonQueueMessage>>() {
            @Override
            public Observable<AmazonQueueMessage> call(Response<List<T>> response) {
                List<AmazonQueueMessage> messages = new ArrayList<>();
                messages.addAll(getNextPagesMessages(response));
                messages.addAll(getDerivedMessages(response));
                return Observable.from(messages);
            }
        });
    }

    private List<AmazonQueueMessage> getNextPagesMessages(Response<List<T>> response) {
        List<AmazonQueueMessage> messages = new ArrayList<>();

        if (!currentMessage.localTerminal()) {
            return messages;
        }

        Optional<Integer> nextPageOptional = nextPageExtractor.getNextPageFrom(response);
        Optional<Integer> lastPageOptional = lastPageExtractor.getLastPageFrom(response);

        if (nextPageOptional.isPresent()) {
            int nextPage = nextPageOptional.get();
            int lastPage = lastPageOptional.get();

            for (int page = nextPage; page <= lastPage; page++) {
                boolean terminalMessage = page == lastPage;
                messages.add(getNextPageMessage(terminalMessage, nextPage));
            }
        }

        return messages;
    }

    protected abstract M getNextPageMessage(boolean isTerminalMessage, int nextPage);

    private List<AmazonQueueMessage> getDerivedMessages(Response<List<T>> response) {
        List<AmazonQueueMessage> messages = new ArrayList<>();

        List<T> items = response.body();
        items.forEach(item -> messages.add(getDerivedMessage(item)));

        return messages;
    }

    protected abstract AmazonQueueMessage getDerivedMessage(T item);

}
