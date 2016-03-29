package ie.markomeara.irelandtraintimes.network;

import ie.markomeara.irelandtraintimes.model.StationList;
import ie.markomeara.irelandtraintimes.model.TrainList;
import retrofit.http.GET;
import retrofit.http.Query;

public interface IrishRailService {

    // Synchronous
    @GET("/getStationDataByCodeXML")
    TrainList getTrainsDueAtStation(@Query("StationCode") String stationCode);

    @GET("/getAllStationsXML")
    StationList getAllStations();
}
