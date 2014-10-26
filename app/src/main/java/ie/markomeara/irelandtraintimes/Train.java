package ie.markomeara.irelandtraintimes;

import java.util.Date;

/**
 * Created by Mark on 26/10/2014.
 */
public class Train {

    private String trainCode;
    private String origin;
    private String destination;
    private String latestInfo;
    private String direction;
    private String trainType;
    private int dueIn;
    private int late;
    private Date updateTime;

    // Other options from API include: origintime, destinationtime, exparrival, expdepart, scharrival,
    // schdepart, desttime

    // No need to save train to DB as it doesn't need to persist, since it will go out of date pretty quickly

    public Train(){ }

    public Train(String code, String origin, String dest, String latestInfo, String direction,
                 String trainType, int dueIn, int late, Date updateTime){

        this.trainCode = code;
        this.origin = origin;
        this.destination = dest;
        this.latestInfo = latestInfo;
        this.direction = direction;
        this.trainType = trainType;
        this.dueIn = dueIn;
        this.updateTime = updateTime;

    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLatestInfo() {
        return latestInfo;
    }

    public void setLatestInfo(String latestInfo) {
        this.latestInfo = latestInfo;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public int getDueIn() {
        return dueIn;
    }

    public void setDueIn(int dueIn) {
        this.dueIn = dueIn;
    }

    public int getLate() {
        return late;
    }

    public void setLate(int late) {
        this.late = late;
    }

}
