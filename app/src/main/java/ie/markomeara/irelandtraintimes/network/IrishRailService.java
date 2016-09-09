package ie.markomeara.irelandtraintimes.network;

import ie.markomeara.irelandtraintimes.model.StationList;
import ie.markomeara.irelandtraintimes.model.TrainList;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Call;

public interface IrishRailService {

    // Synchronous
    @GET("getStationDataByCodeXML")
    Call<TrainList> getTrainsDueAtStation(@Query("StationCode") String stationCode);

    @GET("getAllStationsXML")
    Call<StationList> getAllStations();
}
