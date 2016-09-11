package ie.markomeara.irelandtraintimes.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.Injector;
import ie.markomeara.irelandtraintimes.adapter.StationRecyclerViewAdapter;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.model.StationList;
import ie.markomeara.irelandtraintimes.network.ApiCallback;
import ie.markomeara.irelandtraintimes.network.IrishRailService;
import ie.markomeara.irelandtraintimes.model.Station;

public class StationListFragment extends Fragment {

    private static final String TAG = StationListFragment.class.getSimpleName();

    @Bind(R.id.stationlistRV)
    protected RecyclerView mStationRecyclerView;
    @Bind(R.id.loadingStations_progress)
    protected ProgressBar mLoadingStationsProgressBar;
    @Bind(R.id.stationListToolbar)
    protected Toolbar mToolbar;

    @Inject
    IrishRailService mIrishRailService;

    @Inject
    EventBus mEventBus;

    private List<Station> mAllStations;
    private StationRecyclerViewAdapter mStationRecyclerViewAdapter;
    private OnStationClickedListener mListener;
    private AppCompatActivity mParentActivity;

    public StationListFragment(){
        Injector.get().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stationlist, container, false);
        ButterKnife.bind(this, view);
        mEventBus.register(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated called");
        super.onActivityCreated(savedInstanceState);
        mParentActivity = (AppCompatActivity) getActivity();
        try {
            mListener = (OnStationClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnStationSelectedListener");
        }
        initStationList();
        mIrishRailService.fetchAllStations(false);
        configureToolbar();
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

    @Override
    public void onStop() {
        super.onStop();
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initStationList() {
        mStationRecyclerViewAdapter = new StationRecyclerViewAdapter(Lists.<Station>newArrayList(), mListener);
        mStationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStationRecyclerView.setAdapter(mStationRecyclerViewAdapter);
    }

    @Subscribe
    private void onLatestStationListReceived(StationList stationList) {
        List<Station> stations = stationList.getStationList();
        Collections.sort(stations);
        mAllStations = stations;
        mStationRecyclerViewAdapter.updateDataSet(mAllStations);
        mLoadingStationsProgressBar.setVisibility(View.GONE);

    }

    @Subscribe
    private void onStationRetrievalError(ApiCallback.ApiFailureEvent apiFailureEvent) {
        Toast toastMsg = new Toast(getActivity());
        toastMsg.setText("No stations could be retrieved from Irish Rail website.");
        toastMsg.setDuration(Toast.LENGTH_LONG);
        toastMsg.show();

        mLoadingStationsProgressBar.setVisibility(View.GONE);
    }

    public interface OnStationClickedListener {
        void onStationSelected(Station station);
    }

    private void configureToolbar(){
        mParentActivity.setSupportActionBar(mToolbar);
        mToolbar.setTitle(null);
        ActionBar actionBar = mParentActivity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        else{
            Log.w(TAG, "Action bar is null");
        }
    }
}
