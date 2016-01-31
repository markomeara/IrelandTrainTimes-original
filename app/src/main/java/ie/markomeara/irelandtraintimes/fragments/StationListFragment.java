package ie.markomeara.irelandtraintimes.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.views.adapters.StationRecyclerViewAdapter;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.network.RetrieveStationsTask;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.network.StationsDataSource;

/**
 * Created by Mark on 27/10/2014.
 */
public class StationListFragment extends Fragment {

    private static final String TAG = StationListFragment.class.getSimpleName();

    @Bind(R.id.stationlistRV)
    RecyclerView mStationRecyclerView;
    @Bind(R.id.loadingStations_progress)
    ProgressBar mLoadingStationsProgressBar;

    private List<Station> mAllStations;
    private StationRecyclerViewAdapter mStationRecyclerViewAdapter;
    private OnStationClickedListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stationlist, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity){
        Log.d(TAG, "onAttach called");
        super.onAttach(activity);

        try {
            mListener = (OnStationClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStationSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated called");
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        ActionBar actionBar = parentActivity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        else{
            Log.e(TAG, "Action bar is null");
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart called");
        super.onStart();
        loadStoredStationData();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume called");
        super.onResume();
        // TODO Should some of this be in onStart??
        mStationRecyclerViewAdapter = new StationRecyclerViewAdapter(mAllStations, mListener);
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
            mLoadingStationsProgressBar.setVisibility(View.GONE);
        }
        updateStoredStationsFromAPI(immediateDisplayRefresh);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.base_activity_actions, menu);

        SearchView mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setQueryHint(getActivity().getString(R.string.action_search_stations));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                mStationRecyclerViewAdapter.filter(newText);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
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
        mLoadingStationsProgressBar.setVisibility(View.GONE);
    }

    public interface OnStationClickedListener {
        void onStationSelected(Station station);
    }

    private void loadStoredStationData(){
        // TODO Populate station data
        mAllStations = new ArrayList<>();
        StationsDataSource sds = new StationsDataSource(getActivity());
        mAllStations = sds.retrieveAllStations();
    }
}
