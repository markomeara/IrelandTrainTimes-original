package ie.markomeara.irelandtraintimes.utils;

import android.content.Context;

import ie.markomeara.irelandtraintimes.networktasks.RetrieveStationsTask;

/**
 * Created by Mark on 30/09/2014.
 */
public class StationUtils {

    public static void getAllStations(Context c) {

        // TODO Remove this class... really not needed at all
        new RetrieveStationsTask(c).execute();

    }
}
