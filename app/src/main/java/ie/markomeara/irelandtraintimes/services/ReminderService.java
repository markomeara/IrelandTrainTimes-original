package ie.markomeara.irelandtraintimes.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import ie.markomeara.irelandtraintimes.utils.ReminderManager;

/**
 * Created by mark on 16/03/15.
 */
public class ReminderService extends IntentService {

    private static final String TAG = ReminderService.class.getSimpleName();

    private final String allStationsAPI = "http://api.irishrail.ie/realtime/realtime.asmx/getAllStationsXML";
    private final String trainDetailsAPI = "http://api.irishrail.ie/realtime/realtime.asmx/getTrainMovementsXML?TrainId=e109&TrainDate=21 dec 2011";

    private String stationCode = "";
    private String trainCode = "";
    private int reminderMins = 0;

    public ReminderService(){
        // Worker thread name: ReminderService
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context ctx = getApplicationContext();
        SharedPreferences reminderPrefs = ctx.getSharedPreferences(ReminderManager.REMINDER_PREFS_NAME, Context.MODE_PRIVATE);
        stationCode = reminderPrefs.getString(ReminderManager.STATION_CODE_PREF, null);
        trainCode = reminderPrefs.getString(ReminderManager.TRAIN_CODE_PREF, null);
        reminderMins = reminderPrefs.getInt(ReminderManager.REMINDER_MINS_PREF, -1);

        Log.e(TAG, stationCode + " -- " + trainCode + " -- " + reminderMins);
        // TODO Call startForeground
    }

    private void getLatestTrainInfo(String trainCode){

    }

    /*
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    // Once this executes, service runs indefinitely until stopSelf() or stopService() is called
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return 0;
    }

    */
}
