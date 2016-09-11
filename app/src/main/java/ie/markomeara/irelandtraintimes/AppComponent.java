package ie.markomeara.irelandtraintimes;

import javax.inject.Singleton;

import dagger.Component;
import ie.markomeara.irelandtraintimes.manager.ReminderService;
import ie.markomeara.irelandtraintimes.ui.fragment.StationListFragment;
import ie.markomeara.irelandtraintimes.ui.fragment.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.ui.fragment.TwitterUpdateFragment;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(ReminderService obj);
    void inject(TwitterUpdateFragment obj);
    void inject(StationListFragment obj);
    void inject(StationNextTrainsFragment obj);
    void inject(AppModule.InjectorHelper obj);
}
