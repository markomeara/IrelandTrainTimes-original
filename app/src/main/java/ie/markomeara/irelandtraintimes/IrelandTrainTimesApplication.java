package ie.markomeara.irelandtraintimes;

import android.app.Application;

public class IrelandTrainTimesApplication extends Application {

    private static IrelandTrainTimesApplication sInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        sInstance = this;
    }

    public static IrelandTrainTimesApplication getInstance(){
        return sInstance;
    }

}
