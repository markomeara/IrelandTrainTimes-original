package ie.markomeara.irelandtraintimes.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mark on 21/03/15.
 */
public class ReminderReceiver extends BroadcastReceiver{

    private static final String TAG = ReminderReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast received");
        Intent background = new Intent(context, ReminderService.class);
        context.startService(background);
    }

}
