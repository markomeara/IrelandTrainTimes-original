package ie.markomeara.irelandtraintimes.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.sql.SQLException;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.activities.fragments.TwitterUpdateFragment;
import ie.markomeara.irelandtraintimes.db.StationsDataSource;
import ie.markomeara.irelandtraintimes.networktasks.NextTrainsTask;

public class StationNextTrainsActivity extends Activity implements TwitterUpdateFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_next_trains);

        // TODO Handle if station is null
        Station station = getStationFromIntent();
        TextView trainsDueTV = (TextView) findViewById(R.id.trainsDue);

        AsyncTask ntt = new NextTrainsTask(this, trainsDueTV).execute(station);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.station_next_trains, menu);
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

    private Station getStationFromIntent(){

        Station retrievedStation = null;

        try {
            Intent callingIntent = getIntent();
            Bundle extras = callingIntent.getExtras();

            // TODO Use constant
            if (extras.containsKey("stationId")) {

                long id = (Long) extras.get("stationId");
                StationsDataSource sds = new StationsDataSource(this);
                sds.open();
                retrievedStation = sds.retrieveStationById(id);
                sds.close();

            } else {
                Log.e("Creating next trains activity", "No station info received");
            }
        }
        catch(SQLException ex){
            Log.e("Creating next trains activity", "SQL exception occurred", ex);
        }

        return retrievedStation;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO Implement
    }
}
