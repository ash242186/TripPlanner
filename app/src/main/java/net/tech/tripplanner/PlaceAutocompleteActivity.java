package net.tech.tripplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import net.tech.tripplanner.adapter.PlacesAutoCompleteAdapter;
import net.tech.tripplanner.clientinstance.IxIGoClient;
import net.tech.tripplanner.dataprovider.IxigoRetrofitService;
import net.tech.tripplanner.model.AutoCompletePlacesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ashwini on 4/8/2017.
 */

public class PlaceAutocompleteActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private EditText myEditText;
    private RecyclerView suggestionList;
    private PlacesAutoCompleteAdapter adapter;
    private Call<List<AutoCompletePlacesResponse>> autocompletePlacesCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autocomplete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        suggestionList = (RecyclerView) findViewById(R.id.suggestionList);
        suggestionList.setLayoutManager(new LinearLayoutManager(this));
        suggestionList.setHasFixedSize(true);
        myEditText = (EditText) findViewById(R.id.myEditText);
        adapter = new PlacesAutoCompleteAdapter(this);
        suggestionList.setAdapter(adapter);
        myEditText.addTextChangedListener(textWatcher);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = myEditText.getText().toString().trim();
            if(text.length() > 3){
                IxigoRetrofitService service = IxIGoClient.getClient().create(IxigoRetrofitService.class);
                autocompletePlacesCall =  service.getSearchPlaces(text);
                autocompletePlacesCall.enqueue(new Callback<List<AutoCompletePlacesResponse>>() {
                    @Override
                    public void onResponse(Call<List<AutoCompletePlacesResponse>> call, Response<List<AutoCompletePlacesResponse>> response) {
                        if(response.isSuccessful()){
                            Log.i(TAG, response.body().toString());
                            adapter.updateData(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AutoCompletePlacesResponse>> call, Throwable t) {

                    }
                });
            }
        }
    };
}
