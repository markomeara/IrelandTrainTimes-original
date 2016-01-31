package ie.markomeara.irelandtraintimes.network;

import android.os.AsyncTask;
import android.util.Log;

import com.mobprofs.retrofit.converters.SimpleXmlConverter;

import java.util.List;

import ie.markomeara.irelandtraintimes.fragments.StationListFragment;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.StationList;
import retrofit.RestAdapter;


/**
 * Created by Mark on 30/09/2014.
 */
public class RetrieveStationsTask extends AsyncTask<Boolean, Integer, Boolean> {

    private static final String TAG = RetrieveStationsTask.class.getSimpleName();

    private StationListFragment stationListFragment;


    public RetrieveStationsTask(StationListFragment fragment){ this.stationListFragment = fragment; }

    @Override
    protected Boolean doInBackground(Boolean[] updateUIParam) {

        // TODO Move this creation to common static variable along with other creation
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.irishrail.ie/realtime/realtime.asmx")
                .setConverter(new SimpleXmlConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        IrishRailService irishRailService = restAdapter.create(IrishRailService.class);

        boolean updateUI = false;
        if(updateUIParam.length > 0){
            updateUI = updateUIParam[0];
        }

        // TODO Handle timeout if there's no or weak internet
        StationList stationList = irishRailService.getAllStations();
        List<Station> stations = stationList.getStationList();

        // Using 130 as arbitrary value to just ensure we probably did get all the stations and not just rubbish
        if (stations.size() > 130) {
            StationsDataSource sds = new StationsDataSource(stationListFragment.getActivity());
            sds.updateStoredStations(stations);
        }

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
