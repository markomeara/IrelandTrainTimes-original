package ie.markomeara.irelandtraintimes.reminder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandtraintimes.trains.Train;
import ie.markomeara.irelandtraintimes.trains.TrainsAPI;

/**
 * Created by mark on 16/03/15.
 */
public class ReminderService extends IntentService {

    private static final String TAG = ReminderService.class.getSimpleName();

    private final String allStationsAPI = "http://api.irishrail.ie/realtime/realtime.asmx/getAllStationsXML";
    private final String trainDetailsAPI = "http://api.irishrail.ie/realtime/realtime.asmx/getTrainMovementsXML?TrainId=e109&TrainDate=21 dec 2011";

    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    private String stationCode = "";
    private String trainCode = "";
    private int reminderMins = 0;

    private SharedPreferences reminderPrefs;

    private boolean alertShown;

    public ReminderService(){
        // Worker thread name: ReminderService
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "ReminderService is running");

        reminderPrefs = getSharedPreferences(ReminderManager.REMINDER_PREFS_NAME, Context.MODE_PRIVATE);
        stationCode = reminderPrefs.getString(ReminderManager.STATION_CODE_PREF, null);
        trainCode = reminderPrefs.getString(ReminderManager.TRAIN_CODE_PREF, null);
        reminderMins = reminderPrefs.getInt(ReminderManager.REMINDER_MINS_PREF, -1);
        alertShown = reminderPrefs.getBoolean(ReminderManager.REMINDER_ALERT_SHOWN_PREF, false);

        Log.i(TAG, stationCode + " -- " + trainCode + " -- " + reminderMins);

        try {
            Train latestTrainInfo = TrainsAPI.getTrainAtStationCode(trainCode, stationCode);

            if(latestTrainInfo == null){
                trainHasGone();
            }
            else{
                Log.i(TAG, "DUE: " + latestTrainInfo.getDueIn());
                if(latestTrainInfo.getDueIn() <= reminderMins && !alertShown){
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

    private void showTrainAlert(){

        // TODO Something here to show alert

        Log.e(TAG, "Train is due!!");
        alertShown = reminderPrefs.getBoolean(ReminderManager.REMINDER_ALERT_SHOWN_PREF, false);
        SharedPreferences.Editor prefsEditor = reminderPrefs.edit();
        prefsEditor.putBoolean(ReminderManager.REMINDER_ALERT_SHOWN_PREF, true);
        prefsEditor.commit();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1:
                    Log.e("IncomingHandler", "CASE 1");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
