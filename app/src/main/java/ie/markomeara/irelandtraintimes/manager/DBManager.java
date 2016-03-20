package ie.markomeara.irelandtraintimes.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mark on 19/10/2014.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final String TAG = DBManager.class.getSimpleName();

    private static final String DATABASE_NAME = "irelandtraintimes.db";

    public static final String TABLE_STATIONS = "stations";
    public static final String TABLE_TWEETS = "tweets";

    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_STN_NAME = "name";
    public static final String COLUMN_STN_ALIAS = "alias";
    public static final String COLUMN_STN_DISPLAY_NAME = "displayname";
    public static final String COLUMN_STN_LAT = "latitude";
    public static final String COLUMN_STN_LONG = "longitude";
    public static final String COLUMN_STN_CODE = "code";
    public static final String COLUMN_STN_FAV = "favourite";

    public static final String COLUMN_TWEET_TEXT = "text";
    public static final String COLUMN_TWEET_CREATE_DATE = "createdate";
    public static final String COLUMN_TWEET_RT_COUNT = "retweetcount";

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_STATIONS_CREATE = "create table "
            + TABLE_STATIONS + "(" + COLUMN_ID + " integer primary key, "
            + COLUMN_STN_NAME + " text unique not null, "
            + COLUMN_STN_ALIAS + " text, "
            + COLUMN_STN_DISPLAY_NAME + " text unique not null, "
            + COLUMN_STN_LAT + " real, "
            + COLUMN_STN_LONG + " real, "
            + COLUMN_STN_CODE + " text, "
            + COLUMN_STN_FAV + " integer DEFAULT 0);";

    private static final String TABLE_TWEETS_CREATE = "create table "
            + TABLE_TWEETS + "(" + COLUMN_ID + " integer primary key, "
            + COLUMN_TWEET_TEXT + " text not null, "
            + COLUMN_TWEET_CREATE_DATE + " integer not null, "
            + COLUMN_TWEET_RT_COUNT + " integer);";

    private static DBManager instance;

    public DBManager(Context context){
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBManager getDBManager(Context context){
        if(instance == null) {
            instance = new DBManager(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_STATIONS_CREATE);
        db.execSQL(TABLE_TWEETS_CREATE);
    }


    // TODO Valid upgrade process to copy data over
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWEETS);
        onCreate(db);
    }

    public void clearTable(SQLiteDatabase db, String table){
       db.delete(table, null, null);
    }
}
