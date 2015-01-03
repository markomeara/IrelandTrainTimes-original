package ie.markomeara.irelandtraintimes.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.Train;
import ie.markomeara.irelandtraintimes.activities.fragments.StationListFragment;
import ie.markomeara.irelandtraintimes.activities.fragments.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.activities.fragments.TrainDetailsFragment;
import ie.markomeara.irelandtraintimes.activities.fragments.TwitterUpdateFragment;

/**
 * Created by Mark on 02/11/2014.
 */
public class HomeActivity extends Activity implements TwitterUpdateFragment.OnFragmentInteractionListener,
        StationListFragment.OnStationSelectedListener, StationNextTrainsFragment.OnTrainSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    // TODO Fragments should never communicate directly
    // Change implementation so Activity handles comms and fragment changing
    // See http://developer.android.com/training/basics/fragments/communicating.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_container);

        FragmentManager fragMgr = getFragmentManager();
        if(fragMgr.findFragmentById(R.id.mainfragment_placeholder) == null) {
            FragmentTransaction ft = fragMgr.beginTransaction();
            ft.add(R.id.mainfragment_placeholder, new StationListFragment());
            ft.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e(TAG, "Need to implement onFragmentInteraction");
    }

    @Override
    public void onStationSelected(Station station) {
        Log.i(TAG, "Clicked: " + station.getName());
        FragmentTransaction ft  = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainfragment_placeholder, StationNextTrainsFragment.newInstance(station));
        ft.addToBackStack(TAG);
        ft.commit();
    }

    @Override
    public void onTrainSelected(Train train){
        FragmentTransaction ft  = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainfragment_placeholder, TrainDetailsFragment.newInstance(train));
        ft.addToBackStack(TAG);
        ft.commit();
    }
}
