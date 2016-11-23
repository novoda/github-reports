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

class EntryCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<Observable<Entry>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (!isAcceptedType(returnType)) {
            return null; // move on to next adapter
        }

        Type type = new TypeToken<Observable<Response<Sheet>>>() {}.getType();
        CallAdapter<Observable<Response<Sheet>>> delegate = getDelegateCallAdapter(annotations, retrofit, type);
        return new EntryCallAdapter(delegate);
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

    @SuppressWarnings("unchecked") // we're forced to cast due to having to implement <R> T adapt(Call<R> call)
    private CallAdapter<Observable<Response<Sheet>>> getDelegateCallAdapter(Annotation[] annotations, Retrofit retrofit, Type type) {
        return (CallAdapter<Observable<Response<Sheet>>>) retrofit.nextCallAdapter(this, type, annotations);
    }

}
