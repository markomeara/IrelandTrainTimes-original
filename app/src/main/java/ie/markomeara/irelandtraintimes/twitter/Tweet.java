package ie.markomeara.irelandtraintimes.twitter;

import java.util.Date;

/**
 * Created by Mark on 24/10/2014.
 */
public class Tweet {

    private static final String TAG = Tweet.class.getSimpleName();

    private int id;
    private String text;
    private Date createdAt;
    private int retweetCount;

    public Tweet(int id, String text, Date createdAt, int retweetCount){
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.retweetCount = retweetCount;
    }

    public int getId(){
        return id;
    }

    public String getText(){
        return text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

}
