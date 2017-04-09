package net.tech.tripplanner.clientinstance;

import net.tech.tripplanner.model.AutoCompletePlacesResponse;
import net.tech.tripplanner.model.Result;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ashwini on 3/11/2017.
 */

public class IxIGoClient {
    public static final String BASE_URL = "http://build2.ixigo.com/";
    private static Retrofit retrofit = null;
    private static AutoCompletePlacesResponse currentCity = null;


    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

            // Can be Level.BASIC, Level.HEADERS, or Level.BODY
            // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
            //httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.networkInterceptors().add(httpLoggingInterceptor);

            retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AutoCompletePlacesResponse getCurrentCity() {
        return currentCity;
    }

    public static void setCurrentCity(AutoCompletePlacesResponse currentCity) {
        IxIGoClient.currentCity = currentCity;
    }
}
