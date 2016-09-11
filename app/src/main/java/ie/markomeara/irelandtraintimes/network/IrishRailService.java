package ie.markomeara.irelandtraintimes.network;

import ie.markomeara.irelandtraintimes.model.StationList;
import ie.markomeara.irelandtraintimes.model.TrainList;

public class IrishRailService {

    IrishRailApi mIrishRailApi;

    public IrishRailService(IrishRailApi irishRailApi) {
        mIrishRailApi = irishRailApi;
    }

    public void fetchTrainsDueAtStation(String stationCode) {
        mIrishRailApi.getTrainsDueAtStation(stationCode).enqueue(new ApiCallback<TrainList>());
    }

    public void fetchAllStations() {
        mIrishRailApi.getAllStations().enqueue(new ApiCallback<StationList>());
    }
}
