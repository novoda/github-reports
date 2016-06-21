package com.novoda.github.reports.network;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import okhttp3.Interceptor;

public abstract class Interceptors {

    protected List<Interceptor> interceptors;

    protected Interceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    protected Interceptors() {
        this(new ArrayList<>());
    }

    public Interceptors with(Interceptor interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    public List<Interceptor> asList() {
        return interceptors;
    }

    public Stream<Interceptor> stream() {
        return interceptors.stream();
    }

}
