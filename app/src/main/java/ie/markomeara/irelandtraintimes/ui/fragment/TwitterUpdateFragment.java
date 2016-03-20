package ie.markomeara.irelandtraintimes.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.network.TweetsDataSource;
import ie.markomeara.irelandtraintimes.network.TwitterTask;
import ie.markomeara.irelandtraintimes.model.Tweet;

public class TwitterUpdateFragment extends Fragment {

    private static final String TAG = TwitterUpdateFragment.class.getSimpleName();
    private List<Tweet> tweets;
    Timer tweetSwitchTimer;
    private int currentTweet;
    private TweetFragmentListener mListener;

    @Bind(R.id.tweetdisplayedTV)
    TextView tweetTextView;

    public TwitterUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_twitter_update, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        new TwitterTask(getActivity()).execute();

        TweetsDataSource tds = new TweetsDataSource(getActivity());
        tweets = tds.getAllTweets();

        currentTweet = 0;
        if(!tweets.isEmpty()) {
            tweetTextView.setText(tweets.get(currentTweet).getText());
        }

        scheduleTweetSwitching();

        // OnClick
        tweetTextView.setOnClickListener(new View.OnClickListener() {
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
        tweetSwitchTimer.cancel();
    }

    public interface TweetFragmentListener {
        // TODO: Update argument type and name
        void onTweetFragmentClicked();
    }

    private void scheduleTweetSwitching(){
        tweetSwitchTimer = new Timer("tweet_switcher");
        tweetSwitchTimer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        getActivity().runOnUiThread( new Runnable(){

                            @Override
                            public void run() {
                                if(currentTweet < (tweets.size() - 1)){
                                    currentTweet++;
                                }
                                else{
                                    // Refresh tweets when we're about to go back to start
                                    refreshTweetList();
                                    currentTweet = 0;
                                }

                                if(!tweets.isEmpty()) {
                                    tweetTextView.setText(tweets.get(currentTweet).getText());
                                }
                                else{
                                    tweetTextView.setText(getString(R.string.no_tweets));
                                }
                            }
                        });
                        //switch your text using either runOnUiThread() or sending alarm and receiving it in your gui thread
                    }
                }, 5000, 5000);
    }

    private void refreshTweetList(){
        TweetsDataSource tds = new TweetsDataSource(getActivity());
        tweets = tds.getAllTweets();
    }

}
