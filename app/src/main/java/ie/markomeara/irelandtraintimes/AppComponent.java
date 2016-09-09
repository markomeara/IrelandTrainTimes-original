package ie.markomeara.irelandtraintimes;

import javax.inject.Singleton;

import dagger.Component;
import ie.markomeara.irelandtraintimes.manager.ReminderService;
import ie.markomeara.irelandtraintimes.ui.fragment.StationListFragment;
import ie.markomeara.irelandtraintimes.ui.fragment.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.ui.fragment.TwitterUpdateFragment;

/**
 * Created by markomeara on 10/09/2016.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(ReminderService obj);
    void inject(TwitterUpdateFragment obj);
    void inject(StationListFragment obj);
    void inject(StationNextTrainsFragment obj);
}
