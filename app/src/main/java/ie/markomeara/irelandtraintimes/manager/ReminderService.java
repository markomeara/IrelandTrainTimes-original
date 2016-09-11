package ie.markomeara.irelandtraintimes.manager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import com.google.common.eventbus.Subscribe;

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

    public static final String TRAIN_CODE = "trainCode";
    public static final String STATION = "station";
    public static final String REMINDER_MINS = "reminderMins";

    private final IBinder mReminderServiceBinder = new ReminderServiceBinder();

    @Inject
    IrishRailService mIrishRailService;

    private Station mStation;
    private String mTrainCode;
    private int mReminderMins = 0;

    public ReminderService(){
        // Worker thread name: ReminderService
        super("ReminderService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.get().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "ReminderService is running");

        mTrainCode = intent.getParcelableExtra(TRAIN_CODE);
        mStation = intent.getParcelableExtra(STATION);
        mReminderMins = intent.getIntExtra(REMINDER_MINS, 0);

        Log.i(TAG, mStation.getName() + " -- " + mTrainCode + " -- " + mReminderMins);

        mIrishRailService.fetchTrainsDueAtStation(mStation.getCode());
    }

    @Subscribe
    private void onTrainsDueReceived(TrainList trainList) {
        try {
            Train latestTrainInfo = IrishRailAPIUtil.extractTrainFromTrainList(mTrainCode, trainList.getTrainList());
            processLatestTrainInfo(latestTrainInfo);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void processLatestTrainInfo(Train latestTrainInfo) {
        if(latestTrainInfo == null){
            Log.i(TAG, "Train has gone");
            ReminderManager.clearReminder(this);
        } else {
            Log.d(TAG, "DUE: " + latestTrainInfo.getDueIn());

            notifyUIWithReminderDetails(latestTrainInfo);

            if(latestTrainInfo.getDueIn() <= mReminderMins && !ReminderManager.isAlertShown()){
                showTrainAlert();
            }
        }

        // TODO Call startForeground
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

    public static Intent prepareIntent(Context ctx, String trainCode, Station station, int reminderMins) {
        Intent intent = new Intent(ctx, ReminderService.class);
        intent.putExtra(TRAIN_CODE, trainCode);
        intent.putExtra(STATION, station);
        intent.putExtra(REMINDER_MINS, reminderMins);
        return intent;
    }

    public class ReminderServiceBinder extends Binder {
        public ReminderService getService(){
            Log.d(TAG, "getService");
            return ReminderService.this;
        }
    }

}
