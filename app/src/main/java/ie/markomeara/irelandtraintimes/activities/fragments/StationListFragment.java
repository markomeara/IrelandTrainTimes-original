package ie.markomeara.irelandtraintimes.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.ListHelpers.adapters.StationListAdapter;
import ie.markomeara.irelandtraintimes.db.StationsDataSource;
import ie.markomeara.irelandtraintimes.exceptions.DBNotAvailableException;
import ie.markomeara.irelandtraintimes.networktasks.RetrieveStationsTask;

/**
 * Created by Mark on 27/10/2014.
 */
public class StationListFragment extends Fragment {

    private static final String TAG = StationListFragment.class.getSimpleName();

    private EditText stationSearchField_ET;
    private ListView stationListView;
    private TextView stationsLoadingTV;
    private List<Station> stationList = null;
    private StationListAdapter stationListAdapter;

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

        getActivity().getActionBar().setTitle(R.string.app_name);

        stationSearchField_ET = (EditText) getView().findViewById(R.id.stationSearchField);
        stationListView = (ListView) getView().findViewById(R.id.stationlist);
        stationsLoadingTV = (TextView) getView().findViewById(R.id.loadingStationsTV);
        stationListAdapter = new StationListAdapter(getActivity(), stationList);

        // TODO Figure out when stations should be refreshed... not that often obviously
        initStationListDisplay();

        monitorStationNameInput();

        // Updating stations from API

        // If we are not showing any stations, tell this to Stations task
        // so it knows to update view when it's finished
        boolean initializingStationsList = (stationListView.getCount() == 0);
        new RetrieveStationsTask(this).execute(initializingStationsList);

    }

    // TODO Think about lifecycle and how we refresh data when user goes back to home screen
    public void initStationListDisplay(){

        StationsDataSource sds = new StationsDataSource(getActivity());

        // Retrieving stations from DB
        try {
            sds.open();
            stationList = sds.retrieveAllStations();
            sds.close();
        } catch (SQLException ex) {
            Log.e(TAG, ex.toString(), ex);
        } catch(DBNotAvailableException ex){
            Log.e(TAG, ex.toString(), ex);
        }
        if(!stationList.isEmpty()) {

            stationListAdapter = new StationListAdapter(getActivity(), stationList);
            stationListView.setAdapter(stationListAdapter);

            stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id) {
                    Station selectedStation = (Station) parent.getAdapter().getItem(position);
                    listener.onStationSelected(selectedStation);
                }
            });

            stationsLoadingTV.setVisibility(View.GONE);
        }
    }

    private void monitorStationNameInput(){

        TextWatcher stationSearchFieldListener = new TextWatcher(){

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null) {
                    stationListAdapter.getFilter().filter(s.toString());
                }
            }};

        stationSearchField_ET.addTextChangedListener(stationSearchFieldListener);
        stationListView.setTextFilterEnabled(true);
    }

    public interface OnStationSelectedListener {
        public void onStationSelected(Station station);
    }

}
