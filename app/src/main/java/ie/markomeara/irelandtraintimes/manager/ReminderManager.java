package ie.markomeara.irelandtraintimes.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.Train;

/**
 *
 * ReminderManager is to be used as an access point to the ReminderService. All management of
 * the train reminder should be done through this manager class. This class controls the stopping
 * and starting of the reminder service, as well as the storing and updating of relevant values.
 */

public class ReminderManager {

    private static final String TAG = ReminderManager.class.getSimpleName();
    public static final String BROADCAST_NAME = "train-update-broadcast";

    // 30 seconds
    private static final int REMINDER_POLL_INTERVAL = 30000;

    private static PendingIntent sPendingIntent = null;
    private static Train sTrainBeingTracked;
    private static Station sStationBeingTracked;
    private static int sReminderMins;

    private static boolean sAlertShown = false;

    public static void setReminder(Train train, Station station, int mins, Context ctx){

        Log.d(TAG, "setReminder");
        sTrainBeingTracked = train;
        sStationBeingTracked = station;
        sReminderMins = mins;
        sAlertShown = false;

        Intent alarm = new Intent(ctx, ReminderStartReceiver.class);
        alarm.putExtra("train", sTrainBeingTracked);
        alarm.putExtra("station", sStationBeingTracked);
        alarm.putExtra("reminderMins", sReminderMins);

        sPendingIntent = PendingIntent.getBroadcast(ctx, 0, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                REMINDER_POLL_INTERVAL, sPendingIntent);

    }

    public static void clearReminder(Context ctx){

        Log.d(TAG, "clearReminder");
        // Stop reminder polling
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sPendingIntent);
        sTrainBeingTracked = null;
        sStationBeingTracked = null;
        sReminderMins = -1;

    }

    public static Train getTrainBeingTracked(){
        return sTrainBeingTracked;
    }

    public static Station getStationBeingTracked(){
        return sStationBeingTracked;
    }

    public static int getReminderMins(){
        return sReminderMins;
    }

    public static boolean isAlertShown(){
        return sAlertShown;
    }

    public static void setAlertShown(){
        sAlertShown = true;
    }

}
