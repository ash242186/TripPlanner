package net.tech.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LandActivity extends AppCompatActivity {

    private View emptyRecyclerView;
    private TextView emptytextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emptyRecyclerView = findViewById(R.id.emptyRecyclerView);
        emptyRecyclerView.setVisibility(View.VISIBLE);
        emptytextView = (TextView) findViewById(R.id.emptytextView);
        emptytextView.setText(getString(R.string.emptyplaces, "Search History \n Please search new place"));
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
