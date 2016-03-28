package ie.markomeara.irelandtraintimes.ui.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.Train;
import ie.markomeara.irelandtraintimes.ui.fragment.StationListFragment;
import ie.markomeara.irelandtraintimes.ui.fragment.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.ui.fragment.TrainDetailsFragment;
import ie.markomeara.irelandtraintimes.ui.fragment.TwitterUpdateFragment;
import ie.markomeara.irelandtraintimes.utils.LocationUtils;
import ie.markomeara.irelandtraintimes.utils.SecretKeys;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements TwitterUpdateFragment.TweetFragmentListener,
        StationListFragment.OnStationClickedListener, StationNextTrainsFragment.OnTrainSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // TODO Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    // TODO Fragments should never communicate directly
    // Change implementation so Activity handles comms and fragment changing
    // See http://developer.android.com/training/basics/fragments/communicating.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(SecretKeys.FABRIC_TWITTER_KEY,
                SecretKeys.FABRIC_TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        setContentView(R.layout.activity_home_container);
        buildGoogleApiClient();

        FragmentManager fragMgr = getSupportFragmentManager();
        if(fragMgr.findFragmentById(R.id.mainfragment_placeholder) == null) {
            FragmentTransaction ft = fragMgr.beginTransaction();
            ft.add(R.id.mainfragment_placeholder, new StationListFragment());
            ft.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart(){
        Log.d(TAG, "onStart called");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onTweetFragmentClicked() {
        Intent intent = new Intent(this, TwitterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStationSelected(Station station) {
        Log.i(TAG, "Clicked: " + station.getName());
        FragmentTransaction ft  = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainfragment_placeholder, StationNextTrainsFragment.newInstance(station));
        ft.addToBackStack(TAG);
        ft.commit();
    }

    @Override
    public void onTrainSelected(Train train, Station station){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
