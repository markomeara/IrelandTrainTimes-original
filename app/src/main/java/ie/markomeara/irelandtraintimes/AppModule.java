package ie.markomeara.irelandtraintimes;

import com.mobprofs.retrofit.converters.SimpleXmlConverter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ie.markomeara.irelandtraintimes.managers.ReminderService;
import ie.markomeara.irelandtraintimes.network.IrishRailService;
import ie.markomeara.irelandtraintimes.network.NextTrainsTask;
import ie.markomeara.irelandtraintimes.network.RetrieveStationsTask;
import retrofit.RestAdapter;

/**
 * Created by markomeara on 20/03/2016.
 */
@Module(
    injects = {NextTrainsTask.class, ReminderService.class, RetrieveStationsTask.class}
)
public class AppModule {

    private static final String API_ENDPOINT = "http://api.irishrail.ie/realtime/realtime.asmx";

    @Provides
    @Singleton
    IrishRailService providesIrishRailService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_ENDPOINT)
                .setConverter(new SimpleXmlConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        IrishRailService irishRailService = restAdapter.create(IrishRailService.class);

        return irishRailService;
    }
}
