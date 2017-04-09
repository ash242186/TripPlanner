package net.tech.tripplanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.tech.tripplanner.adapter.HistoryPlacesPageAdapter;
import net.tech.tripplanner.clientinstance.IxIGoClient;
import net.tech.tripplanner.dataprovider.IxigoRetrofitService;
import net.tech.tripplanner.fragments.MultipleCurrentOptionFragment;
import net.tech.tripplanner.helper.AppSession;
import net.tech.tripplanner.model.AutoCompletePlacesResponse;
import net.tech.tripplanner.model.POIDetailsPlaceResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandActivity extends BaseActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, MultipleCurrentOptionFragment.TapListener{

    private final String TAG = getClass().getSimpleName();
    private View emptyRecyclerView;
    private TextView emptytextView, currentLocationTxt;

    private AppSession session;
    private ViewPager historyRecyclerView;
    private CircleIndicator indicator;
    private HistoryPlacesPageAdapter historyAdapter;

    private GoogleApiClient mGoogleApiClient;
    // These settings are the same as the settings for the map. They will in
    // fact give you updates at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000) // 5 seconds
            .setFastestInterval(16) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private LatLng mLastLocation = null;
    private final int REQUEST_CHECK_SETTINGS = 10001;
    private final int REQUEST_SEARCH_PLACE = 11001;
    private final int REQUEST_CURRENT_PLACE = 11002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new AppSession(this);

        emptyRecyclerView = findViewById(R.id.emptyRecyclerView);
        emptytextView = (TextView) findViewById(R.id.emptytextView);

        currentLocationTxt = (TextView) findViewById(R.id.currentLocationTxt);

        historyRecyclerView = (ViewPager) findViewById(R.id.historyRecyclerView);
        indicator = (CircleIndicator) findViewById(R.id.indicator);

        //biuld client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firstTime = true;
        if(IxIGoClient.getCurrentCity() != null){
            setCurrentCity(IxIGoClient.getCurrentCity());
        }else {
            mGoogleApiClient.connect();
        }
        ArrayList<String> cities = session.Get_Arraykey(session.App_history_places);
        if(cities.size() == 0) {
            emptyRecyclerView.setVisibility(View.VISIBLE);
            emptytextView.setText(getString(R.string.emptyplaces, "Search History \n Please search new place"));
            historyRecyclerView.setVisibility(View.GONE);
        }else{
            emptyRecyclerView.setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.VISIBLE);
            historyAdapter = new HistoryPlacesPageAdapter(this, cities);
            historyRecyclerView.setAdapter(historyAdapter);
            indicator.setViewPager(historyRecyclerView);
        }
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected()){
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_land, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_search:
                startActivityForResult(new Intent(this, PlaceAutocompleteActivity.class), REQUEST_SEARCH_PLACE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void myLocation(View view){
        if(checkPermissionStatus()){
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isGpsEnabled){
                if (mGoogleApiClient.isConnected()) {
                    startLocationUpdates();
                }else{
                    mGoogleApiClient.connect();
                }
            }else {
                askGPSSetting();
            }
        }else{
            requestPermission();
        }

    }

    private void askGPSSetting(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(REQUEST);
        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(mResultCallbackFromSettings);
    }

    // The callback for the management of the user settings regarding location
    private ResultCallback<LocationSettingsResult> mResultCallbackFromSettings = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(LocationSettingsResult result) {
            final Status status = result.getStatus();
            //final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                                LandActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e(TAG, "Settings change unavailable. We have no way to fix the settings so we won't show the dialog.");
                    break;
            }
        }
    };

    /**
     * Used to check the result of the check of the user location settings
     *
     * @param requestCode code of the request made
     * @param resultCode code of the result of that request
     * @param intent intent with further information
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i(TAG, "requestCode :"+requestCode+" resultCode :"+resultCode);
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (mGoogleApiClient.isConnected()) {
                            startLocationUpdates();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                }
                break;
            case REQUEST_SEARCH_PLACE :
                if(resultCode == Activity.RESULT_OK) {
                    AutoCompletePlacesResponse resul_ = new GsonBuilder()
                            .create().fromJson(intent.getStringExtra("places"), AutoCompletePlacesResponse.class);

                    session.Set_Arraykey(session.App_history_places, resul_.getId());
                    Intent sendIntent = new Intent(this, POIActivity.class);
                    sendIntent.putExtra("CityId", resul_.getId());
                    startActivity(sendIntent);
                }
                break;
            case REQUEST_CURRENT_PLACE :
                if(resultCode == Activity.RESULT_OK){
                    AutoCompletePlacesResponse resul_ = new GsonBuilder()
                            .create().fromJson(intent.getStringExtra("places"), AutoCompletePlacesResponse.class);
                    setCurrentCity(resul_);
                }
                break;
        }
    }

    /**
     * Starts the user location updates
     */
    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, REQUEST, this);
        }catch (SecurityException e){}
    }

    /**
     * Stops the user location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Method to fetch the last location for fragments
     * */
    private LatLng fetchLastLocation() {

        try{
            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }catch (SecurityException e){}
        catch(NullPointerException e){}
        return mLastLocation;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = fetchLastLocation();
        if(mLastLocation != null){
            //TODO get city name
            fetchCurrentCity();
        }else{
            startLocationUpdates(); // LocationListener
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //TODO get city name
        fetchCurrentCity();
        stopLocationUpdates();
    }

    @Override
    protected void permissionDenied(@NonNull int[] grantResults) {
        Boolean permissionGrant = true;
        for(int result : grantResults) {
            permissionGrant = permissionGrant && (result == PackageManager.PERMISSION_GRANTED);
        }

        if(permissionGrant){
            //TODO
            fetchCurrentCity();
       }else{
            openAppPermissionScreen(findViewById(R.id.landscreen_content));
        }
    }

    private Call<List<AutoCompletePlacesResponse>> placeDetailCall;
    private Boolean firstTime = true;
    private void fetchCurrentCity(){
        Log.i(TAG, "called fetchCurrentCity(); "+firstTime);
        if(firstTime){
            firstTime = false;
            return;
        }
        Log.i(TAG, "location "+mLastLocation.latitude);
        Log.i(TAG, "location "+mLastLocation.longitude);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    mLastLocation.latitude,
                    mLastLocation.longitude,
                    // In this sample, we get just a single address.
                    1);
            if(addresses.size() > 0){
                Address currentAddress  = addresses.get(0);
                IxigoRetrofitService service = IxIGoClient.getClient().create(IxigoRetrofitService.class);

                placeDetailCall = service.getSearchPlaces(currentAddress.getLocality());
                placeDetailCall.enqueue(new Callback<List<AutoCompletePlacesResponse>>() {
                    @Override
                    public void onResponse(Call<List<AutoCompletePlacesResponse>> call, Response<List<AutoCompletePlacesResponse>> response) {
                        Log.i(TAG, response.body().toString());
                        if(response.isSuccessful()){
                            Log.i(TAG, response.body().size()+" : size");
                            //Log.i(TAG, response.body().get(0).toString());
                            //IxIGoClient.setCurrentCity(response.body().get(0).getId());

                            if(response.body().size() > 1) {

                                MultipleCurrentOptionFragment bottomSheetDialog = new MultipleCurrentOptionFragment();
                                Bundle extra = new Bundle();
                                extra.putString("places", new GsonBuilder()
                                        .create().toJson(response.body(), new TypeToken<List<AutoCompletePlacesResponse>>() {
                                        }.getType()));
                                bottomSheetDialog.setArguments(extra);
                                bottomSheetDialog.show(getSupportFragmentManager(), "MultipleCurrentOptionFragment");
                            }else{
                                Log.i(TAG, response.body().get(0).toString());
                                IxIGoClient.setCurrentCity(response.body().get(0));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AutoCompletePlacesResponse>> call, Throwable t) {
                        Log.e(TAG, t.getLocalizedMessage());
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(TAG, ioException.getLocalizedMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e(TAG, "Latitude = " + mLastLocation.latitude +
                    ", Longitude = " + mLastLocation.longitude, illegalArgumentException);
        }
    }

    @Override
    public void openSearch(View v) {
        startActivityForResult(new Intent(this, PlaceAutocompleteActivity.class), REQUEST_CURRENT_PLACE);
    }

    @Override
    public void setCurrentCity(AutoCompletePlacesResponse city) {
        currentLocationTxt.setText(city.getText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            currentLocationTxt.setTextColor(getResources().getColor(R.color.white, null));
        } else {
            currentLocationTxt.setTextColor(getResources().getColor(R.color.white));
        }
        IxIGoClient.setCurrentCity(city);
    }
}
