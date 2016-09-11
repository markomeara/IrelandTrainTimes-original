package ie.markomeara.irelandtraintimes.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ie.markomeara.irelandtraintimes.model.Tweet;

public class DatabaseOrmHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseOrmHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "irelandtraintimes.db";
    private static final int DATABASE_VERSION = 2;

    private Dao<Tweet, Integer> mTweetDao = null;

    private RuntimeExceptionDao<Tweet, Integer> mTweetRuntimeDao = null;

    public DatabaseOrmHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "onCreate Database helper");
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
            TableUtils.dropTable(connectionSource, Tweet.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Tweet, Integer> getTweetDao() throws SQLException {
        if (mTweetDao == null) {
            mTweetDao = getDao(Tweet.class);
        }
        return mTweetDao;
    }

    public RuntimeExceptionDao<Tweet, Integer> getTweetRuntimeDao() {
        if (mTweetRuntimeDao == null) {
            mTweetRuntimeDao = getRuntimeExceptionDao(Tweet.class);
        }
        return mTweetRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        mTweetDao = null;
        mTweetRuntimeDao = null;
    }
}
