package ie.markomeara.irelandtraintimes.networktasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandtraintimes.Station;
import ie.markomeara.irelandtraintimes.activities.StationListActivity;
import ie.markomeara.irelandtraintimes.db.StationsDataSource;


/**
 * Created by Mark on 30/09/2014.
 */
public class RetrieveStationsTask extends AsyncTask<Boolean, Integer, Boolean> {

    private final String allStationsAPI = "http://api.irishrail.ie/realtime/realtime.asmx/getAllStationsXML";
    private StationListActivity callingActivity;

    public RetrieveStationsTask(StationListActivity activity){ this.callingActivity = activity; }

    @Override
    protected Boolean doInBackground(Boolean[] updateUIParam) {

        try {
            URL url = new URL(allStationsAPI);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList stationsNodes = doc.getElementsByTagName("objStation");

            // Using 130 as arbitrary value to just ensure we probably did get all the stations and not just rubbish
            if(stationsNodes.getLength() > 130) {
                StationsDataSource sds = new StationsDataSource(callingActivity);
                try{
                    sds.open();
                    sds.createStationsFromNodes(stationsNodes);
                    sds.close();
                }
                catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
        }

        catch (MalformedURLException ex) { Log.w("Error updating stations", ex); }
        catch (ParserConfigurationException ex) { Log.w("Error updating stations", ex); }
        catch (IOException ex) { Log.w("Error updating stations", ex); }
        catch (SAXException ex) { Log.w("Error updating stations", ex); }

        boolean updateUI = false;
        if(updateUIParam.length > 0){
            updateUI = updateUIParam[0];
        }

        return updateUI;
    }

    @Override
    protected void onPostExecute(Boolean updateUIImmediately) {
        // If station list is being initialized for first time, then refresh UI immediately
        if(updateUIImmediately){
            callingActivity.refreshStationListDisplay();
        }
        Log.i("Network Task", "Stations have been updated");
        Toast toast = Toast.makeText(callingActivity, "Stations updated", Toast.LENGTH_SHORT);
        toast.show();
    }



}
