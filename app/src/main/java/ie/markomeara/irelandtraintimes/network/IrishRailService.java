package ie.markomeara.irelandtraintimes.network;

import com.google.common.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.StationList;
import ie.markomeara.irelandtraintimes.model.TrainList;

public class IrishRailService {

    private StationList mCachedStationList;

    IrishRailApi mIrishRailApi;
    EventBus mEventBus;

    public IrishRailService(IrishRailApi irishRailApi, EventBus eventBus) {
        mIrishRailApi = irishRailApi;
        mEventBus = eventBus;
    }

    public void fetchTrainsDueAtStation(String stationCode) {
        mIrishRailApi.getTrainsDueAtStation(stationCode).enqueue(new ApiCallback<TrainList>());
    }

    public StationList getCachedStationList() {
        return mCachedStationList;
    }

    public void fetchAllStations(boolean forceRefresh) {
        if(!forceRefresh && mCachedStationList != null) {
            mEventBus.post(mCachedStationList);
            return;
        }
        mIrishRailApi.getAllStations().enqueue(new ApiCallback<StationList>() {
            @Override
            protected void processResult(StationList stationList) {
                stationList = removeDuplicates(stationList);
                mCachedStationList = stationList;
            }
        });
    }

    /**
     * This is to deal with an issue with the Irish Rail API where some stations appear
     * twice with different IDs but with the same name. The duplicate stations seem to all
     * have an ID of over 900
     * @param stationList
     */
    private StationList removeDuplicates(StationList stationList) {
        List<Station> dedupedStations = new ArrayList<>();
        for(Station station : stationList.getStations()) {
            if(station.getId() < 900) {
                dedupedStations.add(station);
            }
        }
        stationList.setStations(dedupedStations);
        return stationList;
    }

}
