package ie.markomeara.irelandtraintimes.activities.fragments;

import android.app.Fragment;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        return inflater.inflate(R.layout.fragment_station_next_trains, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setTitle(displayedStation.getName());

        AsyncTask ntt = new NextTrainsTask(this).execute(displayedStation);
    }

    // Called by NextTrainsTask
    public void displayTimes(List<Train> trainsDue){
        String trainsText = "";

        View activityView = getView();
        if(activityView != null) {

            TextView trainsDueTV = (TextView) activityView.findViewById(R.id.trainsDue);

            if (trainsDue != null && trainsDue.size() > 0) {
                for (int i = 0; i < trainsDue.size(); i++) {
                    Train train = trainsDue.get(i);
                    trainsText += train.getDueIn() + " " + train.getDestination() + " (" + train.getDirection() + ")\n";
                }
                trainsDueTV.setText(trainsText);
            } else {
                trainsDueTV.setText("No trains found");
            }
        }
    }
}
