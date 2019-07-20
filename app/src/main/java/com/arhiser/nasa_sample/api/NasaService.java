package com.arhiser.nasa_sample.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NasaService {
    public static String KEY = "bUPDj3NcY7TPvoShGVEilLJJmiYHzdqyirJx04n4";

    NasaApi api;

    public NasaService() {
        Retrofit retrofit = createRetrofit();
        api = retrofit.create(NasaApi.class);
    }

    public NasaApi getApi() {
        return api;
    }

    private OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();
                final HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", KEY)
                        .build();
                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url);
                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        return httpClient.build();
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/EPIC/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
