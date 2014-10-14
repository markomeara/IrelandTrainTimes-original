package ie.markomeara.irelandrailtimes.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandrailtimes.networktasks.RetrieveStationsTask;

/**
 * Created by Mark on 30/09/2014.
 */
public class StationUtils {

    public static void getAllStations() {

        // TODO Caching
        new RetrieveStationsTask().execute();

    }
}
