package com.novoda.github.reports.github.network;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.java8.HttpException;

class ResponseCallAdapterFactory extends CallAdapter.Factory {

    static ResponseCallAdapterFactory create() {
        return new ResponseCallAdapterFactory();
    }

    private ResponseCallAdapterFactory() {
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != Response.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException("CompletableFuture return type must be parameterized"
                                                    + " as CompletableFuture<Foo> or CompletableFuture<? extends Foo>");
        }
        Type innerType = getParameterUpperBound(0, (ParameterizedType) returnType);

        if (getRawType(innerType) != Response.class) {
            // Generic type is not Response<T>. Use it for body-only adapter.
            return new ResponseCallAdapterFactory.BodyCallAdapter(innerType);
        }

        // Generic type is Response<T>. Extract T and create the Response version of the adapter.
        if (!(innerType instanceof ParameterizedType)) {
            throw new IllegalStateException("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) innerType);
        return new ResponseCallAdapterFactory.ResponseCallAdapter(responseType);
    }

    private static class BodyCallAdapter implements CallAdapter<CompletableFuture<?>> {
        private final Type responseType;

        BodyCallAdapter(Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public <R> CompletableFuture<R> adapt(final Call<R> call) {
            final CompletableFuture<R> future = new CompletableFuture<R>() {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    if (mayInterruptIfRunning) {
                        call.cancel();
                    }
                    return super.cancel(mayInterruptIfRunning);
                }
            };

            call.enqueue(new Callback<R>() {
                @Override
                public void onResponse(Call<R> call, Response<R> response) {
                    if (response.isSuccessful()) {
                        future.complete(response.body());
                    } else {
                        future.completeExceptionally(new HttpException(response));
                    }
                }

                @Override
                public void onFailure(Call<R> call, Throwable t) {
                    future.completeExceptionally(t);
                }
            });

            return future;
        }
    }

    private static class ResponseCallAdapter implements CallAdapter<Response<?>> {
        private final Type responseType;

        ResponseCallAdapter(Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public <R> Response<R> adapt(final Call<R> call) {

            try {
                return call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
