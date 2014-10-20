package ie.markomeara.irelandtraintimes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mark on 19/10/2014.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "irelandtraintimes.db";

    public static final String TABLE_STATIONS = "stations";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STN_NAME = "name";
    public static final String COLUMN_STN_ALIAS = "alias";
    public static final String COLUMN_STN_LAT = "latitude";
    public static final String COLUMN_STN_LONG = "longitude";
    public static final String COLUMN_STN_CODE = "code";
    public static final String COLUMN_STN_FAV = "favourite";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_STATIONS + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_STN_NAME + " text not null, "
            + COLUMN_STN_ALIAS + " text, "
            + COLUMN_STN_LAT + " real, "
            + COLUMN_STN_LONG + " real, "
            + COLUMN_STN_CODE + " text, "
            + COLUMN_STN_FAV + " integer);";

    public DBManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBManager.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        onCreate(db);
    }

    public void clearTable(SQLiteDatabase db, String table){
       db.delete(table, null, null);
    }
}
