package net.tech.tripplanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.nex3z.flowlayout.FlowLayout;
import com.squareup.picasso.Picasso;

import net.tech.tripplanner.clientinstance.IxIGoClient;
import net.tech.tripplanner.dataprovider.IxigoRetrofitService;
import net.tech.tripplanner.fragments.MultipleCurrentOptionFragment;
import net.tech.tripplanner.helper.AppSession;
import net.tech.tripplanner.model.AutoCompletePlacesResponse;
import net.tech.tripplanner.model.POIDetailsPlaceResponse;
import net.tech.tripplanner.model.Result;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlaceDetailActivity extends BaseActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, MultipleCurrentOptionFragment.TapListener{

    private final String TAG = getClass().getSimpleName();
    private Picasso picBuilder;
    private ImageView place_ImageView;
    private FlowLayout categoriesNameTextView;
    private Call<POIDetailsPlaceResponse> placeDetailCall;


    private TextView placeNameTextView, addressTextView, priceTagTextView, descriptionTextView, whyTovisitTextView, websiteTextView;
    private ProgressBar image_loading_indicator;

    private Result place;


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

    private AppSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new AppSession(this);

        //biuld client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this).build();

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
                    place = response.body().getData();
                    processdetail();
                }
            }

            @Override
            public void onFailure(Call<POIDetailsPlaceResponse> call, Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firstTime = true;
        if (IxIGoClient.getCurrentCity() == null) {
            mGoogleApiClient.connect();
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
                                PlaceDetailActivity.this,
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

    private Call<List<AutoCompletePlacesResponse>> placeautoplaceCall;
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

                placeautoplaceCall = service.getSearchPlaces(currentAddress.getLocality());
                placeautoplaceCall.enqueue(new Callback<List<AutoCompletePlacesResponse>>() {
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
        IxIGoClient.setCurrentCity(city);
        //TODO
        openDirection(findViewById(R.id.linearlayout));
    }

    private void processdetail(){
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

    public void openWebSite(View v){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(place.getUrl()));
        startActivity(i);
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

    public void openDirection(View v){
        try {
            Log.i(TAG, IxIGoClient.getCurrentCity().getId() + "==" + place.getCityId());
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" +
                            IxIGoClient.getCurrentCity().getLat() + "," + IxIGoClient.getCurrentCity().getLon()
                            + "&daddr=" + place.getLatitude() + "," + place.getLongitude()));
            startActivity(intent);
        }catch(NullPointerException e){
            missingCurrentLocation();
        }
    }


    public void missingCurrentLocation(){
        Snackbar.make(findViewById(R.id.linearlayout), "Current City not Set", Snackbar.LENGTH_INDEFINITE)
                .setAction("Set Location", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myLocation(view);
                    }
                }).show();
    }
}
