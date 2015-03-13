package ie.markomeara.irelandtraintimes.utils;

import android.content.SharedPreferences;

import ie.markomeara.irelandtraintimes.Train;

/**
 * Created by Mark on 02/01/2015.
 */

public class ReminderUtils {

    public static String TRAIN_CODE_PREF = "trainCode";
    public static String REMINDER_MINS_PREF = "reminderMins";

    public static void setReminder(Train train, int mins, SharedPreferences settings){
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString(TRAIN_CODE_PREF, train.getTrainCode());
        settingsEditor.putInt(REMINDER_MINS_PREF, mins);
        settingsEditor.commit();
    }

    public static void clearReminder(SharedPreferences settings){
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.clear();
        settingsEditor.commit();
    }

}
