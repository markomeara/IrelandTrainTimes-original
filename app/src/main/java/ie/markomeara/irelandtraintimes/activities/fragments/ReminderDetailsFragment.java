package ie.markomeara.irelandtraintimes.activities.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.reminder.ReminderManager;
import ie.markomeara.irelandtraintimes.trains.Train;

/**
 * Created by Mark on 03/04/2015.
 */
public class ReminderDetailsFragment extends Fragment {

    private static final String TAG = ReminderDetailsFragment.class.getSimpleName();

    private final String screenTitle = "Tracking Train";
    private TextView trainCode_TV;
    // TODO Come up with more specific var names
    private TextView stationName_TV;
    private TextView dueIn_TV;
    private TextView reminderTime_TV;
    private TextView latestInfo_TV;

    public ReminderDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_reminder_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getActionBar().setTitle(screenTitle);

        trainCode_TV = (TextView) getView().findViewById(R.id.trainCode_TV);
        stationName_TV = (TextView) getView().findViewById(R.id.station_TV);
        dueIn_TV = (TextView) getView().findViewById(R.id.dueIn_TV);
        reminderTime_TV = (TextView) getView().findViewById(R.id.reminderTime_TV);
        latestInfo_TV = (TextView) getView().findViewById(R.id.latestInfo_TV);

        // TODO Make intent event name a constant
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("train-update-broadcast"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Train trainDetails = intent.getParcelableExtra("trainDetails");
            trainCode_TV.setText(trainDetails.getTrainCode());
            // TODO SET STATION NAME FOR REMINDER
            stationName_TV.setText(ReminderManager.getStationBeingTracked().getName());
            dueIn_TV.setText(Integer.toString(trainDetails.getDueIn()));
            reminderTime_TV.setText(Integer.toString(ReminderManager.getReminderMins()));
            latestInfo_TV.setText(trainDetails.getLatestInfo());

        }
    };

}
