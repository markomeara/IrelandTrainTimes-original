package ie.markomeara.irelandtraintimes.network;

import com.google.common.eventbus.EventBus;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.LinkedList;
import java.util.List;

public class TwitterService {

    private static final long IRISH_RAIL_TWITTER_ID = 15115986;
    private static final long CACHE_TIMEOUT = 300000; // Five minutes

    private List<Tweet> mCachedTweets;
    private long mCacheUpdateTimestamp;

    private UserTimeline userTimelineConfig;

    private EventBus mEventBus;

    public TwitterService(EventBus eventBus) {
        mEventBus = eventBus;
        userTimelineConfig = new UserTimeline.Builder()
                .includeReplies(false)
                .includeRetweets(false)
                .userId(IRISH_RAIL_TWITTER_ID)
                .build();
    }
    public void fetchFilteredTweets() {

        if(isCacheFresh()) {
            mEventBus.post(mCachedTweets);
            return;
        }

        userTimelineConfig.next(null, new Callback<TimelineResult<Tweet>>() {
            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                List<Tweet> unfilteredTweets = result.data.items;
                mCachedTweets = filter(unfilteredTweets);
                mCacheUpdateTimestamp = System.currentTimeMillis();
                mEventBus.post(mCachedTweets);
            }

            @Override
            public void failure(TwitterException exception) {
                mEventBus.post(new TwitterApiFailureEvent());
            }
        });
    }

    private List<Tweet> filter(List<Tweet> unfilteredTweets) {
        List<Tweet> tweets = new LinkedList<>();
        for(Tweet tweet : unfilteredTweets) {
            if(!hasMention(tweet) && !hasHashtag(tweet)) {
                tweets.add(tweet);
            }
        }
        return tweets;
    }

    private boolean isCacheFresh() {
        return mCachedTweets != null
                && System.currentTimeMillis() - mCacheUpdateTimestamp < CACHE_TIMEOUT;
    }

    private boolean hasMention(Tweet tweet) {
        return tweet.extendedEtities != null
                    && tweet.extendedEtities.userMentions != null
                    && !tweet.extendedEtities.userMentions.isEmpty();
    }

    private boolean hasHashtag(Tweet tweet) {
        return tweet.extendedEtities != null
                    && tweet.extendedEtities.hashtags != null
                    && !tweet.extendedEtities.hashtags.isEmpty();
    }

    public static class TwitterApiFailureEvent { }
}
