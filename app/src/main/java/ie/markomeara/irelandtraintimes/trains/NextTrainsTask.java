package ie.markomeara.irelandtraintimes.trains;

import android.os.AsyncTask;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandtraintimes.activities.fragments.StationNextTrainsFragment;

/**
 * Created by Mark on 26/10/2014.
 */
public class NextTrainsTask extends AsyncTask<Station, Integer, List<Train>> {

    private static final String TAG = NextTrainsTask.class.getSimpleName();

    private StationNextTrainsFragment callingFragment;

    public NextTrainsTask(StationNextTrainsFragment fragment){
        this.callingFragment = fragment;
    }

    @Override
    protected List<Train> doInBackground(Station[] stationParams) {

        List<Train> relevantTrains = null;

        if(stationParams.length >= 1){
            Station station = stationParams[0];
            String stnCode = station.getCode();

            try {
                List<Train> allTrains = IrishRailAPI.getTrainsFromStationCode(stnCode);
                relevantTrains = removeTrainsTerminatingAtStation(allTrains, station);
            }

            catch(MalformedURLException ex) { Log.w(TAG, ex); }
            catch(ParserConfigurationException ex){ Log.w(TAG, ex); }
            catch(IOException ex){ Log.w(TAG, ex); }
            catch(SAXException ex){ Log.w(TAG, ex); }
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
        List<Train> reducedTrainList = new ArrayList<Train>();

        for(Train selectedTrain : trains){

            String trainDest = selectedTrain.getDestination();

            if(!trainDest.equals(stationName)){
                reducedTrainList.add(selectedTrain);
            }
        }

        return reducedTrainList;

    }

}
