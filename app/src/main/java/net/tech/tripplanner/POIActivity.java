package net.tech.tripplanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.tech.tripplanner.clientinstance.IxIGoClient;
import net.tech.tripplanner.dataprovider.IxigoRetrofitService;
import net.tech.tripplanner.fragments.AccommodationFragment;
import net.tech.tripplanner.fragments.PlaceVisitFragment;
import net.tech.tripplanner.fragments.ThingsToDoFragment;
import net.tech.tripplanner.model.POIPlacesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class POIActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private String CityId;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CityId = getIntent().getStringExtra("CityId");
        onSectionAttached(0);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_hotel:
                    onSectionAttached(1);
                    break;
                case R.id.navigation_thingstodo:
                    onSectionAttached(2);
                    break;
                case R.id.navigation_placetovisit:
                default:
                    onSectionAttached(0);
                    break;
            }
            return true;
        }

    };

    private void onSectionAttached(int page) {

        switch (page) {
            case 1:
                if(hotelList == null) {
                    pagePlaceRequest("hotel", 0);
                }
                replaceFragment(new AccommodationFragment(), "hotel");
                break;
            case 2:
                if(thingstodoList == null) {
                    pagePlaceRequest("Things To Do", 0);
                }
                replaceFragment(new ThingsToDoFragment(), "Things To Do");
                break;
            case 0:
            default:
                if(placetovisitList == null) {
                    pagePlaceRequest("Places To Visit", 0);
                }
                replaceFragment(new PlaceVisitFragment(), "Places To Visit");
                break;
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

    public void updateToolbarTitle(int Text){
        toolbar.setTitle(Text);
    }

    private void replaceFragment(Fragment fragment, String fragmentTag) {
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(fragmentTag, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            System.out.println("Fragment "+fragment);
            if(fragment instanceof PlaceVisitFragment){
                if (placetovisitList != null) {
                    ((PlaceVisitFragment)fragment).setResponse(placetovisitList);
                }
            }else if(fragment instanceof AccommodationFragment){
                if (hotelList != null) {
                    ((AccommodationFragment)fragment).setResponse(hotelList);
                }
            }else if(fragment instanceof ThingsToDoFragment){
                if (thingstodoList != null) {
                    ((ThingsToDoFragment)fragment).setResponse(thingstodoList);
                }
            }
            FragmentTransaction ft = manager.beginTransaction();
            //fragment.setRetainInstance(true);
            ft.setAllowOptimization(true);
            ft.replace(R.id.frame_layout, fragment, fragmentTag);
            ft.setCustomAnimations(R.anim.slide_in_up, R.anim.fade_out);
            ft.addToBackStack(fragmentTag);
            ft.commit();

        }
    }


    public final static int limit = 10;
    private Call<POIPlacesResponse> poiPlacesCall;
    private POIPlacesResponse thingstodoList = null, placetovisitList = null, hotelList = null;
    public void pagePlaceRequest(final String type, final int skip) {
        Log.i(TAG, "skip "+skip);
        IxigoRetrofitService service = IxIGoClient.getClient().create(IxigoRetrofitService.class);

        poiPlacesCall = service.getPOIPlaces(CityId, type, skip, limit);

        poiPlacesCall.enqueue(new Callback<POIPlacesResponse>() {
            @Override
            public void onResponse(Call<POIPlacesResponse> call, Response<POIPlacesResponse> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, response.body().getData().toString());
                    Log.i(TAG, type);
                    if (type.equalsIgnoreCase("Places To Visit")) {
                        PlaceVisitFragment placeVisitListFragment = (PlaceVisitFragment) getSupportFragmentManager().findFragmentByTag("Places To Visit");
                        if (placeVisitListFragment != null) {
                            if (placetovisitList != null) {
                                //placetovisitList.setNextPageToken(response.body().getNextPageToken());
                                placetovisitList.getData().getData().addAll(response.body().getData().getData());
                                //placetovisitList.setStatus(response.body().getStatus());
                                //response.body().setSkip(response.body().getSkip() + skip);
                                placeVisitListFragment.onAddData(response.body());
                            } else {
                                placetovisitList = response.body();
                                placeVisitListFragment.onUpdateData(response.body());
                            }
                        }
                    } else if (type.equalsIgnoreCase("hotel")) {
                        AccommodationFragment hotelListFragment = (AccommodationFragment) getSupportFragmentManager().findFragmentByTag("hotel");
                        if (hotelListFragment != null) {
                            if (hotelList != null) {
                                //hotelList.setNextPageToken(response.body().getNextPageToken());
                                hotelList.getData().getData().addAll(response.body().getData().getData());
                                //hotelList.setStatus(response.body().getStatus());
                                //response.body().setSkip(skip);
                                hotelListFragment.onAddData(response.body());
                            } else {
                                hotelList = response.body();
                                hotelListFragment.onUpdateData(response.body());
                            }
                        }
                    } else if (type.equalsIgnoreCase("Things To Do")) {
                        ThingsToDoFragment thingtodoListFragment = (ThingsToDoFragment) getSupportFragmentManager().findFragmentByTag("Things To Do");
                        if (thingtodoListFragment != null) {
                            if (thingstodoList != null) {
                                //thingstodoList.setNextPageToken(response.body().getNextPageToken());
                                thingstodoList.getData().getData().addAll(response.body().getData().getData());
                                //thingstodoList.setStatus(response.body().getStatus());
                                //response.body().setSkip(skip);
                                thingtodoListFragment.onAddData(response.body());
                            } else {
                                thingstodoList = response.body();
                                thingtodoListFragment.onUpdateData(response.body());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<POIPlacesResponse> call, Throwable t) {
                t.printStackTrace();
                Snackbar.make(findViewById(R.id.frame_layout),R.string.serverError, BaseTransientBottomBar.LENGTH_LONG )
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pagePlaceRequest(type, skip);
                            }
                        })
                        .show();
            }
        });
    }
}
