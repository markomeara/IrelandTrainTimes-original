package ie.markomeara.irelandtraintimes.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ie.markomeara.irelandtraintimes.Injector;
import ie.markomeara.irelandtraintimes.manager.DatabaseOrmHelper;
import ie.markomeara.irelandtraintimes.model.Tweet;
import ie.markomeara.irelandtraintimes.utils.SecretKeys;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TweetUpdaterTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = TweetUpdaterTask.class.getSimpleName();
    private static final long IRISH_RAIL_TWITTER_ID = 15115986;
    private static final long ONE_DAY = TimeUnit.DAYS.toMillis(1);

    @Inject
    protected DatabaseOrmHelper mDatabaseHelper;

    private Context mCurrentContext;

    public TweetUpdaterTask(Context c){
        Injector.inject(this);
        this.mCurrentContext = c;
    }

    @Override
    protected Void doInBackground(Void... params) {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(SecretKeys.TWITTER4J_API_KEY);
        cb.setOAuthConsumerSecret(SecretKeys.TWITTER4J_SECRET_KEY);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try {

            twitter.getOAuth2Token();
            List<twitter4j.Status> statuses;

            // Get last 200 statuses from Irish Rail
            // 200 is the count before all replies and retweets are removed
            Paging page = new Paging(1, 200);

            boolean includeReplies = false;
            boolean includeRTs = false;
            statuses = twitter.getUserTimeline(IRISH_RAIL_TWITTER_ID, page, includeReplies, includeRTs);

            Map<String, RateLimitStatus> limits = twitter.getRateLimitStatus();

            Set<String> keys = limits.keySet();

            refreshTweetsInDatabase(statuses);


        }
        catch(TwitterException ex){
            Log.w(TAG, ex);
        }


        // TODO Get tweets from Search if we've reached timeline limit

        // TODO Scrape Twitter feed if we've reached the request limit for both timeline and search

   /*     try {

            Connection.Response res = Jsoup.connect("https://mobile.twitter.com/Mark_O_Meara").execute();

            String resString = res.body();

            Document doc = Jsoup.connect("https://mobile.twitter.com/Mark_O_Meara").userAgent("\"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0\"").get();
            String docString = doc.toString();
            Elements elems = doc.select("#launchdata");
            if(elems.size() > 0) {
                String s = elems.toString();
                System.out.println(s);
            }
            else{
                String s = doc.toString();
                System.out.println(s);

          //      Log.w(TAG, "----null----");

            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }*/

        return null;
    }

    private void refreshTweetsInDatabase(List<twitter4j.Status> statuses){

        Date tweetCutoffTime = new Date();
        tweetCutoffTime.setTime(tweetCutoffTime.getTime() - ONE_DAY);

        List<Tweet> tweetsToStore = relevantTweets(statuses, tweetCutoffTime);

        try {
            deleteOldTweets(tweetCutoffTime, mDatabaseHelper);
            insertNewTweets(tweetsToStore, mDatabaseHelper);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void insertNewTweets(List<Tweet> tweetsToStore, DatabaseOrmHelper dbHelper) throws SQLException {
        Dao<Tweet, Integer> tweetDao = dbHelper.getTweetDao();
        for(Tweet tweet : tweetsToStore){
            tweetDao.createOrUpdate(tweet);
        }
    }

    private List<Tweet> relevantTweets(List<twitter4j.Status> statuses, Date tweetCutoffTime){
        List<Tweet> tweets = new ArrayList<>();

        for(twitter4j.Status status : statuses){

            Date createdDate = status.getCreatedAt();

            if(isRelevantStatus(status) && createdDate.after(tweetCutoffTime)) {
                Tweet tweet = new Tweet(status);
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    private boolean isRelevantStatus(twitter4j.Status status){

        boolean relevant = true;
        String statusText = status.getText();

        if(tweetIsAMention(statusText) || tweetContainsHashTag(statusText) || status.isRetweet()){
            relevant = false;
        }

        return relevant;
    }

    private void deleteOldTweets(Date tweetCutoffTime, DatabaseOrmHelper dbHelper) throws SQLException {
        Dao<Tweet, Integer> tweetDao = dbHelper.getTweetDao();
        DeleteBuilder deleteBuilder = tweetDao.deleteBuilder();
        deleteBuilder.where().lt("createdAt", tweetCutoffTime);
        deleteBuilder.delete();
    }

    private boolean tweetIsAMention(String statusText){
        return statusText.contains("@");
    }

    private boolean tweetContainsHashTag(String tweetText){
        return tweetText.contains("#");
    }

    @Override
    protected void onPostExecute(Void param) {
        Log.i(TAG, "Tweets have been updated");
        Toast t = Toast.makeText(mCurrentContext, "Tweets updated", Toast.LENGTH_SHORT);
        t.show();

    }

}
