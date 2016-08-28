package ie.markomeara.irelandtraintimes.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.Injector;
import ie.markomeara.irelandtraintimes.manager.DatabaseOrmHelper;
import ie.markomeara.irelandtraintimes.model.TrainListHeader;
import ie.markomeara.irelandtraintimes.model.TrainListItem;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.Train;
import ie.markomeara.irelandtraintimes.network.NextTrainsTask;
import ie.markomeara.irelandtraintimes.adapter.TrainsDueRecyclerViewAdapter;

public class StationNextTrainsFragment extends Fragment {

    private static final String TAG = StationNextTrainsFragment.class.getSimpleName();
    private static final String STATION_PARAM = "stationId";
    private static final String NORTHBOUND = "Northbound";
    private static final String SOUTHBOUND = "Southbound";

    private Station mDisplayedStation;
    private AppCompatActivity mParentActivity;
    private OnTrainSelectedListener mOnTrainSelectedListener;
    private LayoutInflater mLayoutInflater;
    private TrainsDueRecyclerViewAdapter mTrainsDueRecyclerViewAdapter;

    @Bind(R.id.trainsDueAtStation_RV)
    protected RecyclerView mTrainsDueRV;
    @Bind(R.id.trainsDue_loading_TV)
    protected TextView mStatusMsgTv;   // TODO Switch to switcher view
    @Bind(R.id.nexttrains_loading)
    protected ProgressBar mNextTrainsProgressBar;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Inject
    protected DatabaseOrmHelper mDatabaseHelper;

    public StationNextTrainsFragment(){
        Injector.inject(this);
    }

    public static StationNextTrainsFragment newInstance(Station selectedStation) {
        StationNextTrainsFragment fragment = new StationNextTrainsFragment();
        Bundle args = new Bundle();
        args.putInt(STATION_PARAM, selectedStation.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int stationId = getArguments().getInt(STATION_PARAM);
            try {
                Dao<Station, Integer> stationDao =  mDatabaseHelper.getStationDao();
                mDisplayedStation = stationDao.queryForId(stationId);
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        mLayoutInflater = inflater;
        View view = mLayoutInflater.inflate(R.layout.fragment_station_next_trains, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentActivity = (AppCompatActivity) getActivity();
        configureToolbar();
        mNextTrainsProgressBar.setVisibility(View.VISIBLE);
        AsyncTask<Station, Integer, List<Train>> nextTrainsTask = new NextTrainsTask(this);
        nextTrainsTask.execute(mDisplayedStation);

        if(mParentActivity instanceof OnTrainSelectedListener){
            mOnTrainSelectedListener = (OnTrainSelectedListener) mParentActivity;
        }
        else{
            Log.e(TAG, "Parent activity is not instance of OnTrainSelectedListener");
        }
    }

    // Called by NextTrainsTask
    public void displayTimes(List<Train> trainsDue){

        // TODO Move setting activity view to oncreate/onresume
        View activityView = getView();
        if(activityView != null) {
            mNextTrainsProgressBar.setVisibility(View.GONE);
            if (trainsDue != null && !trainsDue.isEmpty()) {
                List<TrainListItem> trainListItems = constructTrainListItems(trainsDue);

                mTrainsDueRecyclerViewAdapter = new TrainsDueRecyclerViewAdapter(trainListItems, this);
                mTrainsDueRV.setLayoutManager(new LinearLayoutManager(getActivity()));
                mTrainsDueRV.setAdapter(mTrainsDueRecyclerViewAdapter);
            } else {
                showNoResultsMessage();
            }
        }
    }

    @NonNull
    private List<TrainListItem> constructTrainListItems(List<Train> trainsDue) {

        boolean northSouthOnly = trainDirectionsAreNorthOrSouth(trainsDue);

        if(northSouthOnly) {
            return generateTrainItemsWithDirectionHeadings(trainsDue);
        } else {
            return generateItemsWithStandardHeading(trainsDue);
        }

    }

    private boolean trainDirectionsAreNorthOrSouth(List<Train> trainsDue){

        for(Train train : trainsDue){
            if(!train.getDirection().equalsIgnoreCase(NORTHBOUND)
                    && !train.getDirection().equalsIgnoreCase(SOUTHBOUND)){
                return false;
            }
        }

        return true;
    }

    private List<TrainListItem> generateTrainItemsWithDirectionHeadings(List<Train> trainsDue){
        // Sort trains by direction, then by due time
        Collections.sort(trainsDue);

        // TrainListItems can be a train OR a direction heading
        List<TrainListItem> trainListItems = new ArrayList<>();

        String latestTrainDirectionHeading = "";

        for (int i = 0; i < trainsDue.size(); i++) {
            Train train = trainsDue.get(i);

            String nextTrainDirection = train.getDirection();
            if(!nextTrainDirection.equals(latestTrainDirectionHeading)){
                TrainListHeader directionHeader = new TrainListHeader(nextTrainDirection);
                trainListItems.add(directionHeader);
                latestTrainDirectionHeading = nextTrainDirection;
            }
            trainListItems.add(trainsDue.get(i));
        }
        return trainListItems;
    }

    private List<TrainListItem> generateItemsWithStandardHeading(List<Train> trainsDue){
        // Sort by estimated due time
        Collections.sort(trainsDue, Train.dueTimeComparator());

        List<TrainListItem> trainListItems = new ArrayList<>();
        TrainListHeader directionHeader = new TrainListHeader("Due Soon");
        trainListItems.add(directionHeader);
        for(Train train : trainsDue){
            trainListItems.add(train);
        }
        return trainListItems;
    }

    private void configureToolbar(){
        mParentActivity.setSupportActionBar(mToolbar);
        mToolbar.setTitle(null);
        ActionBar actionBar = mParentActivity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(mDisplayedStation.getName());
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else{
            Log.w(TAG, "Action bar is null");
        }
    }

    private void showNoResultsMessage(){
        // TODO Move setting activity view to oncreate/onresume
        // TODO Extract to String resource
        mStatusMsgTv.setText("No trains found");
        mStatusMsgTv.setVisibility(View.VISIBLE);
    }

    public void onTrainSelected(int position){
        Train selectedTrain = mTrainsDueRecyclerViewAdapter.getTrainAtPosition(position);
        mOnTrainSelectedListener.onTrainSelected(selectedTrain, mDisplayedStation);
    }

    public interface OnTrainSelectedListener{
        void onTrainSelected(Train train, Station station);
    }
}
