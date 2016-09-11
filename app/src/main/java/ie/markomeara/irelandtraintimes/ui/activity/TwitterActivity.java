package ie.markomeara.irelandtraintimes.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.R;

public class TwitterActivity extends AppCompatActivity {

    @Bind(R.id.twittertimeline_LV)
    protected ListView mTwitter_LV;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        UserTimeline userTimeline = new UserTimeline.Builder().screenName("irishrail").build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(this, userTimeline);
        mTwitter_LV.setAdapter(adapter);
    }
}
