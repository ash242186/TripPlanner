package net.tech.tripplanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.tech.tripplanner.POIActivity;
import net.tech.tripplanner.PlaceAutocompleteActivity;
import net.tech.tripplanner.R;
import net.tech.tripplanner.helper.AppSession;
import net.tech.tripplanner.model.AutoCompletePlacesResponse;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ashwini on 4/8/2017.
 */

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PlaceViewHolder> {

    private ArrayList<AutoCompletePlacesResponse> resultList = new ArrayList<>();
    private final String LOG_TAG = getClass().getSimpleName();
    private LayoutInflater inflater;
    private Context mContext;
    private AppSession session;

    public PlacesAutoCompleteAdapter(Context context) {
        this.mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        session = new AppSession(context);
    }

    public void updateData(List<AutoCompletePlacesResponse> data) {
        resultList.clear();
        resultList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaceViewHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        final AutoCompletePlacesResponse res = resultList.get(position);
        holder.textid.setText(res.getText());
        String CountryName = res.getCo();
        if (res.getSt() != null && !TextUtils.isEmpty(res.getSt())) {
            holder.textid2.setText(res.getSt() + ", " + CountryName);
        } else {
            holder.textid2.setText(CountryName);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "clicked");
                session.Set_Arraykey(session.App_history_places, res.getId());
                Intent intent = new Intent(mContext, POIActivity.class);
                intent.putExtra("CityId", res.getId());
                mContext.startActivity(intent);
            }
        });
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        public TextView textid, textid2;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            textid = (TextView) itemView.findViewById(R.id.textid);
            textid2 = (TextView) itemView.findViewById(R.id.textid2);
        }
    }
}

