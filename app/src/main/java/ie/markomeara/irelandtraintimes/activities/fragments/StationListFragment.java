package ie.markomeara.irelandtraintimes.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.ListHelpers.adapters.StationRecyclerViewAdapter;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.trains.RetrieveStationsTask;
import ie.markomeara.irelandtraintimes.trains.Station;
import ie.markomeara.irelandtraintimes.trains.StationsDataSource;
import ie.markomeara.irelandtraintimes.db.DBNotAvailableException;

/**
 * Created by Mark on 27/10/2014.
 */
public class StationListFragment extends Fragment {

    private static final String TAG = StationListFragment.class.getSimpleName();

    private EditText stationSearchField_ET;
    private RecyclerView mStationRecyclerView;
    private List<Station> mAllStations;
    private StationRecyclerViewAdapter mStationRecyclerViewAdapter;
    private TextView stationsLoadingTV;
    private OnStationClickedListener listener;

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
            listener = (OnStationClickedListener) activity;
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
        mStationRecyclerView = (RecyclerView) getView().findViewById(R.id.stationlistRV);
        stationsLoadingTV = (TextView) getView().findViewById(R.id.loadingStationsTV);
    }

    @Override
    public void onStart(){
        super.onStart();
        loadStoredStationData();
    }

    @Override
    public void onResume(){
        super.onResume();
        mStationRecyclerViewAdapter = new StationRecyclerViewAdapter(mAllStations, listener);
        mStationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStationRecyclerView.setAdapter(mStationRecyclerViewAdapter);

        boolean immediateDisplayRefresh = false;
        if(mAllStations.isEmpty()){
            // If no stations are being displayed to user then update the display
            // as soon as API returns with latest list of stations
            immediateDisplayRefresh = true;
        }
        else{
            // Don't show 'Initializing stations' text if stations are being shown
            stationsLoadingTV.setVisibility(View.GONE);
        }
        updateStoredStationsFromAPI(immediateDisplayRefresh);
        monitorStationNameInput();
    }

    public void updateStoredStationsFromAPI(boolean updateUIWhenComplete){
        // TODO Network connection check
        new RetrieveStationsTask(this).execute(updateUIWhenComplete);
    }

    // TODO Think about lifecycle and how we refresh data when user goes back to home screen
    public void refreshStationListDisplay(){

        loadStoredStationData();
        if(!mAllStations.isEmpty()) {
            mStationRecyclerViewAdapter.updateDataSet(mAllStations);
        }
        else{
            Toast toastMsg = new Toast(getActivity());
            toastMsg.setText("No stations could be retrieved from Irish Rail website.");
            toastMsg.setDuration(Toast.LENGTH_LONG);
            toastMsg.show();
        }
        stationsLoadingTV.setVisibility(View.GONE);
    }

    private void monitorStationNameInput(){

        TextWatcher stationSearchFieldListener = new TextWatcher(){

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null) {
                    mStationRecyclerViewAdapter.filter(s.toString());
                }
            }};

        stationSearchField_ET.addTextChangedListener(stationSearchFieldListener);
    }

    public interface OnStationClickedListener {
        void onStationSelected(Station station);
    }

    private void loadStoredStationData(){
        // TODO Populate station data
        mAllStations = new ArrayList<Station>();
        StationsDataSource sds = new StationsDataSource(getActivity());
        try {
            sds.open();
            mAllStations = sds.retrieveAllStations();
            sds.close();
        }
        catch(SQLException ex){
            Log.e(TAG, ex.getMessage());
        }
        catch(DBNotAvailableException ex){
            Log.e(TAG, ex.getMessage());
        }
    }
}
