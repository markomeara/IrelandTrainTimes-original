package ie.markomeara.irelandtraintimes.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import ie.markomeara.irelandtraintimes.R;

/**
 * Created by markomeara on 04/05/2015.
 */
public class TwitterActivity extends Activity {

    ListView twitter_LV;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        twitter_LV = (ListView) findViewById(R.id.twittertimeline_LV);
    }

    @Override
    public void onResume(){
        super.onResume();
        UserTimeline userTimeline = new UserTimeline.Builder().screenName("irishrail").build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(this, userTimeline);
        twitter_LV.setAdapter(adapter);
    }
}
