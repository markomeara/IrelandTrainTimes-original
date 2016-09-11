package ie.markomeara.irelandtraintimes.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ie.markomeara.irelandtraintimes.model.Station;

public class ReminderStartReceiver extends BroadcastReceiver {

    private static final String TAG = ReminderStartReceiver.class.getSimpleName();

    public static final String TRAIN_CODE = "trainCode";
    public static final String STATION = "station";
    public static final String REMINDER_MINS = "reminderMins";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast received");
        String trainCode = intent.getStringExtra(TRAIN_CODE);
        Station station = intent.getParcelableExtra(STATION);
        int reminderMins = intent.getIntExtra(REMINDER_MINS, 0);

        Intent reminderServiceIntent = ReminderService.prepareIntent(context, trainCode, station, reminderMins);

        context.startService(reminderServiceIntent);
    }

    public static Intent prepareIntent(Context ctx, String trainCode, Station station, int reminderMins) {
        Intent intent = new Intent(ctx.getApplicationContext(), ReminderStartReceiver.class);
        intent.setAction("abc");
        intent.putExtra(TRAIN_CODE, trainCode);
        intent.putExtra(STATION, station);
        intent.putExtra(REMINDER_MINS, reminderMins);
        return intent;
    }

}
