package ie.markomeara.irelandtraintimes.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.sql.SQLException;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.db.StationsDataSource;

public class StationNextTrainsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_next_trains);

        // TODO Handle if station is null
        Station station = getStationFromIntent();
        Toast toast = Toast.makeText(this, station.getName(), Toast.LENGTH_LONG);
        toast.show();

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
}
