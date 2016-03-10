package ie.markomeara.irelandtraintimes.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ie.markomeara.irelandtraintimes.managers.DBManager;
import ie.markomeara.irelandtraintimes.model.Tweet;
import twitter4j.Status;

/**
 * Created by Mark on 24/10/2014.
 */
public class TweetsDataSource {

    private static final String TAG = TweetsDataSource.class.getSimpleName();

    private SQLiteDatabase db;
    private DBManager dbManager;
    private String[] allColumns = { DBManager.COLUMN_ID, DBManager.COLUMN_TWEET_TEXT,
            DBManager.COLUMN_TWEET_CREATE_DATE, DBManager.COLUMN_TWEET_RT_COUNT};

    private long oneDay = TimeUnit.DAYS.toMillis(1);

    public TweetsDataSource(Context context){
        dbManager = DBManager.getDBManager(context);
        db = dbManager.getWritableDatabase();
    }

    // TODO Remove tweets more than 24 hours old

    public List<Tweet> createRelevantTweets(List<Status> statuses){

        List<Tweet> tweets = new ArrayList<Tweet>();

        Date tweetCutoffTime = new Date();
        tweetCutoffTime.setTime(tweetCutoffTime.getTime() - oneDay);


        for(int i = 0; i < statuses.size(); i++){

            Status status = statuses.get(i);
            Date createdDate = status.getCreatedAt();

            if(isRelevantStatus(status) && createdDate.after(tweetCutoffTime)) {
                long id = status.getId();
                String text = status.getText();
                Date createDate = status.getCreatedAt();
                int rtCount = status.getRetweetCount();

                Tweet createdTweet = insertTweet(id, text, createDate, rtCount);
                if(createdTweet != null) {
                    tweets.add(createdTweet);
                }
            }
        }

        purgeOldTweets(tweetCutoffTime);

        Log.d(TAG, "Returning created tweets");
        return tweets;
    }

    public List<Tweet> getAllTweets(){

        // Ordering by tweet timestamp
        List<Tweet> tweets = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DBManager.TABLE_TWEETS, allColumns, null, null, null, null, DBManager.COLUMN_TWEET_CREATE_DATE);
            tweets = new ArrayList<>();

            while (cursor.moveToNext()) {
                tweets.add(cursorToTweet(cursor));
            }
        } catch(SQLiteConstraintException ex){
            Log.e(TAG, ex.getMessage(), ex);
        } finally {
            if(cursor != null){
                cursor.close();
            }
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

    private Tweet insertTweet(long id, String text, Date createDate, int rtCount){

        Tweet newTweet = null;
        ContentValues values = new ContentValues();
        values.put(DBManager.COLUMN_ID, id);
        values.put(DBManager.COLUMN_TWEET_TEXT, text);
        values.put(DBManager.COLUMN_TWEET_CREATE_DATE, createDate.getTime());
        values.put(DBManager.COLUMN_TWEET_RT_COUNT, rtCount);

        Cursor cursor = null;
        try {
            db.insertOrThrow(DBManager.TABLE_TWEETS, null, values);
            Log.i(TAG, "Tweet created with id " + id);

            cursor = db.query(DBManager.TABLE_TWEETS, allColumns, DBManager.COLUMN_ID + " = " + id, null, null, null, null);

            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                newTweet = cursorToTweet(cursor);
            }
        } catch(SQLiteConstraintException ex){
            Log.i(TAG, ex.getMessage(), ex);
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return newTweet;
    }

    private Tweet cursorToTweet(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndex(DBManager.COLUMN_ID));
        String text = cursor.getString(cursor.getColumnIndex(DBManager.COLUMN_TWEET_TEXT));
        long createdAt = cursor.getLong(cursor.getColumnIndex(DBManager.COLUMN_TWEET_CREATE_DATE));
        int rtCount = cursor.getInt(cursor.getColumnIndex(DBManager.COLUMN_TWEET_RT_COUNT));

        Date createdAtDate = new Date(createdAt);
        Tweet tweet = new Tweet(id, text, createdAtDate, rtCount);

        Log.d(TAG, "Returning tweet with id " + id);
        return tweet;
    }

    private void purgeOldTweets(Date cutoffTime){
        db.delete(DBManager.TABLE_TWEETS, DBManager.COLUMN_TWEET_CREATE_DATE + "<" + cutoffTime.getTime(), null);
    }

}
