package net.tech.tripplanner.adapter;

/**
 * Created by ashwini on 4/9/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import net.tech.tripplanner.R;
import net.tech.tripplanner.clientinstance.IxIGoClient;
import net.tech.tripplanner.dataprovider.IxigoRetrofitService;
import net.tech.tripplanner.model.POIDetailsPlaceResponse;
import net.tech.tripplanner.model.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class HistoryPlacesPageAdapter extends PagerAdapter {

    private final String TAG = getClass().getSimpleName();
    private List<String> cities;
    private Picasso picBuilder;
    private Call<POIDetailsPlaceResponse> placeDetailCall;
    private LayoutInflater inflater;



    public HistoryPlacesPageAdapter(Context mContext, List<String> cities) {
        this.cities = cities;
        this.picBuilder = new Picasso.Builder(mContext)
                .build();
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (object instanceof View && view.equals(object));
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        ViewPager pager = (ViewPager) container;
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.history_item, pager, false);
        layout.setTag(position);

        final View imageLoadingIndicator = layout.findViewById(R.id.image_loading_indicator);
        final ImageView imageView = (ImageView) layout.findViewById(R.id.place_ImageView);
        final TextView placeNameTextView = (TextView) layout.findViewById(R.id.placeNameTextView);
        final FlowLayout categoriesNameTextView = (FlowLayout) layout.findViewById(R.id.categoriesNameTextView);
        IxigoRetrofitService service = IxIGoClient.getClient().create(IxigoRetrofitService.class);
        placeDetailCall = service.getPOIDetailPlace(cities.get(position));
        placeDetailCall.enqueue(new retrofit2.Callback<POIDetailsPlaceResponse>() {
            @Override
            public void onResponse(Call<POIDetailsPlaceResponse> call, Response<POIDetailsPlaceResponse> response) {
                Log.i(TAG, response.body().toString());
                if(response.isSuccessful()){
                    //processdetail(response.body().getData());
                    Result place = response.body().getData();
                    if (!TextUtils.isEmpty(place.getKeyImageUrl())) {
                        picBuilder.load(place.getKeyImageUrl())
                                .fit()
                                .into(imageView, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        placeNameTextView.setTextColor(Color.WHITE);
                                        imageLoadingIndicator.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        placeNameTextView.setTextColor(Color.BLACK);
                                        imageLoadingIndicator.setVisibility(View.GONE);
                                        imageView.setImageResource(R.drawable.ic_no_places);
                                    }
                                });
                    }else{
                        imageLoadingIndicator.setVisibility(View.GONE);
                        imageView.setImageResource(R.drawable.ic_no_places);
                    }
                    placeNameTextView.setText(place.getName());
                    categoriesNameTextView.removeAllViews();
                    for(Object category : place.getCategoryNames()){
                        if(!TextUtils.isEmpty(String.valueOf(category))) {
                            TextView tv = new TextView(container.getContext());
                            tv.setText(String.valueOf(category));
                            tv.setPadding(20, 20, 20, 20);
                            tv.setBackgroundResource(R.drawable.label_bg);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                tv.setTextColor(container.getContext().getResources().getColor(R.color.white, null));
                            } else {
                                tv.setTextColor(container.getContext().getResources().getColor(R.color.white));
                            }
                            categoriesNameTextView.addView(tv, new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT));
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<POIDetailsPlaceResponse> call, Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
            }
        });

        pager.addView(layout);
        return layout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        Context context = container.getContext();
        if (context != null) {
            View view = container.findViewWithTag(position);
            ImageView imageView = (ImageView) view.findViewById(R.id.place_ImageView);
            picBuilder.cancelRequest(imageView);
        }
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        //return super.getItemPosition(object);
        if (!cities.contains(object)) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }
}
