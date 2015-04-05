package ie.markomeara.irelandtraintimes.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import ie.markomeara.irelandtraintimes.trains.Station;
import ie.markomeara.irelandtraintimes.trains.Train;

/**
 * Created by Mark on 02/01/2015.
 *
 * ReminderManager is to be used as an access point to the ReminderService. All management of
 * the train reminder should be done through this manager class. This class controls the stopping
 * and starting of the reminder service, as well as the storing and updating of relevant values.
 */

public class ReminderManager {

    private static final String TAG = ReminderManager.class.getSimpleName();

    // 30 seconds
    private static final int REMINDER_POLL_INTERVAL = 30000;
    private static PendingIntent pendingIntent = null;
    private static Train trainBeingTracked;
    private static Station stationBeingTracked;
    private static int reminderMins;

    private static boolean alertShown = false;


    public static void setReminder(Train train, Station station, int mins, Context ctx){

        trainBeingTracked = train;
        stationBeingTracked = station;
        reminderMins = mins;
        alertShown = false;

        Intent alarm = new Intent(ctx, ReminderReceiver.class);
        alarm.putExtra("train", trainBeingTracked);
        alarm.putExtra("station", stationBeingTracked);
        alarm.putExtra("reminderMins", reminderMins);

        Log.e(TAG, "Attempting to run polling");

        // TODO Check if service/alarm is already running. If so, cancel alarm and start new one

        pendingIntent = PendingIntent.getBroadcast(ctx, 0, alarm, 0);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                REMINDER_POLL_INTERVAL, pendingIntent);

    }

    public static void clearReminder(Context ctx){

        // Stop reminder polling
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

    public static Train getTrainBeingTracked(){
        return trainBeingTracked;
    }

    public static Station getStationBeingTracked(){
        return stationBeingTracked;
    }

    public static int getReminderMins(){
        return reminderMins;
    }

    public static boolean isAlertShown(){
        return alertShown;
    }

    public static void setAlertShown(){
        alertShown = true;
    }

}
