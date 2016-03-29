package ie.markomeara.irelandtraintimes.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import ie.markomeara.irelandtraintimes.Injector;
import ie.markomeara.irelandtraintimes.manager.DatabaseOrmHelper;
import ie.markomeara.irelandtraintimes.ui.fragment.StationListFragment;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.StationList;

public class RetrieveStationsTask extends AsyncTask<Boolean, Integer, Boolean> {

    private static final String TAG = RetrieveStationsTask.class.getSimpleName();

    @Inject
    IrishRailService irishRailService;

    @Inject
    DatabaseOrmHelper databaseHelper;

    private StationListFragment stationListFragment;

    public RetrieveStationsTask(StationListFragment fragment) throws SQLException {
        Injector.inject(this);
        this.stationListFragment = fragment;
        setDatabaseHelper(fragment.getActivity());

    }

    @Override
    protected Boolean doInBackground(Boolean[] updateUIParam) {

        boolean updateUI = false;
        if(updateUIParam.length > 0){
            updateUI = updateUIParam[0];
        }

        // TODO Handle timeout if there's no or weak internet
        StationList stationList = irishRailService.getAllStations();
        List<Station> stations = stationList.getStationList();

        storeStationsInDatabase(stations);

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

    private void setDatabaseHelper(Context ctx) throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(ctx, DatabaseOrmHelper.class);
    }

    private void storeStationsInDatabase(List<Station> stations){
        try {
            Dao<Station, Integer> stationDao = databaseHelper.getStationDao();

            if (!stations.isEmpty()) {
                for(Station station : stations){
                    try {
                        Log.d(TAG, station.toString());
                        stationDao.createOrUpdate(station);
                    } catch (SQLException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


}
