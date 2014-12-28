package ie.markomeara.irelandtraintimes.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.Train;
import ie.markomeara.irelandtraintimes.db.StationsDataSource;
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
    }

    // Called by NextTrainsTask
    public void displayTimes(List<Train> trainsDue){
 //       String trainsText = "";

        View activityView = getView();
        if(activityView != null) {

            if (trainsDue != null && trainsDue.size() > 0) {
                for (int i = 0; i < trainsDue.size(); i++) {
                    Train train = trainsDue.get(i);

                    RelativeLayout trainDueRow_RL = (RelativeLayout) layoutInflater.inflate(R.layout.list_trains_relative, null);

                    LinearLayout trainsDueList_LL = (LinearLayout) parentActivity.findViewById(R.id.trainsDueInfo_LL);
                    TextView trainDest = (TextView) trainDueRow_RL.findViewById(R.id.trainsdue_dest_TV);
                    TextView trainDueMins = (TextView) trainDueRow_RL.findViewById(R.id.trainsdue_mins_TV);

                    trainDest.setText(train.getDestination());
                    trainDueMins.setText(Integer.toString(train.getDueIn()));

                    trainsDueList_LL.addView(trainDueRow_RL);

//                    trainsText += train.getDueIn() + " " + train.getDestination() + " (" + train.getDirection() + ")\n";
                }
//                trainsDueTV.setText(trainsText);
            } else {
                TextView trainsDueTV = (TextView) activityView.findViewById(R.id.trainsDue);
                trainsDueTV.setText("No trains found");
            }
        }
    }
}
