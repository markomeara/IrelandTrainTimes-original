package ie.markomeara.irelandtraintimes.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.model.Tweet;

public class DatabaseOrmHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseOrmHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "irelandtraintimes.db";
    private static final int DATABASE_VERSION = 2;

    private static DatabaseOrmHelper instance;

    private Dao<Station, Integer> stationDao = null;
    private Dao<Tweet, Integer> tweetDao = null;

    private RuntimeExceptionDao<Station, Integer> stationRuntimeDao = null;
    private RuntimeExceptionDao<Tweet, Integer> tweetRuntimeDao = null;

    public DatabaseOrmHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseOrmHelper getDbHelper(Context context) {
        if (instance == null) {
            Log.d(TAG, "Creating instance of DatabaseOrmHelper");
            instance = OpenHelperManager.getHelper(context, DatabaseOrmHelper.class);
        }
        Log.d(TAG, "Returning instance of DatabaseOrmHelper");
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "onCreate Database helper");
            TableUtils.createTable(connectionSource, Station.class);
            TableUtils.createTable(connectionSource, Tweet.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(TAG, "onUpgrade");
            TableUtils.dropTable(connectionSource, Station.class, true);
            TableUtils.dropTable(connectionSource, Tweet.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Station, Integer> getStationDao() throws SQLException {
        if (stationDao == null) {
            stationDao = getDao(Station.class);
        }
        return stationDao;
    }

    public Dao<Tweet, Integer> getTweetDao() throws SQLException {
        if (tweetDao == null) {
            tweetDao = getDao(Tweet.class);
        }
        return tweetDao;
    }

    public RuntimeExceptionDao<Station, Integer> getStationRuntimeDao() {
        if (stationRuntimeDao == null) {
            stationRuntimeDao = getRuntimeExceptionDao(Station.class);
        }
        return stationRuntimeDao;
    }

    public RuntimeExceptionDao<Tweet, Integer> getTweetRuntimeDao() {
        if (tweetRuntimeDao == null) {
            tweetRuntimeDao = getRuntimeExceptionDao(Tweet.class);
        }
        return tweetRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        stationDao = null;
        tweetDao = null;
        stationRuntimeDao = null;
        tweetRuntimeDao = null;
    }
}
