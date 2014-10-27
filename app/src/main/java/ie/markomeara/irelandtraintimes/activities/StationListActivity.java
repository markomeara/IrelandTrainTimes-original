package ie.markomeara.irelandtraintimes.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.activities.fragments.TwitterUpdateFragment;
import ie.markomeara.irelandtraintimes.adapters.StationListAdapter;
import ie.markomeara.irelandtraintimes.db.StationsDataSource;
import ie.markomeara.irelandtraintimes.networktasks.RetrieveStationsTask;
import ie.markomeara.irelandtraintimes.networktasks.TwitterTask;


public class StationListActivity extends Activity implements TwitterUpdateFragment.OnFragmentInteractionListener {

    private ListView stationListView;
    private TextView stationsLoadingTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationlist);

        stationListView = (ListView) findViewById(R.id.stationlist);
        stationsLoadingTV = (TextView) findViewById(R.id.loadingStationsTV);

        // TODO Figure out when stations should be refreshed... not that often obviously
        refreshStationListDisplay();

        // Updating stations from API

        // If we are not showing any stations, tell this to Stations task
        // so it knows to update view when it's finished
        boolean initializingStationsList = (stationListView.getCount() == 0);
        new RetrieveStationsTask(this).execute(initializingStationsList);

        // Update tweets
        new TwitterTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO Think about lifecycle and how we refresh data when user goes back to home screen

    public void refreshStationListDisplay(){

        List<Station> stationList = null;
        StationsDataSource sds = new StationsDataSource(this);

        // Retrieving stations from DB
        try {
            sds.open();
            stationList = sds.retrieveAllStations();
            sds.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!stationList.isEmpty()) {
            stationListView.setAdapter(new StationListAdapter(this, stationList));
            stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id) {

                    Station station = (Station) parent.getItemAtPosition(position);
                    Intent i = new Intent(StationListActivity.this, StationNextTrainsActivity.class);
                    i.putExtra("stationId", station.getId());
                    startActivity(i);

                }
            });
            stationsLoadingTV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO Implement
    }
}
