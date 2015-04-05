package ie.markomeara.irelandtraintimes.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ie.markomeara.irelandtraintimes.trains.Station;
import ie.markomeara.irelandtraintimes.trains.Train;

/**
 * Created by mark on 21/03/15.
 */
public class ReminderReceiver extends BroadcastReceiver{

    private static final String TAG = ReminderReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast received");
        Train train = intent.getParcelableExtra("train");
        Station station = intent.getParcelableExtra("station");
        int reminderMins = intent.getIntExtra("reminderMins", 0);

        Intent reminderServiceIntent = new Intent(context, ReminderService.class);
        reminderServiceIntent.putExtra("train", train);
        reminderServiceIntent.putExtra("station", station);
        reminderServiceIntent.putExtra("reminderMins", reminderMins);

        context.startService(reminderServiceIntent);
    }

}
