package ie.markomeara.irelandtraintimes.network;

import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import ie.markomeara.irelandtraintimes.manager.DatabaseOrmHelper;
import ie.markomeara.irelandtraintimes.ui.fragment.StationListFragment;
import ie.markomeara.irelandtraintimes.model.Station;

public class RetrieveStationsTask extends AsyncTask<Boolean, Integer, Boolean> {

    private static final String TAG = RetrieveStationsTask.class.getSimpleName();

    @Inject
    IrishRailApi mIrishRailApi;

    @Inject
    protected DatabaseOrmHelper mDatabaseHelper;

    private StationListFragment mStationListFragment;

    public RetrieveStationsTask(StationListFragment fragment, DatabaseOrmHelper databaseOrmHelper, IrishRailApi irishRailApi) throws SQLException {
        mStationListFragment = fragment;
        mDatabaseHelper = databaseOrmHelper;
        mIrishRailApi = irishRailApi;
    }

    @Override
    protected Boolean doInBackground(Boolean[] updateUIParam) {

        boolean updateUI = false;
        if(updateUIParam.length > 0){
            updateUI = updateUIParam[0];
        }

        // TODO Handle timeout / exception if there's no or weak internet
        try {
            copyStationsFromApiToDatabase();
        }
        catch(RuntimeException ex){
            Log.e(TAG, ex.getMessage(), ex);
        }

        return updateUI;
    }

    @Override
    protected void onPostExecute(Boolean updateUIImmediately) {
        // If station list is being initialized for first time, then refresh UI immediately
        if(updateUIImmediately){
            mStationListFragment.refreshStationListDisplay();
        }
        Log.i(TAG, "Stations have been updated");
        // TODO This returns null pointer exception if we've already changed activity
        // .... but how can this be if it was passed updateUIImmediately (as this should only be passed
        // when no stations are displayed?!?

    }

    private void copyStationsFromApiToDatabase() {

        try {
            List<Station> stations = mIrishRailApi.getAllStations().execute().body().getStationList();
            storeStationsInDatabase(stations);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void storeStationsInDatabase(List<Station> stations){
        try {
            Dao<Station, Integer> stationDao = mDatabaseHelper.getStationDao();

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
