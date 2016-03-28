package ie.markomeara.irelandtraintimes.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by Mark on 24/10/2014.
 */
public class Tweet {

    private static final String TAG = Tweet.class.getSimpleName();

    // TODO Use something bigger than int for id in DB table
    @DatabaseField
    private long id;

    @DatabaseField
    private String text;

    @DatabaseField
    private Date createdAt;

    @DatabaseField
    private int retweetCount;

    public Tweet(){

    }

    public Tweet(long id, String text, Date createdAt, int retweetCount){
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.retweetCount = retweetCount;
    }

    public long getId(){
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
