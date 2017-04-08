package net.tech.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.tech.tripplanner.adapter.HistoryPlacesPageAdapter;
import net.tech.tripplanner.helper.AppSession;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class LandActivity extends AppCompatActivity {

    private View emptyRecyclerView;
    private TextView emptytextView;

    private AppSession session;
    private ViewPager historyRecyclerView;
    private CircleIndicator indicator;
    private HistoryPlacesPageAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new AppSession(this);

        emptyRecyclerView = findViewById(R.id.emptyRecyclerView);
        emptytextView = (TextView) findViewById(R.id.emptytextView);

        historyRecyclerView = (ViewPager) findViewById(R.id.historyRecyclerView);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                startActivity(new Intent(this, PlaceAutocompleteActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
