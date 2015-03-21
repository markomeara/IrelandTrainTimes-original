package ie.markomeara.irelandtraintimes.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Messenger;
import android.os.SystemClock;
import android.util.Log;

import ie.markomeara.irelandtraintimes.reminder.ReminderService;
import ie.markomeara.irelandtraintimes.trains.Station;
import ie.markomeara.irelandtraintimes.trains.Train;

/**
 * Created by Mark on 02/01/2015.
 */

public class ReminderManager {

    private static final String TAG = ReminderManager.class.getSimpleName();

    public static final String TRAIN_CODE_PREF = "trainCode";
    public static final String STATION_CODE_PREF = "stationCode";
    public static final String REMINDER_MINS_PREF = "reminderMins";
    public static final String REMINDER_ALERT_SHOWN_PREF = "alertShown";

    public static final String REMINDER_PREFS_NAME = "reminderPrefs";

    // 30 seconds
    private static final int REMINDER_POLL_INTERVAL = 30000;

    private static Messenger mReminderService = null;
    private static boolean mBound;

    private static PendingIntent pendingIntent = null;


    public static void setReminder(Train train, Station station, int mins, Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences(REMINDER_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(TRAIN_CODE_PREF, train.getTrainCode());
        prefsEditor.putString(STATION_CODE_PREF, station.getCode());
        prefsEditor.putInt(REMINDER_MINS_PREF, mins);
        prefsEditor.putBoolean(REMINDER_ALERT_SHOWN_PREF, false);
        prefsEditor.commit();

        Intent alarm = new Intent(ctx, ReminderReceiver.class);
     //   boolean reminderPollRunning = (PendingIntent.getBroadcast(ctx, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);

        Log.e(TAG, "If !reminderPollRunning");
     //   if(reminderPollRunning == false) {

            Log.e(TAG, "Attempting to run polling");

            pendingIntent = PendingIntent.getBroadcast(ctx, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                    REMINDER_POLL_INTERVAL, pendingIntent);


     //   }

//        Intent reminderServiceIntent = new Intent(ctx, ReminderService.class);
//        ctx.startService(reminderServiceIntent);

        // AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
       // alarmManager.setRepeating();
    }

    public static void clearReminder(Context ctx){

        // Stop reminder polling
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        SharedPreferences prefs = ctx.getSharedPreferences(REMINDER_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();

    }

    // https://developer.android.com/guide/components/bound-services.html

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mReminderService = new Messenger(service);
            mBound = true;
        }

        // Called when service unexpectedly disconnects (e.g. crashes)
        public void onServiceDisconnected(ComponentName className) {
            mReminderService = null;
            mBound = false;
        }
    };

}
