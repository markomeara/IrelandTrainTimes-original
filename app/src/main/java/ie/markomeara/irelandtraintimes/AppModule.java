package ie.markomeara.irelandtraintimes;

import android.app.Application;

import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobprofs.retrofit.converters.SimpleXmlConverter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ie.markomeara.irelandtraintimes.manager.DatabaseOrmHelper;
import ie.markomeara.irelandtraintimes.manager.ReminderService;
import ie.markomeara.irelandtraintimes.network.IrishRailService;
import ie.markomeara.irelandtraintimes.network.NextTrainsTask;
import ie.markomeara.irelandtraintimes.network.RetrieveStationsTask;
import ie.markomeara.irelandtraintimes.network.TweetUpdaterTask;
import ie.markomeara.irelandtraintimes.ui.fragment.StationListFragment;
import ie.markomeara.irelandtraintimes.ui.fragment.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.ui.fragment.TwitterUpdateFragment;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

@Module(
    injects = {NextTrainsTask.class, ReminderService.class, RetrieveStationsTask.class,
            TwitterUpdateFragment.class, StationListFragment.class, StationNextTrainsFragment.class,
            TweetUpdaterTask.class}
)
public class AppModule {

    private static final String TAG = AppModule.class.getSimpleName();

//    private static final String API_ENDPOINT = "http://demo0511310.mockable.io/";
    private static final String API_ENDPOINT = "http://api.irishrail.ie/realtime/realtime.asmx";

    private Application mApplication;

    public AppModule(Application application){
        mApplication = application;
    }

    @Provides
    @Singleton
    IrishRailService providesIrishRailService() {
        Log.d(TAG, "Returning Irish Rail service");
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_ENDPOINT)
                .setConverter(new SimpleXmlConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        throw new RuntimeException("Error contacting Irish Rail API");
                    }
                })
                .build();

        IrishRailService irishRailService = restAdapter.create(IrishRailService.class);

        return irishRailService;
    }

    @Provides
    @Singleton
    DatabaseOrmHelper getDbHelper(){
        Log.d(TAG, "Returning database helper");
        return OpenHelperManager.getHelper(mApplication, DatabaseOrmHelper.class);
    }
}
