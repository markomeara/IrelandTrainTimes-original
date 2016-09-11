package ie.markomeara.irelandtraintimes.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.Injector;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.network.TwitterService;

public class TwitterUpdateFragment extends Fragment {

    private static final String TAG = TwitterUpdateFragment.class.getSimpleName();

    private List<Tweet> mDisplayTweets;
    private List<Tweet> mMostRecentlyLoadedTweets;
    private Timer mTweetSwitchTimer;
    private int mCurrentTweetIndx;

    @Bind(R.id.tweetView)
    CompactTweetView mTweetView;

    @Inject
    TwitterService mTwitterService;

    @Inject
    EventBus mEventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.get().inject(this);
        mDisplayTweets = Lists.newArrayList();
        mMostRecentlyLoadedTweets = Lists.newArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitter_update, container, false);
        ButterKnife.bind(this, view);
        mEventBus.register(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mTwitterService.fetchFilteredTweets();
        scheduleTweetSwitching();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mTweetSwitchTimer.cancel();
    }

    private void scheduleTweetSwitching(){
        mCurrentTweetIndx = 0;
        mTweetSwitchTimer = new Timer("tweet_switcher");
        mTweetSwitchTimer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        getActivity().runOnUiThread( new Runnable(){

                            @Override
                            public void run() {
                                if(mCurrentTweetIndx < (mDisplayTweets.size() - 1)){
                                    mCurrentTweetIndx++;
                                } else {
                                    mDisplayTweets = mMostRecentlyLoadedTweets;
                                    mTwitterService.fetchFilteredTweets();
                                    mCurrentTweetIndx = 0;
                                }

                                if(mCurrentTweetIndx <= (mDisplayTweets.size() - 1)) {
                                    mTweetView.setVisibility(View.VISIBLE);
                                    mTweetView.setTweet(mDisplayTweets.get(mCurrentTweetIndx));
                                } else {
                                    mTweetView.setVisibility(View.GONE);
                                }
                            }
                        });
                        //switch your text using either runOnUiThread() or sending alarm and receiving it in your gui thread
                    }
                }, 5000, 5000);
    }

    @Subscribe
    private void onTweetsReceived(List<Tweet> tweets) {
        mMostRecentlyLoadedTweets = tweets;
    }

}
