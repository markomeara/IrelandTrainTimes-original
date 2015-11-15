package ie.markomeara.irelandtraintimes.network;

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
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.Train;

/**
 * Created by mark on 16/03/15.
 */
public class IrishRailAPIUtil {

    private static final String STATION_DATA_BY_CODE_RAW_URL = "http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML?StationCode=%s";
    public static final String ALL_STATIONS_RAW_URL = "http://api.irishrail.ie/realtime/realtime.asmx/getAllStationsXML";

    public static URL stationDataByCodeURL(String stnCode) throws MalformedURLException {
        return new URL(String.format(STATION_DATA_BY_CODE_RAW_URL, stnCode));
    }

    /**
     * Return all trains due to pass through selected station.
     * NOTE: This includes trains that terminate at selected station
     *
     * @param stnCode
     * @return trains
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static List<Train> getTrainsFromStationCode(String stnCode) throws IOException, SAXException, ParserConfigurationException {
        NodeList trainsNodes = getTrainNodesFromStationCode(stnCode);
        List<Train> trains = createTrainsFromNodes(trainsNodes);
        return trains;
    }

    public static List<Station> getAllStations() throws ParserConfigurationException, IOException, SAXException {

        NodeList stationNodes = getAllStationNodes();
        List<Station> allStations = createStationsFromNodes(stationNodes);
        return allStations;
    }

    public static Train getTrainAtStationCode(String trainCode, String stnCode) throws ParserConfigurationException, SAXException, IOException {
        Train relevantTrain = null;

        List<Train> allTrainsAtStation = getTrainsFromStationCode(stnCode);
        for(Train train : allTrainsAtStation){
            if(train.getTrainCode().equals(trainCode)){
                relevantTrain = train;
                break;
            }
        }

        return relevantTrain;

    }

    private static NodeList getAllStationNodes() throws ParserConfigurationException, IOException, SAXException {
        URL url = new URL(IrishRailAPIUtil.ALL_STATIONS_RAW_URL);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(url.openStream()));
        doc.getDocumentElement().normalize();
        return doc.getElementsByTagName("objStation");
    }

    private static NodeList getTrainNodesFromStationCode(String stnCode) throws ParserConfigurationException, IOException, SAXException {
        URL url = stationDataByCodeURL(stnCode);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(url.openStream()));
        doc.getDocumentElement().normalize();
        return doc.getElementsByTagName("objStationData");
    }

    private static List<Station> createStationsFromNodes(NodeList stationsNodes){

        List<Station> createdStationsList = new LinkedList<Station>();

        for (int i = 0; i < stationsNodes.getLength(); i++) {

            if (stationsNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element stationElem = (Element) stationsNodes.item(i);

                // TODO A shitload of null checks
                int stationId = Integer.parseInt(stationElem.getElementsByTagName("StationId").item(0).getTextContent());
                String stationName = stationElem.getElementsByTagName("StationDesc").item(0).getTextContent();
                stationName = stationName.trim();
                String stationAlias = stationElem.getElementsByTagName("StationAlias").item(0).getTextContent();
                stationAlias = stationAlias.trim();
                String displayName = !stationAlias.isEmpty() ? stationAlias : stationName;
                double stationLat = Double.parseDouble(stationElem.getElementsByTagName("StationLatitude").item(0).getTextContent());
                double stationLong = Double.parseDouble(stationElem.getElementsByTagName("StationLongitude").item(0).getTextContent());
                String stationCode = stationElem.getElementsByTagName("StationCode").item(0).getTextContent();
                stationCode = stationCode.trim();

                Station createdStation = new Station(stationId, stationName, stationAlias, displayName, stationLat, stationLong, stationCode);

                createdStationsList.add(createdStation);
            }
        }

        return createdStationsList;
    }


    private static Train createTrainFromXml(Element trainElem) {
        Train train = new Train();
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

        return train;

    }

    /**
     * Create trains from nodes
     * @param trainsNodes
     * @return
     */
    private static List<Train> createTrainsFromNodes(NodeList trainsNodes) {

        List<Train> trains = new ArrayList<Train>();

        for (int i = 0; i < trainsNodes.getLength(); i++) {

            if (trainsNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element trainElem = (Element) trainsNodes.item(i);

                // TODO A load of null checks
                // TODO Use constants for element names

                Train createdTrain = IrishRailAPIUtil.createTrainFromXml(trainElem);
                trains.add(createdTrain);

            }
        }

        return trains;
    }
}
