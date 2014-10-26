package ie.markomeara.irelandtraintimes.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        List<Station> stns = null;
        StationsDataSource sds = new StationsDataSource(this);
        // Retrieving stations from DB
        try {
            sds.open();
            stns = sds.getAllStations();
            sds.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(stns == null){
            stns = new ArrayList<Station>();
            Station station = new Station(123, "Portmarnock", "Porto", 1.23, 3.21, "pnock");
            stns.add(station);
        }

        stationListView = (ListView) findViewById(R.id.stationlist);

        ArrayList stationList = new ArrayList<String[]>();

        for(int i = 0; i < stns.size(); i++){
            stationList.add(new String[]{stns.get(i).getName(), stns.get(i).getAlias()});
            //Test
        }

        StationListAdapter adapter = new StationListAdapter(this, stationList);
        stationListView.setAdapter(adapter);

        stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id){

                System.out.println("Clicked: " + position);
                RelativeLayout clickedItemLayout = (RelativeLayout) clickedItem;
                TextView stationNameView = (TextView) clickedItemLayout.getChildAt(0);
                String stationName = stationNameView.getText().toString();
                System.out.println(stationName);

            }
        });
    }
}
