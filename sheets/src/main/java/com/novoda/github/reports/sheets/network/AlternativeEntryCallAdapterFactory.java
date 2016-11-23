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

    static AlternativeEntryCallAdapterFactory create() {
        return new AlternativeEntryCallAdapterFactory();
    }

    private AlternativeEntryCallAdapterFactory() {
        // no op
    }

    @Override
    public CallAdapter<Observable<Entry>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != Observable.class) {
            return null;
        }

        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException("Observable type must be parameterized as Observable<Foo> or Observable<? extends Foo>");
        }

        Type innerType = getParameterUpperBound(0, (ParameterizedType) returnType);
        if (getRawType(innerType) != Response.class) {
            Type responseSheetType = new TypeToken<Observable<Response<Sheet>>>(){}.getType();
            return new BodyCallAdapter(this, responseSheetType, annotations, retrofit);
        }

        if (!(innerType instanceof ParameterizedType)) {
            throw new IllegalStateException("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
        }

        Type responseType = getParameterUpperBound(0, (ParameterizedType) innerType);
        return new ResponseCallAdapter(this, responseType, annotations, retrofit);
    }

}
