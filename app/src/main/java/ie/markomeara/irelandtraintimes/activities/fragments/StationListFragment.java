package ie.markomeara.irelandtraintimes.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.adapters.StationListAdapter;
import ie.markomeara.irelandtraintimes.db.StationsDataSource;
import ie.markomeara.irelandtraintimes.networktasks.RetrieveStationsTask;

/**
 * Created by Mark on 27/10/2014.
 */
public class StationListFragment extends Fragment {

    private static final String TAG = StationListFragment.class.getSimpleName();

    private ListView stationListView;
    private TextView stationsLoadingTV;

    private OnStationSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stationlist, container, false);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            listener = (OnStationSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStationSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        stationListView = (ListView) getView().findViewById(R.id.stationlist);
        stationsLoadingTV = (TextView) getView().findViewById(R.id.loadingStationsTV);

        // TODO Figure out when stations should be refreshed... not that often obviously
        refreshStationListDisplay();

        // Updating stations from API

        // If we are not showing any stations, tell this to Stations task
        // so it knows to update view when it's finished
        boolean initializingStationsList = (stationListView.getCount() == 0);
        new RetrieveStationsTask(this).execute(initializingStationsList);

        // Update tweets
    //    new TwitterTask(getActivity()).execute();
    }

    // TODO Think about lifecycle and how we refresh data when user goes back to home screen

    public void refreshStationListDisplay(){

        List<Station> stationList = null;
        StationsDataSource sds = new StationsDataSource(getActivity());

        // Retrieving stations from DB
        try {
            sds.open();
            stationList = sds.retrieveAllStations();
            sds.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!stationList.isEmpty()) {

            stationListView.setAdapter(new StationListAdapter(getActivity(), stationList));

            stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id) {
                    Station selectedStation = (Station) parent.getItemAtPosition(position);
                    goToNextTrainsFragment(selectedStation);
                }
            });

            stationsLoadingTV.setVisibility(View.GONE);
        }
    }

    private void goToNextTrainsFragment(Station station){
        listener.onStationSelectedListener(station);
    }

    public interface OnStationSelectedListener {
        public void onStationSelectedListener(Station station);
    }

}
