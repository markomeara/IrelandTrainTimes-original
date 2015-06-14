package ie.markomeara.irelandtraintimes.trains;

import android.os.AsyncTask;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandtraintimes.activities.fragments.StationListFragment;
import ie.markomeara.irelandtraintimes.db.DBNotAvailableException;


/**
 * Created by Mark on 30/09/2014.
 */
public class RetrieveStationsTask extends AsyncTask<Boolean, Integer, Boolean> {

    private static final String TAG = RetrieveStationsTask.class.getSimpleName();

    private StationListFragment stationListFragment;


    public RetrieveStationsTask(StationListFragment fragment){ this.stationListFragment = fragment; }

    @Override
    protected Boolean doInBackground(Boolean[] updateUIParam) {

        boolean updateUI = false;
        if(updateUIParam.length > 0){
            updateUI = updateUIParam[0];
        }

        try {

            List<Station> stations = IrishRailAPI.getAllStations();

            // Using 130 as arbitrary value to just ensure we probably did get all the stations and not just rubbish
            if (stations.size() > 130) {
                StationsDataSource sds = new StationsDataSource(stationListFragment.getActivity());
                sds.updateStoredStations(stations);
            }

         }

        catch (MalformedURLException ex) { Log.w(TAG, ex); }
        catch (ParserConfigurationException ex) { Log.w(TAG, ex); }
        catch (IOException ex) { Log.w(TAG, ex); }
        catch (SAXException ex) { Log.w(TAG, ex); }

        return updateUI;
    }

    @Override
    protected void onPostExecute(Boolean updateUIImmediately) {
        // If station list is being initialized for first time, then refresh UI immediately
        if(updateUIImmediately){
            stationListFragment.refreshStationListDisplay();
        }
        Log.i(TAG, "Stations have been updated");
        // TODO This returns null pointer exception if we've already changed activity
        // .... but how can this be if it was passed updateUIImmediately (as this should only be passed
        // when no stations are displayed?!?

    }



}
