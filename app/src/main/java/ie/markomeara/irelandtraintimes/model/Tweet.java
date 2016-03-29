package ie.markomeara.irelandtraintimes.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

public class Tweet implements Comparable {

    private static final String TAG = Tweet.class.getSimpleName();

    // TODO Use something bigger than int for id in DB table
    @DatabaseField(id = true, unique = true)
    private long id;

    @DatabaseField
    private String text;

    @DatabaseField(columnName = "createdAt")
    private Date createdAt;

    @DatabaseField
    private int retweetCount;

    public Tweet(){

    }

    public Tweet(twitter4j.Status status){
        this.id = status.getId();
        this.text = status.getText();
        this.createdAt = status.getCreatedAt();
        this.retweetCount = status.getRetweetCount();
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

    @Override
    public int compareTo(Object another) {
        int result = 0;
        if(another instanceof Tweet){
            Tweet otherTweet = (Tweet) another;
            // TODO Ensure this orders it correctly
            result = this.getCreatedAt().compareTo(otherTweet.getCreatedAt());
        }
        return result;
    }
}
