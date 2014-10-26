package ie.markomeara.irelandtraintimes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.twitter.Tweet;
import twitter4j.Status;

/**
 * Created by Mark on 24/10/2014.
 */
public class TweetsDataSource {

    private SQLiteDatabase db;
    private DBManager dbManager;
    private String[] allColumns = { DBManager.COLUMN_ID, DBManager.COLUMN_TWEET_TEXT,
            DBManager.COLUMN_TWEET_CREATE_DATE, DBManager.COLUMN_TWEET_RT_COUNT};

    public TweetsDataSource(Context context){
        dbManager = new DBManager(context);
    }

    public void open() throws SQLException {
        db = dbManager.getWritableDatabase();
    }

    public void close() { dbManager.close(); }

    public List<Tweet> createRelevantTweets(List<Status> statuses){

        List<Tweet> tweets = new ArrayList<Tweet>();

        for(int i = 0; i < statuses.size(); i++){

            Status status = statuses.get(i);
            if(isRelevantStatus(status)) {
                long id = status.getId();
                String text = status.getText();
                String createDate = status.getCreatedAt().toString();
                int rtCount = status.getRetweetCount();

                Tweet createdTweet = insertTweet(id, text, createDate, rtCount);
                tweets.add(createdTweet);
            }
        }

        Log.d("createTweets", "Returning created tweets");
        return tweets;
    }

    public List<Tweet> getAllTweets(){

        // Ordering by tweet timestamp
        Cursor cursor = db.query(DBManager.TABLE_TWEETS, allColumns, null, null, null, null, DBManager.COLUMN_TWEET_CREATE_DATE);
        List<Tweet> tweets = new ArrayList<Tweet>();

        while(cursor.moveToNext()){
            tweets.add(cursorToTweet(cursor));
        }

        return tweets;
    }

    public void clearAllTweets(){
        db.delete(DBManager.TABLE_TWEETS, null, null);
    }

    /**
     * Any tweets which are replies or RTs, or have hashtags or mention other users
     * are assumed to not be transport updates and therefore not relevant
     * NOTE: All replies are excluded already by API call
     *
     * @param status
     * @return
     */
    private boolean isRelevantStatus(Status status){

        boolean relevant = true;
        String statusText = status.getText();

        // If it mentions a user
        if(statusText.contains("@")){
            relevant = false;
        }
        // If it contains a hashtag
        else if(statusText.contains("#")){
            relevant = false;
        }
        else if(status.isRetweet()){
            relevant = false;
        }

        return relevant;
    }

    private Tweet insertTweet(long id, String text, String createDate, int rtCount){

        ContentValues values = new ContentValues();
        values.put(DBManager.COLUMN_ID, id);
        values.put(DBManager.COLUMN_TWEET_TEXT, text);
        values.put(DBManager.COLUMN_TWEET_CREATE_DATE, createDate);
        values.put(DBManager.COLUMN_TWEET_RT_COUNT, rtCount);

        // TODO Check that tweet isn't there already
        db.insert(DBManager.TABLE_TWEETS, null, values);
        Log.i("DB Access", "Tweet created with id " + id);

        Cursor cursor = db.query(DBManager.TABLE_TWEETS, allColumns, DBManager.COLUMN_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        Tweet newTweet = null;
        if(!cursor.isAfterLast()) {
            newTweet = cursorToTweet(cursor);
            cursor.close();
        }
        return newTweet;
    }

    private Tweet cursorToTweet(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndex(DBManager.COLUMN_ID));
        String text = cursor.getString(cursor.getColumnIndex(DBManager.COLUMN_TWEET_TEXT));
        String createdAt = cursor.getString(cursor.getColumnIndex(DBManager.COLUMN_TWEET_CREATE_DATE));
        int rtCount = cursor.getInt(cursor.getColumnIndex(DBManager.COLUMN_TWEET_RT_COUNT));

        Tweet tweet = new Tweet(id, text, createdAt, rtCount);

        Log.i("DB Access", "Returning tweet with id " + id);
        return tweet;
    }

}
