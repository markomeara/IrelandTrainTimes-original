package ie.markomeara.irelandtraintimes.activities.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Train;

public class TrainDetailsFragment extends Fragment {

    private Train mTrain;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // TODO Rename fragment to not use 'overlay' if it's no longer an overlay
        return inflater.inflate(R.layout.reminderoverlay, container, false);
    }


}
