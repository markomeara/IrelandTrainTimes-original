package ie.markomeara.irelandtraintimes;

import android.content.Context;
import android.util.Log;

import com.google.common.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ie.markomeara.irelandtraintimes.network.IrishRailApi;
import ie.markomeara.irelandtraintimes.network.IrishRailService;
import ie.markomeara.irelandtraintimes.network.TwitterService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Module
public class AppModule {

    private static final String TAG = AppModule.class.getSimpleName();

    // TODO Move to build.gradle
    private static final String API_ENDPOINT = "http://api.irishrail.ie/realtime/realtime.asmx/";

    private Context mContext;

    public AppModule(Context context){
        mContext = context;
    }

    @Provides
    @Singleton
    public IrishRailService providesIrishRailService(IrishRailApi irishRailApi, EventBus eventBus) {
        return new IrishRailService(irishRailApi, eventBus);
    }

    @Provides
    @Singleton
    public IrishRailApi providesIrishRailApi() {
        Log.d(TAG, "Returning Irish Rail service");
        return createIrishRailApi(API_ENDPOINT);
    }

    protected IrishRailApi createIrishRailApi(String serverUrl) {
        // TODO Handle exception - this is thrown if, eg, site is down or internet is down

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor);

        return new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(okClientBuilder.build())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
                .create(IrishRailApi.class);
    }

    @Provides
    @Singleton
    TwitterService providesTwitterService(EventBus eventBus) {
        return new TwitterService(eventBus);
    }

    @Provides
    @Singleton
    EventBus providesEventBus() {
        return new EventBus();
    }

    public static class InjectorHelper {
        @Inject
        public EventBus mEventBus;
    }
}
