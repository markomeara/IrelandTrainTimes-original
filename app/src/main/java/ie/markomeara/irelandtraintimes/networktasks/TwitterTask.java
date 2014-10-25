package ie.markomeara.irelandtraintimes.networktasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKey;

import ie.markomeara.irelandtraintimes.db.TweetsDataSource;
import ie.markomeara.irelandtraintimes.twitter.Tweet;
import ie.markomeara.irelandtraintimes.utils.SecretKeys;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Mark on 21/10/2014.
 */
public class TwitterTask extends AsyncTask {

    private Context currentContext;

    public TwitterTask(Context c){
        this.currentContext = c;
    }

    @Override
    protected Object doInBackground(Object[] params) {


        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(SecretKeys.API_KEY);
        cb.setOAuthConsumerSecret(SecretKeys.SECRET_KEY);
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
            statuses = twitter.getUserTimeline(15115986, page, includeReplies, includeRTs);

            Map<String, RateLimitStatus> limits = twitter.getRateLimitStatus();

            Set<String> keys = limits.keySet();
            Iterator<String> iter = keys.iterator();

            TweetsDataSource tds = new TweetsDataSource(currentContext);
            try {
                tds.open();
                tds.createRelevantTweets(statuses);
                tds.close();

                tds.open();
                List<Tweet> ts = tds.getAllTweets();
                for(int i = 0; i < ts.size(); i++){
                   Log.w("TWEET FROM DB", ts.get(i).getText());
                }
                tds.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (TwitterException e) {
            e.printStackTrace();
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

          //      Log.w("NULL", "----null----");

            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }*/



        return null;
    }
}
