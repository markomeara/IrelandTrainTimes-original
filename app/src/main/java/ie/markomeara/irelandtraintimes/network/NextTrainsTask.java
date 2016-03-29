package ie.markomeara.irelandtraintimes.network;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ie.markomeara.irelandtraintimes.Injector;
import ie.markomeara.irelandtraintimes.ui.fragment.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.Train;
import ie.markomeara.irelandtraintimes.model.TrainList;

public class NextTrainsTask extends AsyncTask<Station, Integer, List<Train>> {

    private static final String TAG = NextTrainsTask.class.getSimpleName();

    @Inject
    IrishRailService irishRailService;

    private StationNextTrainsFragment callingFragment;

    public NextTrainsTask(StationNextTrainsFragment fragment){
        Injector.inject(this);
        this.callingFragment = fragment;
    }

    @Override
    protected List<Train> doInBackground(Station[] stationParams) {

        List<Train> relevantTrains = null;

        if(stationParams.length >= 1){
            Station station = stationParams[0];
            String stnCode = station.getCode();

            TrainList allTrains = irishRailService.getTrainsDueAtStation(stnCode);
            relevantTrains = removeTrainsTerminatingAtStation(allTrains.getTrainList(), station);

        }
        else{
            Log.w(TAG, "Parameters are not as expected");
        }
        return relevantTrains;
    }

    @Override
    protected void onPostExecute(List<Train> trainsDue) {
        callingFragment.displayTimes(trainsDue);
    }


    private List<Train> removeTrainsTerminatingAtStation(List<Train> trains, Station station){

        String stationName = station.getName();
        List<Train> reducedTrainList = new ArrayList<>();

        for(Train selectedTrain : trains){

            String trainDest = selectedTrain.getDestination();

            if(!trainDest.equals(stationName)){
                reducedTrainList.add(selectedTrain);
            }
        }

        return reducedTrainList;

    }

}
