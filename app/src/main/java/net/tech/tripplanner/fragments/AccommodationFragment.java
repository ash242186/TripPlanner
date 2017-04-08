package net.tech.tripplanner.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.tech.tripplanner.POIActivity;
import net.tech.tripplanner.R;
import net.tech.tripplanner.adapter.POIItemListAdapter;
import net.tech.tripplanner.model.POIPlacesResponse;

/**
 * Created by ashwini on 4/8/2017.
 */

public class AccommodationFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();
    private RecyclerView list_view;
    private LinearLayout loadingMore;
    private ProgressBar loadingprogressBar;
    private RelativeLayout emptyRecyclerView;
    private TextView emptytextView;
    private LinearLayoutManager listViewLayoutManager;
    private POIItemListAdapter listAdapter;
    private POIPlacesResponse response;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.list_view_places, container, false);
        list_view = (RecyclerView) rootView.findViewById(R.id.list_view);
        loadingMore = (LinearLayout) rootView.findViewById(R.id.loadingMore);
        emptyRecyclerView = (RelativeLayout) rootView.findViewById(R.id.emptyRecyclerView);
        emptytextView = (TextView) rootView.findViewById(R.id.emptytextView);
        loadingprogressBar = (ProgressBar) rootView.findViewById(R.id.loadingprogressBar);
        //loadingprogressBar.setVisibility(View.VISIBLE);
        //list_view.setVisibility(View.GONE);

        listViewLayoutManager = new LinearLayoutManager(getActivity());
        list_view.setLayoutManager(listViewLayoutManager);
        list_view.setHasFixedSize(true);
        list_view.setAdapter(listAdapter);
        list_view.addOnScrollListener(scroll);
        return rootView;
    }

    public void setResponse(POIPlacesResponse response) {
        this.response = response;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach");
        listAdapter = new POIItemListAdapter(context);
        /**/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach");
        listAdapter = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        ((POIActivity) getActivity()).updateToolbarTitle(R.string.title_accommodation);
        if (response != null) {
            loading = false;
            onLoadingShow(false);
            listAdapter.updateResultData(response.getData().getData());
            loadingprogressBar.setVisibility(View.GONE);
            list_view.setVisibility(View.VISIBLE);
            if (response.getData().getData().size() == 0) {
                emptyRecyclerView.setVisibility(View.VISIBLE);
                emptytextView.setText(getString(R.string.emptyplaces, "Places"));
                list_view.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    public void onUpdateData(POIPlacesResponse response) {
        //Log.i(TAG, response.toString());
        //Log.i(TAG, "onUpdateData skip "+response.getSkip());
        Log.i(TAG, String.valueOf(response.getData().getData().size()));
        this.response = response;
        loadingprogressBar.setVisibility(View.GONE);
        list_view.setVisibility(View.VISIBLE);
        if (response.getData().getData().size() > 0) {
            listAdapter.updateResultData(response.getData().getData());
        } else {
            emptyRecyclerView.setVisibility(View.VISIBLE);
            emptytextView.setText(getString(R.string.emptyplaces, "Places"));
            list_view.setVisibility(View.GONE);
        }
        loading = false;
        onLoadingShow(false);
    }

    public void onAddData(POIPlacesResponse response) {
        //Log.i(TAG, response.toString());
        //Log.i(TAG, "onAddData skip "+response.getSkip());
        Log.i(TAG, String.valueOf(response.getData().getData().size()));

        //TODO set current skip
        this.response.getData().getData().addAll(response.getData().getData());

        listAdapter.addResultData(response.getData().getData());
        loading = false;
        onLoadingShow(false);
    }


    private void onLoadingShow(Boolean show) {
        if (show) {
            loadingMore.setVisibility(View.VISIBLE);
        } else {
            loadingMore.setVisibility(View.GONE);
        }
    }

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 2;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private RecyclerView.OnScrollListener scroll = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount = list_view.getChildCount();
            totalItemCount = listViewLayoutManager.getItemCount();
            firstVisibleItem = listViewLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    //loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if ((!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) && !loading) {
                // End has been reached
                Log.e(TAG, "end called " + previousTotal);
                // Do something
                onLoadingShow(true);
                loading = true;

                if (getActivity() instanceof POIActivity && response != null) {
                    ((POIActivity) getActivity()).pagePlaceRequest("hotel", (POIActivity.limit + previousTotal));
                } else {
                    list_view.removeOnScrollListener(scroll);
                    onLoadingShow(false);
                }

            } else {
                //Log.i(TAG, "not end called");
            }
        }
    };
}
