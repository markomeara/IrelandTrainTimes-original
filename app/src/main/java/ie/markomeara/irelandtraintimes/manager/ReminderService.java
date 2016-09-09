package ie.markomeara.irelandtraintimes.manager;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandtraintimes.Injector;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.Train;
import ie.markomeara.irelandtraintimes.model.TrainList;
import ie.markomeara.irelandtraintimes.network.IrishRailAPIUtil;
import ie.markomeara.irelandtraintimes.network.IrishRailService;

public class ReminderService extends IntentService {

    private static final String TAG = ReminderService.class.getSimpleName();

    private final IBinder mReminderServiceBinder = new ReminderServiceBinder();

    @Inject
    protected IrishRailService mIrishRailService;

    private Station station;
    private Train train;
    private int reminderMins = 0;

    public ReminderService(){
        // Worker thread name: ReminderService
        super("ReminderService");
        Injector.get().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "ReminderService is running");

        train = intent.getParcelableExtra("train");
        station = intent.getParcelableExtra("station");
        reminderMins = intent.getIntExtra("reminderMins", 0);

        Log.i(TAG, station.getName() + " -- " + train.getTrainCode() + " -- " + reminderMins);

        try {
            // TODO Handle error
            TrainList trainList = mIrishRailService.getTrainsDueAtStation(station.getCode()).execute().body();

            Train latestTrainInfo = IrishRailAPIUtil.extractTrainFromTrainList(train.getTrainCode(), trainList.getTrainList());

            if(latestTrainInfo == null){
                trainHasGone();
            }
            else{
                Log.d(TAG, "DUE: " + latestTrainInfo.getDueIn());

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
        Log.i(TAG, "Train has gone");
        ReminderManager.clearReminder(this);
    }

    private void notifyUIWithReminderDetails(Train trainInfo){
        Log.d(TAG, "Notifying UI with reminder details");
        Intent broadcastIntent = new Intent(ReminderManager.BROADCAST_NAME);
        broadcastIntent.putExtra("trainDetails", trainInfo);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

    }

    private void showTrainAlert(){

        Log.i(TAG, "Train is due!!");
        ReminderManager.setAlertShown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mReminderServiceBinder;
    }

    public class ReminderServiceBinder extends Binder {
        public ReminderService getService(){
            Log.d(TAG, "getService");
            return ReminderService.this;
        }
    }

}
