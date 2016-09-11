package ie.markomeara.irelandtraintimes.network;

import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TwitterService {

    private static final String TAG = TwitterService.class.getSimpleName();

    private static final long CACHE_TIMEOUT = 300000; // Five minutes
    private static final SimpleDateFormat TWITTER_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
    private static final int TWEET_MAX_HOURS_AGO = 12;

    private List<Tweet> mCachedTweets;
    private long mCacheUpdateTimestamp;

    private UserTimeline mUserTimeline;
    private EventBus mEventBus;

    static {
        TWITTER_DATE_FORMAT.setLenient(true);
    }

    public TwitterService(EventBus eventBus, UserTimeline userTimeline) {
        mEventBus = eventBus;
        mUserTimeline = userTimeline;
    }

    public void fetchFilteredTweets() {

        if(isCacheFresh()) {
            mEventBus.post(mCachedTweets);
            return;
        }

        mUserTimeline.next(null, new Callback<TimelineResult<Tweet>>() {
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
            if(isRecent(tweet) && !hasMention(tweet) && !hasHashtag(tweet) && !hasMedia(tweet)) {
                tweets.add(tweet);
            }
        }
        return tweets;
    }

    private boolean isRecent(Tweet tweet) {
        boolean recent = false;
        try {
            final Date date = TWITTER_DATE_FORMAT.parse(tweet.createdAt);
            long hoursAgo = millisecondsToHours(System.currentTimeMillis() - date.getTime());
            recent = hoursAgo < TWEET_MAX_HOURS_AGO;
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return recent;
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

    private boolean hasMedia(Tweet tweet) {
        return tweet.extendedEtities != null
                && tweet.extendedEtities.media != null
                && !tweet.extendedEtities.media.isEmpty();
    }

    private long millisecondsToHours(long milliseconds) {
        return (milliseconds / 1000 / 60 / 60);
    }

    public static class TwitterApiFailureEvent { }
}
