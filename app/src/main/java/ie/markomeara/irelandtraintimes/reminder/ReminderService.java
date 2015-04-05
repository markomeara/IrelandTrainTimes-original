package ie.markomeara.irelandtraintimes.reminder;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandtraintimes.trains.Station;
import ie.markomeara.irelandtraintimes.trains.Train;
import ie.markomeara.irelandtraintimes.trains.TrainsAPI;

/**
 * Created by mark on 16/03/15.
 */
public class ReminderService extends IntentService {

    private static final String TAG = ReminderService.class.getSimpleName();

    private final IBinder reminderServiceBinder = new ReminderServiceBinder();

    private Station station;
    private Train train;
    private int reminderMins = 0;

    public ReminderService(){
        // Worker thread name: ReminderService
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e(TAG, "ReminderService is running");

        train = intent.getParcelableExtra("train");
        station = intent.getParcelableExtra("station");
        reminderMins = intent.getIntExtra("reminderMins", 0);

        Log.e(TAG, station.getName() + " -- " + train.getTrainCode() + " -- " + reminderMins);

        try {
            Train latestTrainInfo = TrainsAPI.getTrainAtStationCode(train.getTrainCode(), station.getCode());

            if(latestTrainInfo == null){
                trainHasGone();
            }
            else{
                Log.e(TAG, "DUE: " + latestTrainInfo.getDueIn());

                notifyUIWithReminderDetails(latestTrainInfo);

                if(latestTrainInfo.getDueIn() <= reminderMins && !ReminderManager.isAlertShown()){
                    showTrainAlert();
                }
            }

        } catch (ParserConfigurationException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (SAXException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        // TODO Call startForeground
    }

    private void trainHasGone(){
        Log.e(TAG, "Train has gone");
        ReminderManager.clearReminder(this);
    }

    private void notifyUIWithReminderDetails(Train trainInfo){
        Log.e(TAG, "Notifying UI with reminder details");
        Intent broadcastIntent = new Intent("train-update-broadcast");
        broadcastIntent.putExtra("trainDetails", trainInfo);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

    }

    private void showTrainAlert(){

        Log.e(TAG, "Train is due!!");
        ReminderManager.setAlertShown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return reminderServiceBinder;
    }

    public class ReminderServiceBinder extends Binder {
        public ReminderService getService(){
            Log.e(TAG, "getService");
            return ReminderService.this;
        }
    }

}
