package net.tech.tripplanner.clientinstance;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ashwini on 3/11/2017.
 */

public class IxIGoClient {
    public static final String AUTOCOMPLETE_BASE_URL = "http://build2.ixigo.com/action/content/zeus/";
    private static Retrofit retrofit = null;


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
                    .baseUrl(AUTOCOMPLETE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
