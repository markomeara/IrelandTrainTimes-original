package ie.markomeara.irelandtraintimes.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.twitter.sdk.android.core.models.Tweet;

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

    private List<Tweet> mTweets;
    private Timer mTweetSwitchTimer;
    private int mCurrentTweet;
    private TweetFragmentListener mListener;

    @Bind(R.id.tweetdisplayedTV)
    protected TextView mTweetTextView;

    @Inject
    TwitterService mTwitterService;

    @Inject
    EventBus mEventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.get().inject(this);
        mTweets = Lists.newArrayList();
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

        mCurrentTweet = 0;
        if(!mTweets.isEmpty()) {
            mTweetTextView.setText(mTweets.get(mCurrentTweet).text);
        }

        scheduleTweetSwitching();

        // OnClick
        mTweetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTweetFragmentClicked();

            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (TweetFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mTweetSwitchTimer.cancel();
    }

    public interface TweetFragmentListener {
        // TODO: Update argument type and name
        void onTweetFragmentClicked();
    }

    private void scheduleTweetSwitching(){
        mTweetSwitchTimer = new Timer("tweet_switcher");
        mTweetSwitchTimer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        getActivity().runOnUiThread( new Runnable(){

                            @Override
                            public void run() {
                                if(mCurrentTweet < (mTweets.size() - 1)){
                                    mCurrentTweet++;
                                }
                                else{
                                    // Refresh tweets when we're about to go back to start
                                    mTwitterService.fetchFilteredTweets();
                                    mCurrentTweet = 0;
                                }

                                if(!mTweets.isEmpty()) {
                                    mTweetTextView.setText(mTweets.get(mCurrentTweet).text);
                                }
                                else{
                                    mTweetTextView.setText(getString(R.string.no_tweets));
                                }
                            }
                        });
                        //switch your text using either runOnUiThread() or sending alarm and receiving it in your gui thread
                    }
                }, 5000, 5000);
    }

    @Subscribe
    private void onTweetsReceived(List<Tweet> tweets) {
        mTweets = tweets;
    }

}
