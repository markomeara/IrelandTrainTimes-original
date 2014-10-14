package ie.markomeara.irelandrailtimes.networktasks;

import android.os.AsyncTask;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandrailtimes.Station;


/**
 * Created by Mark on 30/09/2014.
 */
public class RetrieveStationsTask extends AsyncTask<Object, Integer, ArrayList<Station>> {

    private final String allStationsAPI = "http://api.irishrail.ie/realtime/realtime.asmx/getAllStationsXML";

    @Override
    protected ArrayList<Station> doInBackground(Object[] params) {

        ArrayList stationsList = new ArrayList<Station>();

        try {
            URL url = new URL(allStationsAPI);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList stations = doc.getElementsByTagName("objStation");
            for (int i = 0; i < stations.getLength(); i++) {

                if (stations.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element stationElem = (Element) stations.item(i);

                    // TODO A shitload of null checks

                    String stationName = stationElem.getElementsByTagName("StationDesc").item(0).getTextContent();
                    String stationAlias = stationElem.getElementsByTagName("StationAlias").item(0).getTextContent();
                    double stationLat = Double.parseDouble(stationElem.getElementsByTagName("StationLatitude").item(0).getTextContent());
                    double stationLong = Double.parseDouble(stationElem.getElementsByTagName("StationLongitude").item(0).getTextContent());
                    String stationCode = stationElem.getElementsByTagName("StationCode").item(0).getTextContent();
                    int stationId = Integer.parseInt(stationElem.getElementsByTagName("StationId").item(0).getTextContent());

                    Station station = new Station(stationName, stationAlias, stationLat, stationLong, stationCode, stationId);
                    stationsList.add(station);
                }

            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }

        return stationsList;
    }

    @Override
    protected void onPostExecute(ArrayList<Station> stations) {

    }

}
