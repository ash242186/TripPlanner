package net.tech.tripplanner;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.squareup.picasso.Picasso;

import net.tech.tripplanner.clientinstance.IxIGoClient;
import net.tech.tripplanner.dataprovider.IxigoRetrofitService;
import net.tech.tripplanner.model.POIDetailsPlaceResponse;
import net.tech.tripplanner.model.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlaceDetailActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private Picasso picBuilder;
    private ImageView place_ImageView;
    private FlowLayout categoriesNameTextView;
    private Call<POIDetailsPlaceResponse> placeDetailCall;

    private TextView placeNameTextView, addressTextView, priceTagTextView, descriptionTextView, whyTovisitTextView, websiteTextView;
    private ProgressBar image_loading_indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        placeNameTextView = (TextView)findViewById(R.id.placeNameTextView);
        priceTagTextView = (TextView) findViewById(R.id.priceTagTextView);
        whyTovisitTextView = (TextView)findViewById(R.id.whyTovisitTextView);
        descriptionTextView = (TextView)findViewById(R.id.descriptionTextView);
        place_ImageView = (ImageView) findViewById(R.id.place_ImageView);
        addressTextView = (TextView)findViewById(R.id.addressTextView);
        websiteTextView = (TextView) findViewById(R.id.websiteTextView);
        categoriesNameTextView = (FlowLayout) findViewById(R.id.categoriesNameTextView);
        image_loading_indicator = (ProgressBar) findViewById(R.id.image_loading_indicator);

        Log.i(TAG, "created");
        Log.i(TAG, getIntent().getStringExtra("entityId"));
        picBuilder = new Picasso.Builder(this)
                .build();

        IxigoRetrofitService service = IxIGoClient.getClient().create(IxigoRetrofitService.class);

        placeDetailCall = service.getPOIDetailPlace(getIntent().getStringExtra("entityId"));
        placeDetailCall.enqueue(new Callback<POIDetailsPlaceResponse>() {
            @Override
            public void onResponse(Call<POIDetailsPlaceResponse> call, Response<POIDetailsPlaceResponse> response) {
                Log.i(TAG, response.body().toString());
                if(response.isSuccessful()){
                    processdetail(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<POIDetailsPlaceResponse> call, Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
            }
        });
    }

    private void processdetail(Result place){
        if (!TextUtils.isEmpty(place.getKeyImageUrl())) {
            picBuilder.load(place.getKeyImageUrl())
                    .fit()
                    .into(place_ImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            image_loading_indicator.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            image_loading_indicator.setVisibility(View.GONE);
                            place_ImageView.setImageResource(R.drawable.ic_no_places);
                        }
                    });
        }else{
            image_loading_indicator.setVisibility(View.GONE);
            place_ImageView.setImageResource(R.drawable.ic_no_places);
        }


        placeNameTextView.setText(place.getName());
        categoriesNameTextView.removeAllViews();
        for(Object category : place.getCategoryNames()){
            if(!TextUtils.isEmpty(String.valueOf(category))) {
                TextView tv = new TextView(this);
                tv.setText(String.valueOf(category));
                tv.setPadding(20, 20, 20, 20);
                tv.setBackgroundResource(R.drawable.label_bg);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setTextColor(getResources().getColor(R.color.white, null));
                } else {
                    tv.setTextColor(getResources().getColor(R.color.white));
                }
                categoriesNameTextView.addView(tv, new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT));
            }
        }
        if(place.getMinimumPrice() != null && place.getMinimumPrice() >0 ) {
            priceTagTextView.setVisibility(View.VISIBLE);
            priceTagTextView.setText(getString(R.string.currency ,String.valueOf(place.getMinimumPrice())));
        }else{
            priceTagTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(place.getAddress())) {
            addressTextView.setText(place.getAddress());
            addressTextView.setVisibility(View.VISIBLE);
        }else{
            addressTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(place.getDescription())) {
            descriptionTextView.setText(Html.fromHtml(place.getDescription()));
            descriptionTextView.setVisibility(View.VISIBLE);
        }else{
            descriptionTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(place.getWhyToVisit())) {
            whyTovisitTextView.setText(place.getWhyToVisit());
            whyTovisitTextView.setVisibility(View.VISIBLE);
        }else{
            whyTovisitTextView.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(place.getUrl())){
            websiteTextView.setText(place.getWhyToVisit());
            websiteTextView.setVisibility(View.VISIBLE);
        }else{
            websiteTextView.setVisibility(View.GONE);
        }
    }

    public void expendDescription(View view){
        Object tag = descriptionTextView.getTag();
        if(Boolean.parseBoolean(String.valueOf(tag))){
            descriptionTextView.setMaxLines(3);
            descriptionTextView.setTag(false);
        }else {
            descriptionTextView.setMaxLines(Integer.MAX_VALUE);
            descriptionTextView.setTag(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
