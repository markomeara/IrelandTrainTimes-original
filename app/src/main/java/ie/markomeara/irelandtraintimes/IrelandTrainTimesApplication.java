package ie.markomeara.irelandtraintimes;

import android.app.Application;

public class IrelandTrainTimesApplication extends Application {

    private static IrelandTrainTimesApplication mInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public static IrelandTrainTimesApplication getInstance(){
        return mInstance;
    }

}
