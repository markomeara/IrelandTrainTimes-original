package ie.markomeara.irelandtraintimes.activities.fragments;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.reminder.ReminderStatusReceiver;
import ie.markomeara.irelandtraintimes.trains.Station;
import ie.markomeara.irelandtraintimes.trains.Train;
import ie.markomeara.irelandtraintimes.reminder.ReminderManager;

public class TrainDetailsFragment extends Fragment {


    private static final String TAG = TrainDetailsFragment.class.getSimpleName();

    // TODO Be consistent of naming member vars throughout app... start with 'm' or not
    private Train mTrain;
    private Station mStation;

    private TextView destination_TV;
    private TextView scheduled_TV;
    private TextView estimated_TV;
    private TextView dueIn_TV;
    private TextView latest_TV;
    private TextView service_TV;
    private TextView reminderMins_ET;
    private TextView trackingActive_TV;
    private Button setReminder_Btn;
    private Button deleteReminder_Btn;
    private LinearLayout trackingDetails_LL;

    public static String TRAIN_PARAM = "train";
    public static String STATION_PARAM = "station";

    private BroadcastReceiver trackingUpdateReceiver = new ReminderStatusReceiver(this);

    private static final String TRACKING_ACTIVE_MSG = "Tracking Active. Details last updated at %s. This screen" +
            " will refresh automatically with your train details.";

    public TrainDetailsFragment() {
    }

    public static TrainDetailsFragment newInstance(Train train, Station stationBeingViewed) {
        TrainDetailsFragment fragment = new TrainDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(TRAIN_PARAM, train);
        args.putParcelable(STATION_PARAM, stationBeingViewed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrain = getArguments().getParcelable(TRAIN_PARAM);
            mStation = getArguments().getParcelable(STATION_PARAM);
        }

        if(mTrain == null){
            // TODO Go back to prev screen or something
            Log.e(TAG, "No train found for train details screen");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_train_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        destination_TV = (TextView) getView().findViewById(R.id.trainDetails_dest_TV);
        scheduled_TV = (TextView) getView().findViewById(R.id.trainDetails_scheduled_TV);
        estimated_TV = (TextView) getView().findViewById(R.id.trainDetails_estimated_TV);
        dueIn_TV = (TextView) getView().findViewById(R.id.trainDetails_dueIn_TV);
        latest_TV = (TextView) getView().findViewById(R.id.trainDetails_latest_TV);
        service_TV = (TextView) getView().findViewById(R.id.trainDetails_service_TV);
        trackingActive_TV = (TextView) getView().findViewById(R.id.trainDetails_trackingActive_TV);
        reminderMins_ET = (EditText) getView().findViewById(R.id.trainDetails_remindermins_ET);
        setReminder_Btn = (Button) getView().findViewById(R.id.trainDetails_reminder_BTN);
        deleteReminder_Btn = (Button) getView().findViewById(R.id.trainDetails_deletereminder_BTN);
        trackingDetails_LL = (LinearLayout) getView().findViewById(R.id.trackingDetails_RL);
        setReminder_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setReminder();
            }
        });
        deleteReminder_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                deleteReminder();
            }
        });

        displayTrainDetails();
        if(trainIsBeingTracked()){
            automaticallyRefreshTrainDetails();
        }

    }

    private void displayTrainDetails(){
        destination_TV.setText(mTrain.getDestination());
        scheduled_TV.setText(mTrain.getSchDepart());
        estimated_TV.setText(mTrain.getExpDepart());
        dueIn_TV.setText(Integer.toString(mTrain.getDueIn()));
        latest_TV.setText(mTrain.getLatestInfo());
        service_TV.setText(mTrain.getTrainType());
    }

    private boolean trainIsBeingTracked(){
        boolean result = false;

        Train trainBeingTracked = ReminderManager.getTrainBeingTracked();
        if(trainBeingTracked != null){
            if(trainBeingTracked.equals(mTrain)){
                result = true;
            }
        }
        return result;
    }

    private void automaticallyRefreshTrainDetails(){
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(trackingUpdateReceiver,
                new IntentFilter(ReminderManager.BROADCAST_NAME));
        updateTrackingUpdateTime();
        setReminder_Btn.setVisibility(View.GONE);
        trackingDetails_LL.setVisibility(View.VISIBLE);
    }

    private void setReminder(){

        automaticallyRefreshTrainDetails();
        int enteredReminderMins = Integer.parseInt(reminderMins_ET.getText().toString());
        ReminderManager.setReminder(mTrain, mStation, enteredReminderMins, this.getActivity());

    }

    private void deleteReminder(){
        ReminderManager.clearReminder(getActivity());
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(trackingUpdateReceiver);
        trackingDetails_LL.setVisibility(View.GONE);
        setReminder_Btn.setVisibility(View.VISIBLE);
    }

    private void updateTrackingUpdateTime(){
        // Getting time to tell user when details were last updated
        Calendar c = Calendar.getInstance();
        Date currTime = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String currTimeDisp = dateFormat.format(currTime);

        String formattedTrackingMsg = String.format(TRACKING_ACTIVE_MSG, currTimeDisp);
        trackingActive_TV.setText(formattedTrackingMsg);
    }

    // Called from broadcaster when an update has been received for the train being tracked
    public void newTrainDetailsReceived(Train trainDetails){
        this.mTrain = trainDetails;
        displayTrainDetails();
        updateTrackingUpdateTime();
    }

}
