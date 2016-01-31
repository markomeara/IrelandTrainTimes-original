package ie.markomeara.irelandtraintimes.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.managers.DBManager;
import ie.markomeara.irelandtraintimes.model.Station;

/**
 * Created by Mark on 19/10/2014.
 */
public class StationsDataSource {

    private static final String TAG = StationsDataSource.class.getSimpleName();

    private SQLiteDatabase db;
    private DBManager dbManager;
    private String[] allColumns = { DBManager.COLUMN_ID, DBManager.COLUMN_STN_NAME,
            DBManager.COLUMN_STN_ALIAS, DBManager.COLUMN_STN_DISPLAY_NAME, DBManager.COLUMN_STN_LAT, DBManager.COLUMN_STN_LONG,
            DBManager.COLUMN_STN_CODE, DBManager.COLUMN_STN_FAV};

    public StationsDataSource(Context context){
        dbManager = DBManager.getDBManager(context);
        db = dbManager.getWritableDatabase();
    }

    public boolean updateStoredStations(List<Station> stationList){

        boolean overallSuccess = true;

        for(Station station : stationList){

            boolean stationSuccess = storeStation(station);
            if(!stationSuccess){
                overallSuccess = false;
            }

        }

        return overallSuccess;
    }

    /**
     * Stores station in SQLite DB
     *
     * @param stationToStore
     * @return false if station isn't entered in DB. This can happen if station with same name already exists.
     */
    public boolean storeStation(Station stationToStore) {

        /*
         *
         * Stores station but keeps its 'fav' boolean field if the station is already in the DB.
         * Still overwrites other station fields as the values may have changed (e.g. new station name)
         *
         * Example query:
         *
         * INSERT OR REPLACE INTO stations (_id,name,alias,latitude,longitude,code,favourite)
         * VALUES (228,'Belfast Central','',54.6123,-5.91744,'BFSTC', (SELECT favourite FROM stations where _id = 228));
         */

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBManager.COLUMN_ID, stationToStore.getId());
        contentValues.put(DBManager.COLUMN_STN_NAME, stationToStore.getName());
        contentValues.put(DBManager.COLUMN_STN_ALIAS, stationToStore.getAlias());
        contentValues.put(DBManager.COLUMN_STN_DISPLAY_NAME, stationToStore.getDisplayName());
        contentValues.put(DBManager.COLUMN_STN_LAT, stationToStore.getLatitude());
        contentValues.put(DBManager.COLUMN_STN_LONG, stationToStore.getLongitude());
        contentValues.put(DBManager.COLUMN_STN_CODE, stationToStore.getCode());

        // TODO Retrieve existing fav value and save it in overwrite
        contentValues.put(DBManager.COLUMN_STN_FAV, false);

        boolean success = false;

        try {
            db.insertWithOnConflict(DBManager.TABLE_STATIONS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            Cursor cursor = db.query(DBManager.TABLE_STATIONS, allColumns, DBManager.COLUMN_ID + " = " + stationToStore.getId(), null, null, null, null);

            if (cursor.moveToFirst()) {
                Station updatedStation = cursorToStation(cursor);
                if(updatedStation != null){
                    success = true;
                }
                else{
                    Log.e(TAG, "Error inserting station (id: " + stationToStore.getId() + ") into DB");
                }
            }
            cursor.close();
        } catch(SQLiteConstraintException ex){
            Log.e(TAG, ex.getMessage(), ex);
        }
        return success;
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

        // Order by display name
        Cursor cursor = db.query(DBManager.TABLE_STATIONS, allColumns, null, null, null, null, DBManager.COLUMN_STN_DISPLAY_NAME);
        List<Station> stns = new ArrayList<>();

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
        db.beginTransaction();
        db.delete(DBManager.TABLE_STATIONS, null, null);
        db.setTransactionSuccessful();
        db.endTransaction();
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

        Log.d(TAG, "Returning station with id " + id + ", name: " + name + ", alias: " + alias + ", displayname: " + stn.getDisplayName());
        return stn;
    }
}
