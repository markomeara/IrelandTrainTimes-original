package ie.markomeara.irelandtraintimes;

import android.app.Application;

public class IrelandTrainTimesApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Injector.init(this);
    }

}
