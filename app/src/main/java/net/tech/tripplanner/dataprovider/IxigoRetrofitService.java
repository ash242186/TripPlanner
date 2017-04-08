package net.tech.tripplanner.dataprovider;

import net.tech.tripplanner.model.AutoCompletePlacesResponse;
import net.tech.tripplanner.model.POIDetailsPlaceResponse;
import net.tech.tripplanner.model.POIPlacesResponse;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ashwini on 4/8/2017.
 */

public interface IxigoRetrofitService {

    @GET("action/content/zeus/autocomplete?searchFor=tpAutoComplete&neCategories=City")
    Call<List<AutoCompletePlacesResponse>> getSearchPlaces(@Query("query") String query);

    @GET("api/v3/namedentities/city/{cityId}/categories?apiKey=ixicode!2$")
    Call<POIPlacesResponse> getPOIPlaces(@Path("cityId") String cityId, @Query("type") String type,
                                         @Query("skip") int skip, @Query("limit") int limit);

    @GET("api/v3/namedentities/id/{entityId}?apiKey=ixicode!2$")
    Call<POIDetailsPlaceResponse> getPOIDetailPlace(@Path("entityId") String entityId);

    @GET("api/v2/widgets/brand/inspire?product=1&apiKey=ixicode!2$")
    Call<POIDetailsPlaceResponse> getRecommendedPlace(@Path("entityId") String entityId);
}
