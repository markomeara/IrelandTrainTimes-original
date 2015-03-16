package ie.markomeara.irelandtraintimes.trains;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

        List<Train> trains = null;

        if(stationParams.length >= 1){
            Station station = stationParams[0];
            String code = station.getCode();

            try {
                URL url = new URL("http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML?StationCode=" + code);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

                NodeList trainsNodes = doc.getElementsByTagName("objStationData");
                trains = createTrainsFromNodesExclStation(trainsNodes, station);
            }
            catch(MalformedURLException ex) { Log.w(TAG, ex); }
            catch(ParserConfigurationException ex){ Log.w(TAG, ex); }
            catch(IOException ex){ Log.w(TAG, ex); }
            catch(SAXException ex){ Log.w(TAG, ex); }
        }
        else{
            Log.w(TAG, "Parameters are not as expected");
        }
        return trains;
    }

    @Override
    protected void onPostExecute(List<Train> trainsDue) {
        callingFragment.displayTimes(trainsDue);
    }

    /**
     * Create trains from nodes but ignore trains destined for the requested station
     * @param trainsNodes
     * @return
     */
    private List<Train> createTrainsFromNodesExclStation(NodeList trainsNodes, Station selectedStation) {

        List<Train> trains = new ArrayList<Train>();

        for (int i = 0; i < trainsNodes.getLength(); i++) {

            if (trainsNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element trainElem = (Element) trainsNodes.item(i);

                // TODO A load of null checks
                // TODO Use constants for element names
                String dest = trainElem.getElementsByTagName("Destination").item(0).getTextContent();
                if(!dest.equals(selectedStation.getName())) {

                    Train createdTrain = new Train();
                    populateTrainFromXml(createdTrain, trainElem);
                    // TODO Exclude trains that terminate at the station displayed
                    trains.add(createdTrain);
                }
            }
        }

        return trains;
    }

    private void populateTrainFromXml(Train train, Element trainElem){
        String code = trainElem.getElementsByTagName("Traincode").item(0).getTextContent();
        String destination = trainElem.getElementsByTagName("Destination").item(0).getTextContent();
        String origin = trainElem.getElementsByTagName("Origin").item(0).getTextContent();
        String latestInfo = trainElem.getElementsByTagName("Lastlocation").item(0).getTextContent();
        String direction = trainElem.getElementsByTagName("Direction").item(0).getTextContent();
        String trainType = trainElem.getElementsByTagName("Traintype").item(0).getTextContent();
        int dueIn = Integer.parseInt(trainElem.getElementsByTagName("Duein").item(0).getTextContent());
        int late = Integer.parseInt(trainElem.getElementsByTagName("Late").item(0).getTextContent());
        String status = trainElem.getElementsByTagName("Status").item(0).getTextContent();
        String expArrivalTime = trainElem.getElementsByTagName("Exparrival").item(0).getTextContent();
        String expDepartTime = trainElem.getElementsByTagName("Expdepart").item(0).getTextContent();
        String schArrivalTime = trainElem.getElementsByTagName("Scharrival").item(0).getTextContent();
        String schDepartTime = trainElem.getElementsByTagName("Schdepart").item(0).getTextContent();

        String destExpArrivalTime = trainElem.getElementsByTagName("Destinationtime").item(0).getTextContent();
        String originTime = trainElem.getElementsByTagName("Origintime").item(0).getTextContent();
        String trainDate = trainElem.getElementsByTagName("Traindate").item(0).getTextContent();

        // Using phone time instead of API update time to avoid parsing and comparison problems etc
        Date updateTime = new Date();

        train.setTrainCode(code);
        train.setDestination(destination);
        train.setOrigin(origin);
        train.setLatestInfo(latestInfo);
        train.setDirection(direction);
        train.setTrainType(trainType);
        train.setDueIn(dueIn);
        train.setDelayMins(late);
        train.setStatus(status);
        train.setExpArrival(expArrivalTime);
        train.setExpDepart(expDepartTime);
        train.setSchArrival(schArrivalTime);
        train.setSchDepart(schDepartTime);
        train.setDestArrivalTime(destExpArrivalTime);
        train.setOriginTime(originTime);
        train.setTrainDate(trainDate);
        train.setUpdateTime(updateTime);

    }
}
