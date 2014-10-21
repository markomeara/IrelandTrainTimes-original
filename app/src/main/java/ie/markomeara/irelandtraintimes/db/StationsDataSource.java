package ie.markomeara.irelandtraintimes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.Station;

/**
 * Created by Mark on 19/10/2014.
 */
public class StationsDataSource {

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

    public Station createStation(int id, String name, String alias, double latitude, double longitude, String code, boolean fav){
        ContentValues values = new ContentValues();
        values.put(DBManager.COLUMN_ID, id);
        values.put(DBManager.COLUMN_STN_NAME, name);
        values.put(DBManager.COLUMN_STN_ALIAS, alias);
        values.put(DBManager.COLUMN_STN_LAT, latitude);
        values.put(DBManager.COLUMN_STN_LONG, longitude);
        values.put(DBManager.COLUMN_STN_CODE, code);
        values.put(DBManager.COLUMN_STN_FAV, fav);

        long entryId = db.insert(DBManager.TABLE_STATIONS, null, values);
        Log.i("DB Access", "Station created with id " + id);
        Cursor cursor = db.query(DBManager.TABLE_STATIONS, allColumns, DBManager.COLUMN_ID + " = " + entryId, null, null, null, null);

        cursor.moveToFirst();
        Station newStation = cursorToStation(cursor);
        cursor.close();
        return newStation;
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

    public List<Station> getAllStations(){

        // Ordering by name
        Cursor cursor = db.query(DBManager.TABLE_STATIONS, allColumns, null, null, null, null, DBManager.COLUMN_STN_NAME);
        List<Station> stns = new ArrayList<Station>();

        while(cursor.moveToNext()){
            stns.add(cursorToStation(cursor));
        }

        return stns;
    }

    public void clearAllStations(){
        db.delete(DBManager.TABLE_STATIONS, null, null);
    }

    private Station cursorToStation(Cursor cursor){
        int id = cursor.getInt(0);
        String name = cursor.getString(1);
        String alias = cursor.getString(2);
        double latitude = cursor.getDouble(3);
        double longitude = cursor.getDouble(4);
        String code = cursor.getString(5);
        boolean fav = (cursor.getInt(6) > 0);

        Station stn = new Station(id, name, alias, latitude, longitude, code, fav);

        Log.i("DB Access", "Returning station with id " + id);
        return stn;
    }
}
