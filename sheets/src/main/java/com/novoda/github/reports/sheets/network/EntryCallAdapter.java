package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Sheet;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

class EntryCallAdapter implements CallAdapter<Observable<Entry>> {

    private final CallAdapter<Observable<Response<Sheet>>> delegate;

    EntryCallAdapter(CallAdapter<Observable<Response<Sheet>>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Type responseType() {
        return delegate.responseType();
    }

    @Override
    public <R> Observable<Entry> adapt(Call<R> call) {
        Observable<Response<Sheet>> observable = delegate.adapt(call);
        return observable
                .flatMap(toEntries());
    }

    private Func1<Response<Sheet>, Observable<Entry>> toEntries() {
        return sheetResponse -> Observable.from(sheetResponse.body().getFeed().getEntries());
    }

}
