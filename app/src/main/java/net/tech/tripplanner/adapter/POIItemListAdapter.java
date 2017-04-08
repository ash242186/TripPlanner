package net.tech.tripplanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.tech.tripplanner.PlaceDetailActivity;
import net.tech.tripplanner.R;
import net.tech.tripplanner.model.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashwini on 3/8/2017.
 */

public class POIItemListAdapter extends RecyclerView.Adapter<POIItemListAdapter.POIPlaceViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private Picasso picBuilder;
    private LayoutInflater layoutInflater;
    private Context context;
    private List<Object> data_;

    public POIItemListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        data_ = new ArrayList<>();
        picBuilder = new Picasso.Builder(context)
                .build();
    }

    public void updateResultData(List<Result> data) {
        data_.clear();
        data_.addAll(data);
        Log.i(TAG, "updateResultData " + String.valueOf(data_.size()));
        notifyDataSetChanged();
    }

    public void addResultData(List<Result> data) {
        data_.addAll(data);
        Log.i(TAG, "addResultData " + String.valueOf(data_.size()));
        notifyDataSetChanged();
    }

    @Override
    public POIPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new POIPlaceViewHolder(layoutInflater.inflate(R.layout.item_place, parent, false));

    }

    @Deprecated
    @Override
    public void onBindViewHolder(final POIPlaceViewHolder placeViewHolder, int position) {
        final Result result = (Result) data_.get(position);

        placeViewHolder.placeNameTextView.setText(result.getName());
        placeViewHolder.placeNameTextView.setTextColor(Color.BLACK);
        placeViewHolder.image_loading_indicator.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(result.getKeyImageUrl())) {
            picBuilder.load(result.getKeyImageUrl())
                    .fit()
                    .into(placeViewHolder.place_ImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            placeViewHolder.placeNameTextView.setTextColor(Color.WHITE);
                            placeViewHolder.image_loading_indicator.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            placeViewHolder.placeNameTextView.setTextColor(Color.BLACK);
                            placeViewHolder.image_loading_indicator.setVisibility(View.GONE);
                            placeViewHolder.place_ImageView.setImageResource(R.drawable.ic_no_places);
                        }
                    });
        }else{
            placeViewHolder.placeNameTextView.setTextColor(Color.BLACK);
            placeViewHolder.image_loading_indicator.setVisibility(View.GONE);
            placeViewHolder.place_ImageView.setImageResource(R.drawable.ic_no_places);
        }

        placeViewHolder.categoriesNameTextView.removeAllViews();
        for(Object category : result.getCategoryNames()){
            if(!TextUtils.isEmpty(String.valueOf(category))) {
                TextView tv = new TextView(context);
                tv.setText(String.valueOf(category));
                tv.setPadding(20, 20, 20, 20);
                tv.setBackgroundResource(R.drawable.label_bg);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setTextColor(context.getResources().getColor(R.color.white, null));
                } else {
                    tv.setTextColor(context.getResources().getColor(R.color.white));
                }
                placeViewHolder.categoriesNameTextView.addView(tv, new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT));
            }
        }
        if(result.getMinimumPrice() != null && result.getMinimumPrice() >0 ) {
            placeViewHolder.priceTagTextView.setVisibility(View.VISIBLE);
            placeViewHolder.priceTagTextView.setText(context.getString(R.string.currency ,String.valueOf(result.getMinimumPrice())));
        }else{
            placeViewHolder.priceTagTextView.setVisibility(View.GONE);
        }

        if(result.getAddress()!= null) {
            placeViewHolder.addressTextView.setText(result.getAddress());
            placeViewHolder.addressTextView.setVisibility(View.VISIBLE);
        }else{
            placeViewHolder.addressTextView.setVisibility(View.GONE);
        }


        placeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent sendBundle = new Intent(context, PlaceDetailActivity.class);
                sendBundle.putExtra("entityId", result.getId());
                context.startActivity(sendBundle);
            }
        });

    }


    @Override
    public int getItemCount() {
        return data_.size();
    }

    class POIPlaceViewHolder extends RecyclerView.ViewHolder {
        public ImageView place_ImageView;
        public ProgressBar image_loading_indicator;
        public TextView placeNameTextView, priceTagTextView, addressTextView;
        public FlowLayout categoriesNameTextView;

        public POIPlaceViewHolder(View itemView) {
            super(itemView);
            place_ImageView = (ImageView) itemView.findViewById(R.id.place_ImageView);
            image_loading_indicator = (ProgressBar) itemView.findViewById(R.id.image_loading_indicator);
            placeNameTextView = (TextView) itemView.findViewById(R.id.placeNameTextView);
            categoriesNameTextView = (FlowLayout) itemView.findViewById(R.id.categoriesNameTextView);

            priceTagTextView = (TextView) itemView.findViewById(R.id.priceTagTextView);
            addressTextView = (TextView) itemView.findViewById(R.id.addressTextView);
        }
    }
}
