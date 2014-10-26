package ie.markomeara.irelandtraintimes.networktasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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

import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.Train;

/**
 * Created by Mark on 26/10/2014.
 */
public class NextTrainsTask extends AsyncTask<Station, Integer, List<Train>> {

    private Context currentContext;
    private TextView textView;

    public NextTrainsTask(Context c, TextView textView){
        this.currentContext = c;
        this.textView = textView;
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
            catch(MalformedURLException ex) { Log.w("Error getting trains at station", ex); }
            catch(ParserConfigurationException ex){ Log.w("Error getting trains at station", ex); }
            catch(IOException ex){ Log.w("Error getting trains at station", ex); }
            catch(SAXException ex){ Log.w("Error getting trains at station", ex); }
        }
        else{
            Log.w("Error getting trains at station", "Parameters are not as expected");
        }
        return trains;
    }

    @Override
    protected void onPostExecute(List<Train> trainsDue) {

        String trainsText = "";
        for(int i = 0; i < trainsDue.size(); i++) {
            Train train = trainsDue.get(i);
            trainsText += train.getDueIn() + " " + train.getDestination() + " (" + train.getDirection() + ")\n";
        }

        if(!trainsText.isEmpty()){
            textView.setText(trainsText);
        }
        else{
            textView.setText("No trains found");
        }
    }

    /**
     * Create trains from nodes but ignore trains destined for the requested station
     * @param trainsNodes
     * @return
     */
    private List<Train> createTrainsFromNodesExclStation(NodeList trainsNodes, Station station) {

        List<Train> trains = new ArrayList<Train>();

        for (int i = 0; i < trainsNodes.getLength(); i++) {

            if (trainsNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element trainElem = (Element) trainsNodes.item(i);

                // TODO A load of null checks
                // TODO Use constants for element names
                String dest = trainElem.getElementsByTagName("Destination").item(0).getTextContent();
                if(!dest.equals(station.getName())) {
                    String code = trainElem.getElementsByTagName("Traincode").item(0).getTextContent();
                    String origin = trainElem.getElementsByTagName("Origin").item(0).getTextContent();
                    String latestInfo = trainElem.getElementsByTagName("Lastlocation").item(0).getTextContent();
                    String direction = trainElem.getElementsByTagName("Direction").item(0).getTextContent();
                    String trainType = trainElem.getElementsByTagName("Traintype").item(0).getTextContent();
                    int dueIn = Integer.parseInt(trainElem.getElementsByTagName("Duein").item(0).getTextContent());
                    int late = Integer.parseInt(trainElem.getElementsByTagName("Late").item(0).getTextContent());

                    // Just using phone time instead of API update time to avoid parsing and comparison problems etc
                    Date updateTime = new Date();

                    Train createdTrain = new Train(code, origin, dest, latestInfo, direction, trainType, dueIn, late, updateTime);

                    // TODO Exclude trains that terminate at the station displayed
                    trains.add(createdTrain);
                }
            }
        }

        return trains;
    }


}
