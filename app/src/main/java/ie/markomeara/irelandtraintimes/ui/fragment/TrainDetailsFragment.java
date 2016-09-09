package ie.markomeara.irelandtraintimes.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.manager.ReminderStatusReceiver;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.Train;
import ie.markomeara.irelandtraintimes.manager.ReminderManager;

public class TrainDetailsFragment extends Fragment {

    private static final String TAG = TrainDetailsFragment.class.getSimpleName();

    public static final String TRAIN_PARAM = "train";
    public static final String STATION_PARAM = "station";

    private Train mTrain;
    private Station mStation;
    private AppCompatActivity mParentActivity;

    private BroadcastReceiver mTrackingUpdateReceiver = new ReminderStatusReceiver(this);

    @Bind(R.id.trainDetails_dest_TV)
    protected TextView mDestination_TV;
    @Bind(R.id.trainDetails_scheduled_TV)
    protected TextView mScheduled_TV;
    @Bind(R.id.trainDetails_estimated_TV)
    protected TextView mEstimated_TV;
    @Bind(R.id.trainDetails_dueIn_TV)
    protected TextView mDueIn_TV;
    @Bind(R.id.trainDetails_latest_TV)
    protected TextView mLatest_TV;
    @Bind(R.id.trainDetails_service_TV)
    protected TextView mService_TV;
    @Bind(R.id.trainDetails_remindermins_ET)
    protected TextView mReminderMins_ET;
    @Bind(R.id.trainDetails_trackingActive_TV)
    protected TextView mTrackingActive_TV;
    @Bind(R.id.trainDetails_reminder_BTN)
    protected Button mSetReminder_Btn;
    @Bind(R.id.trainDetails_deletereminder_BTN)
    protected Button mDeleteReminder_Btn;
    @Bind(R.id.trackingDetails_RL)
    protected LinearLayout mTrackingDetails_LL;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

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
        View view = inflater.inflate(R.layout.fragment_train_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSetReminder_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setReminder();
            }
        });
        mDeleteReminder_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                deleteReminder();
            }
        });
        mParentActivity = (AppCompatActivity) getActivity();
        configureToolbar();
        displayTrainDetails();
        if(trainIsBeingTracked()){
            automaticallyRefreshTrainDetails();
        }

    }

    // Called from broadcaster when an update has been received for the train being tracked
    public void newTrainDetailsReceived(Train trainDetails){
        this.mTrain = trainDetails;
        displayTrainDetails();
        updateTrackingUpdateTime();
    }

    private void configureToolbar(){
        mParentActivity.setSupportActionBar(mToolbar);
        mToolbar.setTitle(null);
        ActionBar actionBar = mParentActivity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(mStation.getName());
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else{
            Log.w(TAG, "Action bar is null");
        }
    }

    private void displayTrainDetails(){
        mDestination_TV.setText(mTrain.getDestination());
        mScheduled_TV.setText(mTrain.getSchDepart());
        mEstimated_TV.setText(mTrain.getExpDepart());
        mDueIn_TV.setText(Integer.toString(mTrain.getDueIn()));
        mLatest_TV.setText(mTrain.getLatestInfo());
        mService_TV.setText(mTrain.getTrainType());
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTrackingUpdateReceiver,
                new IntentFilter(ReminderManager.BROADCAST_NAME));
        updateTrackingUpdateTime();
        mSetReminder_Btn.setVisibility(View.GONE);
        mTrackingDetails_LL.setVisibility(View.VISIBLE);
    }

    private void setReminder(){

        automaticallyRefreshTrainDetails();
        int enteredReminderMins = Integer.parseInt(mReminderMins_ET.getText().toString());
        ReminderManager.setReminder(mTrain, mStation, enteredReminderMins, this.getActivity());

    }

    private void deleteReminder(){
        ReminderManager.clearReminder(getActivity());
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mTrackingUpdateReceiver);
        mTrackingDetails_LL.setVisibility(View.GONE);
        mSetReminder_Btn.setVisibility(View.VISIBLE);
    }

    private void updateTrackingUpdateTime(){
        // Getting time to tell user when details were last updated
        Calendar c = Calendar.getInstance();
        Date currTime = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String currTimeDisp = dateFormat.format(currTime);

        String formattedTrackingMsg = String.format(TRACKING_ACTIVE_MSG, currTimeDisp);
        mTrackingActive_TV.setText(formattedTrackingMsg);
    }

}
