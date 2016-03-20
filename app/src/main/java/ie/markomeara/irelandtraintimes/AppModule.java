package ie.markomeara.irelandtraintimes;

import com.mobprofs.retrofit.converters.SimpleXmlConverter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ie.markomeara.irelandtraintimes.manager.ReminderService;
import ie.markomeara.irelandtraintimes.network.IrishRailService;
import ie.markomeara.irelandtraintimes.network.NextTrainsTask;
import ie.markomeara.irelandtraintimes.network.RetrieveStationsTask;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by markomeara on 20/03/2016.
 */
@Module(
    injects = {NextTrainsTask.class, ReminderService.class, RetrieveStationsTask.class}
)
public class AppModule {

//    private static final String API_ENDPOINT = "http://demo0511310.mockable.io/";
    private static final String API_ENDPOINT = "http://api.irishrail.ie/realtime/realtime.asmx";

    @Provides
    @Singleton
    IrishRailService providesIrishRailService() {
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
}
