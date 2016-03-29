package ie.markomeara.irelandtraintimes.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.Train;

public class ReminderStartReceiver extends BroadcastReceiver{

    private static final String TAG = ReminderStartReceiver.class.getSimpleName();

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
