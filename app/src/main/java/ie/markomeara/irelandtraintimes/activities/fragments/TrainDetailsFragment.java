package ie.markomeara.irelandtraintimes.activities.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.trains.Train;
import ie.markomeara.irelandtraintimes.services.ReminderService;
import ie.markomeara.irelandtraintimes.trains.TrainsAPI;
import ie.markomeara.irelandtraintimes.utils.ReminderUtils;

public class TrainDetailsFragment extends Fragment {


    private static final String TAG = TrainDetailsFragment.class.getSimpleName();

    // TODO Be consistent of naming member vars throughout app... start with 'm' or not
    private Train mTrain;

    private TextView destination_TV;
    private TextView scheduled_TV;
    private TextView estimated_TV;
    private TextView latest_TV;
    private TextView service_TV;
    private TextView reminderMins_ET;
    private TextView toggleReminder_BTN;

    public static String TRAIN_PARAM = "train";

    public static TrainDetailsFragment newInstance(Train train) {
        TrainDetailsFragment fragment = new TrainDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(TRAIN_PARAM, train);
        fragment.setArguments(args);
        return fragment;
    }

    public TrainDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrain = getArguments().getParcelable(TRAIN_PARAM);
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
        return inflater.inflate(R.layout.reminderoverlay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        destination_TV = (TextView) getView().findViewById(R.id.trainDetails_dest_TV);
        scheduled_TV = (TextView) getView().findViewById(R.id.trainDetails_scheduled_TV);
        estimated_TV = (TextView) getView().findViewById(R.id.trainDetails_estimated_TV);
        latest_TV = (TextView) getView().findViewById(R.id.trainDetails_latest_TV);
        service_TV = (TextView) getView().findViewById(R.id.trainDetails_service_TV);
        reminderMins_ET = (EditText) getView().findViewById(R.id.trainDetails_remindermins_ET);
        toggleReminder_BTN = (TextView) getView().findViewById(R.id.trainDetails_reminder_BTN);
        toggleReminder_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleReminder();
            }
        });

        displayTrainDetails();

    }

    private void displayTrainDetails(){
        destination_TV.setText(mTrain.getDestination());
        scheduled_TV.setText(mTrain.getSchDepart());
        estimated_TV.setText(mTrain.getExpDepart());
        latest_TV.setText(mTrain.getLatestInfo());
        service_TV.setText(mTrain.getTrainType());
    }

    private void toggleReminder(){
        int enteredReminderMins = Integer.parseInt(reminderMins_ET.getText().toString());
        ReminderUtils.setReminder(mTrain, enteredReminderMins, getActivity().getPreferences(Context.MODE_PRIVATE));

        Intent reminderServiceIntent = new Intent(this.getActivity(), ReminderService.class);
        this.getActivity().startService(reminderServiceIntent);

    }


}
