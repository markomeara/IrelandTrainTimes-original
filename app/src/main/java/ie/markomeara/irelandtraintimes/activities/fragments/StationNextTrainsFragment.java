package ie.markomeara.irelandtraintimes.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ie.markomeara.irelandtraintimes.ListHelpers.TrainListHeader;
import ie.markomeara.irelandtraintimes.ListHelpers.adapters.TrainsDueListAdapter;
import ie.markomeara.irelandtraintimes.ListHelpers.interfaces.TrainListItem;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.Train;
import ie.markomeara.irelandtraintimes.storage.StationsDataSource;
import ie.markomeara.irelandtraintimes.exceptions.DBNotAvailableException;
import ie.markomeara.irelandtraintimes.networktasks.NextTrainsTask;

/**
 * Created by Mark on 27/10/2014.
 */
public class StationNextTrainsFragment extends Fragment {

    private static final String TAG = StationNextTrainsFragment.class.getSimpleName();
    private static final String STATION_PARAM = "stationId";
    private Station displayedStation;
    private Activity parentActivity;
    private OnTrainSelectedListener onTrainSelectedListener;
    private LayoutInflater layoutInflater;

    public static StationNextTrainsFragment newInstance(Station selectedStation) {
        StationNextTrainsFragment fragment = new StationNextTrainsFragment();
        Bundle args = new Bundle();
        args.putLong(STATION_PARAM, selectedStation.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            long stationId = getArguments().getLong(STATION_PARAM);
            StationsDataSource sds = new StationsDataSource(getActivity());
            try {
                sds.open();
                displayedStation = sds.retrieveStationById(stationId);
                sds.close();
            } catch (SQLException ex) {
                Log.e(TAG, ex.toString(), ex);
            } catch(DBNotAvailableException ex){
                Log.e(TAG, ex.toString(), ex);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        layoutInflater = inflater;
        return layoutInflater.inflate(R.layout.fragment_station_next_trains, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parentActivity = getActivity();
        parentActivity.getActionBar().setTitle(displayedStation.getName());

        AsyncTask ntt = new NextTrainsTask(this).execute(displayedStation);
        if(parentActivity instanceof OnTrainSelectedListener){
            onTrainSelectedListener = (OnTrainSelectedListener) parentActivity;
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

            if (trainsDue != null && !trainsDue.isEmpty()) {

                // Sort trains by direction, then by due time
                Collections.sort(trainsDue);

                // TrainListItems can be a train OR a direction heading
                List<TrainListItem> trainListItems = new ArrayList<TrainListItem>();

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

                ListView trainsDueList = (ListView) activityView.findViewById(R.id.trainsDueAtStation_LV);
                TrainsDueListAdapter trainsListAdapter = new TrainsDueListAdapter(getActivity(), trainListItems);
                setOnClickListeners(trainsListAdapter);

                // Item click listener & reminder button click listener are set in adapter
                trainsDueList.setAdapter(trainsListAdapter);
                hideInfoMessage();
            } else {
                showNoResultsMessage();
            }
        }
    }

    private void hideInfoMessage(){
        // TODO Move setting activity view to oncreate/onresume
        View activityView = getView();
        if(activityView != null){
            View loadingMsg = activityView.findViewById(R.id.trainsDue_loading_TV);
            loadingMsg.setVisibility(View.GONE);
        }
    }

    private void showNoResultsMessage(){
        // TODO Move setting activity view to oncreate/onresume
        View activityView = getView();
        TextView trainsDueTV = (TextView) activityView.findViewById(R.id.trainsDue_loading_TV);
        trainsDueTV.setText("No trains found");
        trainsDueTV.setVisibility(View.VISIBLE);
    }

    private void setOnClickListeners(TrainsDueListAdapter adapter){
        adapter.setTrainDetailsOnClickListener(new TrainsDueListAdapter.OnClickListener() {
            @Override
            public void onClick(Train train) {
                onTrainSelectedListener.onTrainSelected(train);
            }
        });
        adapter.setReminderButtonOnClickListener(new TrainsDueListAdapter.OnClickListener(){
            @Override
            public void onClick(Train train) {
                Log.e(TAG, "Reminder button: " + train.getDestination());
            }
        });
    }

    public interface OnTrainSelectedListener{
        public void onTrainSelected(Train train);
    }
}
