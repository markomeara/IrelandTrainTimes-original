package ie.markomeara.irelandtraintimes;

import java.util.Date;

/**
 * Created by Mark on 26/10/2014.
 */
public class Train implements Comparable<Train> {

    private static final String TAG = Train.class.getSimpleName();

    private String trainCode;
    private String origin;
    private String destination;
    private String latestInfo;
    private String direction;
    private String trainType;
    private int dueIn;
    private int delayMins;
    private Date updateTime;

    private String status;

    private String schArrival;
    private String expArrival;

    // These will be 00:00 if train terminates at this station
    private String schDepart;
    private String expDepart;

    private String destArrivalTime;
    private String originTime;
    private String trainDate;

    // No need to save train to DB as it doesn't need to persist, since it will go out of date pretty quickly

    public Train(){ }

    public int compareTo(Train other){
        if(!this.direction.equals(other.getDirection())){
            return this.direction.compareTo(other.getDirection());
        }
        else if(this.dueIn != other.getDueIn()){
            return this.dueIn > other.getDueIn() ? 1 : 0;
        }
        else{
            return this.destination.compareTo(other.getDestination());
        }
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

    public int getDelayMins() {
        return delayMins;
    }

    public void setDelayMins(int delayMins) {
        this.delayMins = delayMins;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchArrival() {
        return schArrival;
    }

    public void setSchArrival(String schArrival) {
        this.schArrival = schArrival;
    }

    public String getExpArrival() {
        return expArrival;
    }

    public void setExpArrival(String expArrival) {
        this.expArrival = expArrival;
    }

    public String getSchDepart() {
        return schDepart;
    }

    public void setSchDepart(String schDepart) {
        this.schDepart = schDepart;
    }

    public String getExpDepart() {
        return expDepart;
    }

    public void setExpDepart(String expDepart) {
        this.expDepart = expDepart;
    }

    public String getDestArrivalTime() {
        return destArrivalTime;
    }

    public void setDestArrivalTime(String destArrivalTime) {
        this.destArrivalTime = destArrivalTime;
    }

    public String getOriginTime() {
        return originTime;
    }

    public void setOriginTime(String originTime) {
        this.originTime = originTime;
    }

    public String getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(String trainDate) {
        this.trainDate = trainDate;
    }
}
