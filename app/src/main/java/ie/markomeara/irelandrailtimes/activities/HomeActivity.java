package ie.markomeara.irelandrailtimes.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandrailtimes.R;
import ie.markomeara.irelandrailtimes.adapters.FavListAdapter;
import ie.markomeara.irelandrailtimes.utils.StationUtils;


public class HomeActivity extends Activity {

    ListView favList;
    String[][] favStations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        favList = (ListView) findViewById(R.id.favlist);
        favStations = new String[][]{
                {"Portmarnock", "1km"},
                {"Malahide", "2km"},
                {"Dublin Pearse", "8km"},
                {"Dublin Connolly", "6km"},
                {"Tara Street", "7km"},
                {"Grand Canal Dock", "10km"},
                {"Donabate", ""},
                {"Rush & Lusk", ""},
                {"Skerries", ""},
                {"Gormanstown", ""},
                {"Laytown", ""},
                {"Drogheda", ""},
                {"Dundalk", ""}
            };
        ArrayList favStationList = new ArrayList<String[]>();
        for(int i = 0; i < favStations.length; i++){
            favStationList.add(favStations[i]);
            //Test
        }

        FavListAdapter adapter = new FavListAdapter(this, favStationList);
        favList.setAdapter(adapter);

        StationUtils.getAllStations();

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

    private void populateFavList(){
       // favList.setAdapter();
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
