package ie.markomeara.irelandtraintimes.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import butterknife.Bind;
import butterknife.ButterKnife;
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
    private Station mDisplayedStation;
    private AppCompatActivity mParentActivity;
    private OnTrainSelectedListener onTrainSelectedListener;
    private LayoutInflater mLayoutInflater;
    private TrainsDueRecyclerViewAdapter mTrainsDueRecyclerViewAdapter;

    @Bind(R.id.trainsDueAtStation_RV)
    RecyclerView trainsDueRV;
    @Bind(R.id.trainsDue_loading_TV)
    TextView statusMsg_TV;   // TODO Switch to switcher view
    @Bind(R.id.nexttrains_loading)
    ProgressBar nextTrainsProgressBar;

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
            DatabaseOrmHelper dbHelper = DatabaseOrmHelper.getDbHelper(mParentActivity);
            try {
                Dao<Station, Integer> stationDao =  dbHelper.getStationDao();
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
        ActionBar actionBar = mParentActivity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(mDisplayedStation.getName());

            // TODO Add 'ontouch' background change to the back button in action bar
            // it's there by default in lollipop
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        else{
            Log.e(TAG, "Action bar is null");
        }

        AsyncTask<Station, Integer, List<Train>> nextTrainsTask = new NextTrainsTask(this);
        nextTrainsTask.execute(mDisplayedStation);

        if(mParentActivity instanceof OnTrainSelectedListener){
            onTrainSelectedListener = (OnTrainSelectedListener) mParentActivity;
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
            nextTrainsProgressBar.setVisibility(View.GONE);
            if (trainsDue != null && !trainsDue.isEmpty()) {

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

                mTrainsDueRecyclerViewAdapter = new TrainsDueRecyclerViewAdapter(trainListItems, this);
                trainsDueRV.setLayoutManager(new LinearLayoutManager(getActivity()));
                trainsDueRV.setAdapter(mTrainsDueRecyclerViewAdapter);
            } else {
                showNoResultsMessage();
            }
        }
    }

    private void showNoResultsMessage(){
        // TODO Move setting activity view to oncreate/onresume
        statusMsg_TV.setText("No trains found");
        statusMsg_TV.setVisibility(View.VISIBLE);
    }

    public void onTrainSelected(int position){
        Train selectedTrain = mTrainsDueRecyclerViewAdapter.getTrainAtPosition(position);
        onTrainSelectedListener.onTrainSelected(selectedTrain, mDisplayedStation);
    }

    public interface OnTrainSelectedListener{
        void onTrainSelected(Train train, Station station);
    }
}
