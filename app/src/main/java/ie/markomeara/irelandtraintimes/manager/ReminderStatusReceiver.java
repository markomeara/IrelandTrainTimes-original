package ie.markomeara.irelandtraintimes.manager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.ui.activity.MainActivity;
import ie.markomeara.irelandtraintimes.ui.fragment.TrainDetailsFragment;
import ie.markomeara.irelandtraintimes.model.Train;

public class ReminderStatusReceiver extends BroadcastReceiver {

    private TrainDetailsFragment ui;

    public ReminderStatusReceiver(TrainDetailsFragment f){
        super();
        this.ui = f;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get extra data included in the Intent
        Train trainDetails = intent.getParcelableExtra("trainDetails");
        ui.newTrainDetailsReceived(trainDetails);
        notification(context, trainDetails);
    }

    public void notification(Context context, Train trainDetails) {
        // Set Notification Title
        String strtitle = "Train departing in "  + trainDetails.getDueIn() + " mins";
        // Open NotificationView Class on Notification Click
  //  Intent intent = new Intent(context, NotificationView.class);
        Intent intent = new Intent(context, MainActivity.class);
        // Send data to NotificationView Class
     intent.putExtra("title", strtitle);
     intent.putExtra("text", trainDetails.getLatestInfo());
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                // Set Icon
                .setSmallIcon(R.drawable.sample_profilepic)
                        // Set Ticker Message
                .setTicker(trainDetails.getLatestInfo() + ". " + trainDetails.getDueIn() + " mins away")
                        // Set Title
                .setContentTitle(strtitle)
                        // Set Text
                .setContentText(trainDetails.getLatestInfo())
                        // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                        // Dismiss Notification
                .setAutoCancel(false);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }
}
