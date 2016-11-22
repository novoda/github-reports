package com.novoda.github.reports.sheets.network;

import com.google.gson.reflect.TypeToken;
import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Sheet;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;

class AlternativeEntryCallAdapterFactory extends CallAdapter.Factory {

    public static AlternativeEntryCallAdapterFactory create() {
        return new AlternativeEntryCallAdapterFactory();
    }

    private AlternativeEntryCallAdapterFactory() {
    }

    @Override
    public CallAdapter<Observable<Entry>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != Observable.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException(
                    "CompletableFuture return type must be parameterized as CompletableFuture<Foo> or CompletableFuture<? extends Foo>"
            );
        }
        Type innerType = getParameterUpperBound(0, (ParameterizedType) returnType);

        if (getRawType(innerType) != Response.class) {
            // Generic type is not Response<T>. Use it for body-only adapter.
            Type t = new TypeToken<Observable<Response<Sheet>>>(){}.getType();
            return new BodyCallAdapter(this, t, annotations, retrofit);
        }

        // Generic type is Response<T>. Extract T and create the Response version of the adapter.
        if (!(innerType instanceof ParameterizedType)) {
            throw new IllegalStateException("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) innerType);
        return new ResponseCallAdapter(this, responseType, annotations, retrofit);
    }

}
