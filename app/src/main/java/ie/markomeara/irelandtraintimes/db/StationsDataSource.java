package ie.markomeara.irelandtraintimes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.Station;

/**
 * Created by Mark on 19/10/2014.
 */
public class StationsDataSource {

    private static final String TAG = StationsDataSource.class.getSimpleName();

    private SQLiteDatabase db;
    private DBManager dbManager;
    private String[] allColumns = { DBManager.COLUMN_ID, DBManager.COLUMN_STN_NAME,
            DBManager.COLUMN_STN_ALIAS , DBManager.COLUMN_STN_LAT, DBManager.COLUMN_STN_LONG,
            DBManager.COLUMN_STN_CODE, DBManager.COLUMN_STN_FAV};

    public StationsDataSource(Context context){
        dbManager = new DBManager(context);
    }

    public void open() throws SQLException {
        db = dbManager.getWritableDatabase();
    }

    public void close() {
        dbManager.close();
    }

    public Station createStation(int id, String name, String alias, double latitude, double longitude, String code){
        return createStation(id, name, alias, latitude, longitude, code, false);
    }

    public Station createStation(int id, String name, String alias, double latitude, double longitude, String code, boolean fav) {
        ContentValues values = new ContentValues();
        values.put(DBManager.COLUMN_ID, id);
        values.put(DBManager.COLUMN_STN_NAME, name);
        values.put(DBManager.COLUMN_STN_ALIAS, alias);
        values.put(DBManager.COLUMN_STN_LAT, latitude);
        values.put(DBManager.COLUMN_STN_LONG, longitude);
        values.put(DBManager.COLUMN_STN_CODE, code);
        values.put(DBManager.COLUMN_STN_FAV, fav);

        long entryId = db.insert(DBManager.TABLE_STATIONS, null, values);
        Log.i(TAG, "Station created with id " + id);
        Cursor cursor = db.query(DBManager.TABLE_STATIONS, allColumns, DBManager.COLUMN_ID + " = " + entryId, null, null, null, null);

        Station newStation = null;
        if(cursor.moveToFirst()){
            newStation = cursorToStation(cursor);
        }
        cursor.close();
        return newStation;
    }

    public List<Station> createStationsFromNodes(NodeList stationsNodes){

        List<Station> createdStationsList = new ArrayList<Station>();
        db.beginTransaction();
        clearAllStations();

        for (int i = 0; i < stationsNodes.getLength(); i++) {

            if (stationsNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element stationElem = (Element) stationsNodes.item(i);

                // TODO A shitload of null checks
                int stationId = Integer.parseInt(stationElem.getElementsByTagName("StationId").item(0).getTextContent());
                String stationName = stationElem.getElementsByTagName("StationDesc").item(0).getTextContent();
                String stationAlias = stationElem.getElementsByTagName("StationAlias").item(0).getTextContent();
                double stationLat = Double.parseDouble(stationElem.getElementsByTagName("StationLatitude").item(0).getTextContent());
                double stationLong = Double.parseDouble(stationElem.getElementsByTagName("StationLongitude").item(0).getTextContent());
                String stationCode = stationElem.getElementsByTagName("StationCode").item(0).getTextContent();

                Station createdStation = createStation(stationId, stationName, stationAlias, stationLat, stationLong, stationCode);
                if(createdStation != null) {
                    createdStationsList.add(createdStation);
                }

            }

        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return createdStationsList;
    }

    public Station updateFavourite(Station stn, boolean fav){
        ContentValues cv = new ContentValues();
        cv.put(DBManager.COLUMN_STN_FAV, false);

        db.update(DBManager.TABLE_STATIONS, cv, DBManager.COLUMN_ID + " = " + stn.getId(), null);

        Cursor cursor = db.query(DBManager.TABLE_STATIONS, allColumns, DBManager.COLUMN_ID + " = " + stn.getId(), null, null, null, null);

        cursor.moveToFirst();
        Station updatedStation = cursorToStation(cursor);
        cursor.close();
        return updatedStation;
    }

    public List<Station> retrieveAllStations(){

        // Ordering by name
        Cursor cursor = db.query(DBManager.TABLE_STATIONS, allColumns, null, null, null, null, DBManager.COLUMN_STN_NAME);
        List<Station> stns = new ArrayList<Station>();

        while(cursor.moveToNext()){
            stns.add(cursorToStation(cursor));
        }

        return stns;
    }

    public Station retrieveStationById(long id){

        Cursor cursor = db.query(DBManager.TABLE_STATIONS, allColumns, DBManager.COLUMN_ID +" = "+ id, null, null, null, null);
        cursor.moveToFirst();
        return cursorToStation(cursor);

    }

    public void clearAllStations(){
        db.delete(DBManager.TABLE_STATIONS, null, null);
    }

    private Station cursorToStation(Cursor cursor){

        int id = cursor.getInt(cursor.getColumnIndex(DBManager.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(DBManager.COLUMN_STN_NAME));
        String alias = cursor.getString(cursor.getColumnIndex(DBManager.COLUMN_STN_ALIAS));
        double latitude = cursor.getDouble(cursor.getColumnIndex(DBManager.COLUMN_STN_LAT));
        double longitude = cursor.getDouble(cursor.getColumnIndex(DBManager.COLUMN_STN_LONG));
        String code = cursor.getString(cursor.getColumnIndex(DBManager.COLUMN_STN_CODE));
        boolean fav = (cursor.getInt(cursor.getColumnIndex(DBManager.COLUMN_STN_FAV)) > 0);

        Station stn = new Station(id, name, alias, latitude, longitude, code, fav);

        Log.i(TAG, "Returning station with id " + id);
        return stn;
    }
}
