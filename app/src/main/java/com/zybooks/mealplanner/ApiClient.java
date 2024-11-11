package com.zybooks.mealplanner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Interceptor;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://api.spoonacular.com/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request().newBuilder()
                                    .addHeader("x-api-key", "2659f755ac1b4c8fa1014cd7a7d853ed")
                                    .build();
                            return chain.proceed(request);
                        }
                    }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
