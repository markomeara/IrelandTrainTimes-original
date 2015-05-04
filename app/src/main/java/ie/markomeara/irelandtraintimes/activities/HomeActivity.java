package ie.markomeara.irelandtraintimes.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.trains.Station;
import ie.markomeara.irelandtraintimes.trains.Train;
import ie.markomeara.irelandtraintimes.activities.fragments.StationListFragment;
import ie.markomeara.irelandtraintimes.activities.fragments.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.activities.fragments.TrainDetailsFragment;
import ie.markomeara.irelandtraintimes.activities.fragments.TwitterUpdateFragment;
import ie.markomeara.irelandtraintimes.location.LocationUtils;

/**
 * Created by Mark on 02/11/2014.
 */
public class HomeActivity extends Activity implements TwitterUpdateFragment.OnFragmentInteractionListener,
        StationListFragment.OnStationClickedListener, StationNextTrainsFragment.OnTrainSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    // TODO Fragments should never communicate directly
    // Change implementation so Activity handles comms and fragment changing
    // See http://developer.android.com/training/basics/fragments/communicating.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_container);
        buildGoogleApiClient();

        FragmentManager fragMgr = getFragmentManager();
        if(fragMgr.findFragmentById(R.id.mainfragment_placeholder) == null) {
            FragmentTransaction ft = fragMgr.beginTransaction();
            ft.add(R.id.mainfragment_placeholder, new StationListFragment());
            ft.commit();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
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
    public void onTrainSelected(Train train, Station station){
        FragmentTransaction ft  = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainfragment_placeholder, TrainDetailsFragment.newInstance(train, station));
        ft.addToBackStack(TAG);
        ft.commit();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to Google API");
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LocationUtils.updateLastLocation(lastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection to Google API has been suspended " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Google API FAILED");
    }

}
