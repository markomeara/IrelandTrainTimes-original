package ie.markomeara.irelandtraintimes.network;

import com.google.common.eventbus.EventBus;

import javax.inject.Inject;

import ie.markomeara.irelandtraintimes.AppModule;
import ie.markomeara.irelandtraintimes.Injector;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallback<T> implements Callback<T> {

    public static class InjectionHelper {
        @Inject EventBus mBus;
    }

    private final EventBus mEventBus;

    public ApiCallback() {
        AppModule.InjectorHelper injectionHelper = new AppModule.InjectorHelper();
        Injector.get().inject(injectionHelper);
        mEventBus = injectionHelper.mEventBus;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            mEventBus.post(response.body());
        } else {
            postFailure();
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        postFailure();
    }

    private void postFailure() {
        mEventBus.post(new ApiFailureEvent());
    }

    public static class ApiFailureEvent {}
}
