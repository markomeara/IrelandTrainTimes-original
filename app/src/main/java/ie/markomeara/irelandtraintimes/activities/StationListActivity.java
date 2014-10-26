package ie.markomeara.irelandtraintimes.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.adapters.StationListAdapter;
import ie.markomeara.irelandtraintimes.db.StationsDataSource;
import ie.markomeara.irelandtraintimes.networktasks.TwitterTask;
import ie.markomeara.irelandtraintimes.utils.StationUtils;


public class StationListActivity extends Activity {

    private ListView stationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new TwitterTask(this).execute();

        // TODO Resolve possible overlap between writing/clearing DB and also reading from it below?
        // Do writes only take place when connection is closed??

        // Updating stations from API
        StationUtils.getAllStations(this);

        // TODO Figure out when stations should be refreshed... not that often obviously
        refreshStationListDisplay();
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

    private void refreshStationListDisplay(){

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

        stationListView = (ListView) findViewById(R.id.stationlist);
        stationListView.setAdapter(new StationListAdapter(this, stationList));

        stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id){

                Station station = (Station) parent.getItemAtPosition(position);
                Intent i = new Intent(StationListActivity.this, StationNextTrainsActivity.class);
                i.putExtra("stationId", station.getId());
                startActivity(i);

            }
        });
    }
}
