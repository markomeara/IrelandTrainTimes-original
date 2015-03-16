package ie.markomeara.irelandtraintimes.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by mark on 16/03/15.
 */
public class ReminderService extends IntentService {


    private static final String TAG = ReminderService.class.getSimpleName();

    private final String allStationsAPI = "http://api.irishrail.ie/realtime/realtime.asmx/getAllStationsXML";
    private final String trainDetailsAPI = "http://api.irishrail.ie/realtime/realtime.asmx/getTrainMovementsXML?TrainId=e109&TrainDate=21 dec 2011";

    public ReminderService(){
        // Worker thread name: ReminderService
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "TEST TEST TEST");
        // TODO Call startForeground
    }

    private void getLatestTrainInfo(String trainCode){

    }

    /*
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    // Once this executes, service runs indefinitely until stopSelf() or stopService() is called
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return 0;
    }

    */
}
