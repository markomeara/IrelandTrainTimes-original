package ie.markomeara.irelandtraintimes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import ie.markomeara.irelandtraintimes.trains.Station;
import ie.markomeara.irelandtraintimes.trains.Train;

/**
 * Created by Mark on 02/01/2015.
 */

public class ReminderManager {

    public static final String TRAIN_CODE_PREF = "trainCode";
    public static final String STATION_CODE_PREF = "stationCode";
    public static final String REMINDER_MINS_PREF = "reminderMins";

    public static final String REMINDER_PREFS_NAME = "reminderPrefs";

    public static void setReminder(Train train, Station station, int mins, Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences(REMINDER_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(TRAIN_CODE_PREF, train.getTrainCode());
        prefsEditor.putString(STATION_CODE_PREF, station.getCode());
        prefsEditor.putInt(REMINDER_MINS_PREF, mins);
        prefsEditor.commit();
    }

    public static void clearReminder(Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences(REMINDER_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

}
