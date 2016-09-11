package ie.markomeara.irelandtraintimes.network;

import com.google.common.eventbus.EventBus;

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
                mCachedStationList = stationList;
            }
        });
    }
}
