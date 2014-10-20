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
import ie.markomeara.irelandtraintimes.adapters.FavListAdapter;
import ie.markomeara.irelandtraintimes.db.StationsDataSource;
import ie.markomeara.irelandtraintimes.utils.StationUtils;


public class HomeActivity extends Activity {

    ListView favList;
    String[][] favStations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        List<Station> stns = null;
        StationsDataSource sds = new StationsDataSource(this);
        try {
            sds.open();
            stns = sds.getAllStations();
            sds.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(stns == null){
            Station station = new Station(123, "Portmarnock", "Porto", 1.23, 3.21, "pnock");
            stns.add(station);
        }

        favList = (ListView) findViewById(R.id.favlist);

        ArrayList favStationList = new ArrayList<String[]>();
       // favList.setAdapter();
        for(int i = 0; i < stns.size(); i++){
            favStationList.add(new String[]{stns.get(i).getName(), stns.get(i).getAlias()});
            //Test
        }

        FavListAdapter adapter = new FavListAdapter(this, favStationList);
        favList.setAdapter(adapter);

        favList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id){

                System.out.println("Clicked: " + position);
                RelativeLayout clickedItemLayout = (RelativeLayout) clickedItem;
                TextView stationNameView = (TextView) clickedItemLayout.getChildAt(0);
                String stationName = stationNameView.getText().toString();
                System.out.println(stationName);

            }
        });
        //       populateFavList();
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
}
