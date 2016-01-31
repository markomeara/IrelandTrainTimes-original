package ie.markomeara.irelandtraintimes.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import ie.markomeara.irelandtraintimes.views.adapters.TrainsDueRecyclerViewAdapter;

/**
 * Created by Mark on 26/10/2014.
 *
 * This is train info as seen from a particular station (I.E. the 'dueIn' var is relevant to only one station)
 * The 'stationCodeViewedFrom' variable shows which station this train info has been viewed from
 */
@Root(name = "objStationData", strict = false)
public class Train implements Comparable<Train>, TrainListItem, Parcelable {

    private static final String TAG = Train.class.getSimpleName();

    public static final int MAJORDELAY_MINS = 5;

    @Element(name = "Traincode")
    private String trainCode;
    @Element(name = "Origin")
    private String origin;
    @Element(name = "Destination")
    private String destination;
    @Element(name = "Lastlocation", required = false)
    private String latestInfo;
    @Element(name = "Direction")
    private String direction;
    @Element(name = "Traintype")
    private String trainType;
    @Element(name = "Stationfullname")
    private Station stationViewedFrom;
    @Element(name = "Duein")
    private int dueIn;
    @Element(name = "Late", required = false)
    private int delayMins;
    @Element(name = "Servertime")
    private String updateTimeString;
    @Element(name = "Status")
    private String status;
    @Element(name = "Scharrival")
    private String schArrival;
    @Element(name = "Exparrival")
    private String expArrival;

    // These will be 00:00 if train terminates at this station
    @Element(name = "Schdepart")
    private String schDepart;
    @Element(name = "Expdepart")
    private String expDepart;

    @Element(name = "Destinationtime")
    private String destArrivalTime;
    @Element(name = "Origintime")
    private String originDepartureTime;
    @Element(name = "Traindate")
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
        this.updateTimeString = in.readString();
        this.status = in.readString();
        this.schArrival = in.readString();
        this.expArrival = in.readString();
        this.schDepart = in.readString();
        this.expDepart = in.readString();
        this.destArrivalTime = in.readString();
        this.originDepartureTime = in.readString();
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
        dest.writeString(this.updateTimeString);
        dest.writeString(this.status);
        dest.writeString(this.schArrival);
        dest.writeString(this.expArrival);
        dest.writeString(this.schDepart);
        dest.writeString(this.expDepart);
        dest.writeString(this.destArrivalTime);
        dest.writeString(this.originDepartureTime);
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

    public boolean equals(Object obj){
        boolean result = false;
        if(obj instanceof Train){
            Train otherTrain = (Train) obj;
            if(otherTrain.getTrainCode().equals(this.getTrainCode())){
                result = true;
            }
        }
        return result;
    }

    public int getViewType(){
        return TrainsDueRecyclerViewAdapter.RowType.TRAIN.ordinal();
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public String getUpdateTimeString() {
        return updateTimeString;
    }

    public void setUpdateTimeString(String updateTimeString) {
        this.updateTimeString = updateTimeString;
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

    public String getOriginDepartureTime() {
        return originDepartureTime;
    }

    public void setOriginDepartureTime(String originDepartureTime) {
        this.originDepartureTime = originDepartureTime;
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
