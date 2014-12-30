package ie.markomeara.irelandtraintimes.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.util.Collections;
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
    private View activityView;

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

        // TODO Move setting activity view to oncreate/onresume
        View activityView = getView();
        if(activityView != null) {

            if (trainsDue != null && !trainsDue.isEmpty()) {

                // Sort trains by direction, then by due time
                Collections.sort(trainsDue);
                LinearLayout trainsDueList_LL = (LinearLayout) parentActivity.findViewById(R.id.trainsDueInfo_LL);
                String directionBeingShown = "";

                for (int i = 0; i < trainsDue.size(); i++) {
                    Train train = trainsDue.get(i);

                    if(!train.getDirection().equals(directionBeingShown)){
                        appendDirectionHeading(trainsDueList_LL, train.getDirection());
                        directionBeingShown = train.getDirection();
                    }

                    RelativeLayout trainDueRow_RL = (RelativeLayout) layoutInflater.inflate(R.layout.list_trains_relative, trainsDueList_LL, false);

                    TextView trainDest_TV = (TextView) trainDueRow_RL.findViewById(R.id.traindue_dest_TV);
                    TextView trainDueMins_TV = (TextView) trainDueRow_RL.findViewById(R.id.traindue_mins_TV);
                    TextView trainDueTime_TV = (TextView) trainDueRow_RL.findViewById(R.id.traindue_time_TV);
                    TextView trainDelayMins_TV = (TextView) trainDueRow_RL.findViewById(R.id.traindue_delay_TV);

                    trainDest_TV.setText(train.getDestination());
                    trainDueMins_TV.setText(Integer.toString(train.getDueIn()) + " mins");
                    trainDueTime_TV.setText(train.getExpDepart());

                    int delayMins = train.getDelayMins();

                    StringBuilder delayMinsDisplay = new StringBuilder();

                    if(delayMins != 0){
                        String sign = "";
                        // Negative delay will already have a minus sign from API
                        if(delayMins > 0){
                            sign = "+";
                        }
                        delayMinsDisplay.append("(");
                        delayMinsDisplay.append(sign);
                        delayMinsDisplay.append(delayMins);
                        delayMinsDisplay.append(")");
                    }

                    trainDelayMins_TV.setText(delayMinsDisplay.toString());
                    trainsDueList_LL.addView(trainDueRow_RL);

                }
                hideInfoMessage();
            } else {
                showNoResultsMessage();
            }
        }
    }

    private void appendDirectionHeading(LinearLayout parentView, String direction){
        LinearLayout heading_LL = (LinearLayout) layoutInflater.inflate(R.layout.direction_heading, parentView, false);
        TextView direction_TV = (TextView) heading_LL.findViewById(R.id.directionHeading_TV);
        direction_TV.setText(direction);
        parentView.addView(heading_LL);
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
}
