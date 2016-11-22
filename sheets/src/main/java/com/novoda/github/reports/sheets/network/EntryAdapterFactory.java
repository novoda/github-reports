package com.novoda.github.reports.sheets.network;

import com.google.gson.reflect.TypeToken;
import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Sheet;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;

class EntryAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<Observable<Entry>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (!isAcceptedType(returnType)) {
            return null; // move on to next adapter
        }

//        CallAdapter delegate = retrofit.nextCallAdapter(this, returnType, annotations);
        Type type = new TypeToken<Observable<Response<Sheet>>>() {}.getType();
        CallAdapter delegate = retrofit.nextCallAdapter(this, type, annotations);
        return new CallAdapter<Observable<Entry>>() {
            @Override
            public Type responseType() {
                return delegate.responseType();
            }

            @Override
            public <R> Observable<Entry> adapt(Call<R> call) {
                Observable<Response<Sheet>> observable = (Observable<Response<Sheet>>) delegate.adapt(call);
                return observable
                        .flatMap(sheetResponse -> Observable.from(sheetResponse.body().getFeed().getEntries()));
            }

        };
    }

    private boolean isAcceptedType(Type type) {
        if (getRawType(type) != Observable.class) {
            return false;
        }

        ParameterizedType parameterizedType;
        try {
            parameterizedType = (ParameterizedType) type;
        } catch (ClassCastException exception) {
            return false;
        }

        Type parameterType = parameterizedType.getActualTypeArguments()[0];
        if (parameterType != Entry.class) {
            return false;
        }

        return true;
    }

}
