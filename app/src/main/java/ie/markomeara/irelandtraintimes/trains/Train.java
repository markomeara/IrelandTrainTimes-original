package ie.markomeara.irelandtraintimes.trains;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import ie.markomeara.irelandtraintimes.ListHelpers.adapters.TrainsDueListAdapter;
import ie.markomeara.irelandtraintimes.ListHelpers.interfaces.TrainListItem;

/**
 * Created by Mark on 26/10/2014.
 */
public class Train implements Comparable<Train>, TrainListItem, Parcelable {

    private static final String TAG = Train.class.getSimpleName();

    public static final int MAJORDELAY_MINS = 5;

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

    private Train(Parcel in) {
        this.trainCode = in.readString();
        this.origin = in.readString();
        this.destination = in.readString();
        this.latestInfo = in.readString();
        this.direction = in.readString();
        this.trainType = in.readString();
        this.dueIn = in.readInt();
        this.delayMins = in.readInt();
        long tmpUpdateTime = in.readLong();
        this.updateTime = tmpUpdateTime == -1 ? null : new Date(tmpUpdateTime);
        this.status = in.readString();
        this.schArrival = in.readString();
        this.expArrival = in.readString();
        this.schDepart = in.readString();
        this.expDepart = in.readString();
        this.destArrivalTime = in.readString();
        this.originTime = in.readString();
        this.trainDate = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trainCode);
        dest.writeString(this.origin);
        dest.writeString(this.destination);
        dest.writeString(this.latestInfo);
        dest.writeString(this.direction);
        dest.writeString(this.trainType);
        dest.writeInt(this.dueIn);
        dest.writeInt(this.delayMins);
        dest.writeLong(updateTime != null ? updateTime.getTime() : -1);
        dest.writeString(this.status);
        dest.writeString(this.schArrival);
        dest.writeString(this.expArrival);
        dest.writeString(this.schDepart);
        dest.writeString(this.expDepart);
        dest.writeString(this.destArrivalTime);
        dest.writeString(this.originTime);
        dest.writeString(this.trainDate);
    }

    public static final Parcelable.Creator<Train> CREATOR = new Parcelable.Creator<Train>() {
        public Train createFromParcel(Parcel source) {
            return new Train(source);
        }

        public Train[] newArray(int size) {
            return new Train[size];
        }
    };

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

    public int getListViewType(){
        return TrainsDueListAdapter.RowType.TRAIN.ordinal();
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

    @Override
    public int describeContents() {
        return 0;
    }
}
