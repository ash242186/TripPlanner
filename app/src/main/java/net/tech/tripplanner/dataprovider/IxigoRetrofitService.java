package net.tech.tripplanner.dataprovider;

import net.tech.tripplanner.model.AutoCompletePlacesResponse;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ashwini on 4/8/2017.
 */

public interface IxigoRetrofitService {

    @GET("autocomplete?searchFor=tpAutoComplete&neCategories=City")
    Call<List<AutoCompletePlacesResponse>> getSearchPlaces(@Query("query") String query);
}
