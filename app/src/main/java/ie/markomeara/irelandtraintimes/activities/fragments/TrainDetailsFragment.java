package ie.markomeara.irelandtraintimes.activities.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import ie.markomeara.irelandtraintimes.R;
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
    private Button setReminder_Btn;
    private Button deleteReminder_Btn;

    public static String TRAIN_PARAM = "train";
    public static String STATION_PARAM = "station";

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
        // TODO Rename fragment to not use 'overlay' if it's no longer an overlay
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
        reminderMins_ET = (EditText) getView().findViewById(R.id.trainDetails_remindermins_ET);
        setReminder_Btn = (Button) getView().findViewById(R.id.trainDetails_reminder_BTN);
        setReminder_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setReminder();
            }
        });
        deleteReminder_Btn = (Button) getView().findViewById(R.id.trainDetails_deletereminder_BTN);
        deleteReminder_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                deleteReminder();
            }
        });

        displayTrainDetails();

    }

    private void displayTrainDetails(){
        destination_TV.setText(mTrain.getDestination());
        scheduled_TV.setText(mTrain.getSchDepart());
        estimated_TV.setText(mTrain.getExpDepart());
        dueIn_TV.setText(Integer.toString(mTrain.getDueIn()));
        latest_TV.setText(mTrain.getLatestInfo());
        service_TV.setText(mTrain.getTrainType());
    }

    private void setReminder(){

        int enteredReminderMins = Integer.parseInt(reminderMins_ET.getText().toString());

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(trackingUpdateReceiver,
                new IntentFilter("train-update-broadcast"));

        ReminderManager.setReminder(mTrain, mStation, enteredReminderMins, this.getActivity());

        setReminder_Btn.setVisibility(View.GONE);
        deleteReminder_Btn.setVisibility(View.VISIBLE);

    }

    private void deleteReminder(){
        ReminderManager.clearReminder(getActivity());
        deleteReminder_Btn.setVisibility(View.GONE);
        setReminder_Btn.setVisibility(View.VISIBLE);
    }

    private BroadcastReceiver trackingUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            mTrain = intent.getParcelableExtra("trainDetails");
            displayTrainDetails();

        }
    };

}
