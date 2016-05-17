package com.novoda.github.reports.github;

public interface GithubRequestListener<T> {

    void onResponse(T t);

    void onError(Throwable throwable);

}
