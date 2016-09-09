package ie.markomeara.irelandtraintimes;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ie.markomeara.irelandtraintimes.manager.DatabaseOrmHelper;
import ie.markomeara.irelandtraintimes.network.IrishRailService;
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
    public IrishRailService providesIrishRailApi() {
        Log.d(TAG, "Returning Irish Rail service");
        return createIrishRailApi(API_ENDPOINT);
    }

    protected IrishRailService createIrishRailApi(String serverUrl) {
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
                .create(IrishRailService.class);
    }

    @Provides
    @Singleton
    DatabaseOrmHelper getDbHelper(){
        Log.d(TAG, "Returning database helper");
        return OpenHelperManager.getHelper(mContext, DatabaseOrmHelper.class);
    }
}
