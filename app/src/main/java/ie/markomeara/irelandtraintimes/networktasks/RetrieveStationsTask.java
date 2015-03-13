package ie.markomeara.irelandtraintimes.networktasks;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandtraintimes.activities.fragments.StationListFragment;
import ie.markomeara.irelandtraintimes.storage.StationsDataSource;
import ie.markomeara.irelandtraintimes.exceptions.DBNotAvailableException;


/**
 * Created by Mark on 30/09/2014.
 */
public class RetrieveStationsTask extends AsyncTask<Boolean, Integer, Boolean> {

    private static final String TAG = RetrieveStationsTask.class.getSimpleName();

    private final String allStationsAPI = "http://api.irishrail.ie/realtime/realtime.asmx/getAllStationsXML";
  //  private StationListActivity callingActivity;
    private StationListFragment stationListFragment;
    // private StationListFragment stationListFragment;

    // TODO Remove this activity based constructor
  //  public RetrieveStationsTask(StationListActivity activity){ this.callingActivity = activity; }

    public RetrieveStationsTask(StationListFragment fragment){ this.stationListFragment = fragment; }

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
                  StationsDataSource sds = new StationsDataSource(stationListFragment.getActivity());
                try{
                    if(sds != null) {
                        sds.open();
                        sds.createStationsFromNodes(stationsNodes);
                        sds.close();
                    }
                } catch (SQLException ex) {
                    Log.e(TAG, ex.toString(), ex);
                } catch(DBNotAvailableException ex){
                    Log.e(TAG, ex.toString(), ex);
                } catch(NullPointerException ex){
                    // TODO Figure out why nullpointexception thrown here on screen rotate
                    Log.e(TAG, ex.toString(), ex);
                }
            }
        }

        catch (MalformedURLException ex) { Log.w(TAG, ex); }
        catch (ParserConfigurationException ex) { Log.w(TAG, ex); }
        catch (IOException ex) { Log.w(TAG, ex); }
        catch (SAXException ex) { Log.w(TAG, ex); }

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
            // callingActivity.refreshStationListDisplay();
            stationListFragment.initStationListDisplay();
        }
        Log.i(TAG, "Stations have been updated");
        // TODO This returns null pointer exception if we've already changed activity
        // .... but how can this be if it was passed updateUIImmediately (as this should only be passed
        // when no stations are displayed?!?
 //       Toast toast = Toast.makeText(stationListFragment.getActivity(), "Stations updated", Toast.LENGTH_SHORT);
        // Toast toast = Toast.makeText(callingActivity, "Stations updated", Toast.LENGTH_SHORT);
  //      toast.show();
    }



}
