package ie.markomeara.irelandtraintimes.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import ie.markomeara.irelandtraintimes.adapter.TrainsDueRecyclerViewAdapter;

/**
 *
 * This is train info as seen from a particular station (I.E. the 'dueIn' var is relevant to only one station)
 * The 'stationCodeViewedFrom' variable shows which station this train info has been viewed from
 */
@Root(name = "objStationData", strict = false)
public class Train implements Comparable<Train>, TrainListItem, Parcelable {

    private static final String TAG = Train.class.getSimpleName();

    public static final int MAJORDELAY_MINS = 5;

    @Element(name = "Traincode", required = false)
    private String mTrainCode;
    @Element(name = "Origin")
    private String mOrigin;
    @Element(name = "Destination")
    private String mDestination;
    @Element(name = "Lastlocation", required = false)
    private String mLatestInfo;
    @Element(name = "Direction")
    private String mDirection;
    @Element(name = "Traintype")
    private String mTrainType;
    @Element(name = "Stationcode")
    private String mStationCode;
    @Element(name = "Duein")
    private int mDueIn;
    @Element(name = "Late", required = false)
    private int mDelayMins;
    @Element(name = "Servertime")
    private String mUpdateTimeString;
    @Element(name = "Status", required = false)
    private String mStatus;
    @Element(name = "Scharrival")
    private String mSchArrival;
    @Element(name = "Exparrival")
    private String mExpArrival;

    // These will be 00:00 if train terminates at this station
    @Element(name = "Schdepart")
    private String mSchDepart;
    @Element(name = "Expdepart")
    private String mExpDepart;

    @Element(name = "Destinationtime")
    private String mDestArrivalTime;
    @Element(name = "Origintime")
    private String mOriginDepartureTime;
    @Element(name = "Traindate")
    private String mTrainDate;

    // No need to save train to DB as it doesn't need to persist, since it will go out of date pretty quickly

    public Train(){ }

    private Train(Parcel in) {
        this.mTrainCode = in.readString();
        this.mOrigin = in.readString();
        this.mDestination = in.readString();
        this.mLatestInfo = in.readString();
        this.mDirection = in.readString();
        this.mTrainType = in.readString();
        this.mDueIn = in.readInt();
        this.mDelayMins = in.readInt();
        this.mUpdateTimeString = in.readString();
        this.mStatus = in.readString();
        this.mSchArrival = in.readString();
        this.mExpArrival = in.readString();
        this.mSchDepart = in.readString();
        this.mExpDepart = in.readString();
        this.mDestArrivalTime = in.readString();
        this.mOriginDepartureTime = in.readString();
        this.mTrainDate = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTrainCode);
        dest.writeString(this.mOrigin);
        dest.writeString(this.mDestination);
        dest.writeString(this.mLatestInfo);
        dest.writeString(this.mDirection);
        dest.writeString(this.mTrainType);
        dest.writeInt(this.mDueIn);
        dest.writeInt(this.mDelayMins);
        dest.writeString(this.mUpdateTimeString);
        dest.writeString(this.mStatus);
        dest.writeString(this.mSchArrival);
        dest.writeString(this.mExpArrival);
        dest.writeString(this.mSchDepart);
        dest.writeString(this.mExpDepart);
        dest.writeString(this.mDestArrivalTime);
        dest.writeString(this.mOriginDepartureTime);
        dest.writeString(this.mTrainDate);
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
        if(!this.mDirection.equals(other.getDirection())){
            return this.mDirection.compareTo(other.getDirection());
        }
        else if(this.mDueIn != other.getDueIn()){
            return this.mDueIn > other.getDueIn() ? 1 : 0;
        }
        else{
            return this.mDestination.compareTo(other.getDestination());
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
        return mTrainCode;
    }

    public void setTrainCode(String trainCode) {
        this.mTrainCode = trainCode;
    }

    public String getUpdateTimeString() {
        return mUpdateTimeString;
    }

    public void setUpdateTimeString(String updateTimeString) {
        this.mUpdateTimeString = updateTimeString;
    }

    public String getOrigin() {
        return mOrigin;
    }

    public void setOrigin(String origin) {
        this.mOrigin = origin;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        this.mDestination = destination;
    }

    public String getLatestInfo() {
        return mLatestInfo;
    }

    public void setLatestInfo(String latestInfo) {
        this.mLatestInfo = latestInfo;
    }

    public String getDirection() {
        return mDirection;
    }

    public void setDirection(String direction) {
        this.mDirection = direction;
    }

    public String getTrainType() {
        return mTrainType;
    }

    public void setTrainType(String trainType) {
        this.mTrainType = trainType;
    }

    public String getStationCode() { return mStationCode; }

    public void setStationCode(String code) { this.mStationCode = code; }

    public int getDueIn() {
        return mDueIn;
    }

    public void setDueIn(int dueIn) {
        this.mDueIn = dueIn;
    }

    public int getDelayMins() {
        return mDelayMins;
    }

    public void setDelayMins(int delayMins) {
        this.mDelayMins = delayMins;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }

    public String getSchArrival() {
        return mSchArrival;
    }

    public void setSchArrival(String schArrival) {
        this.mSchArrival = schArrival;
    }

    public String getExpArrival() {
        return mExpArrival;
    }

    public void setExpArrival(String expArrival) {
        this.mExpArrival = expArrival;
    }

    public String getSchDepart() {
        return mSchDepart;
    }

    public void setSchDepart(String schDepart) {
        this.mSchDepart = schDepart;
    }

    public String getExpDepart() {
        return mExpDepart;
    }

    public void setExpDepart(String expDepart) {
        this.mExpDepart = expDepart;
    }

    public String getDestArrivalTime() {
        return mDestArrivalTime;
    }

    public void setDestArrivalTime(String destArrivalTime) {
        this.mDestArrivalTime = destArrivalTime;
    }

    public String getOriginDepartureTime() {
        return mOriginDepartureTime;
    }

    public void setOriginDepartureTime(String originDepartureTime) {
        this.mOriginDepartureTime = originDepartureTime;
    }

    public String getTrainDate() {
        return mTrainDate;
    }

    public void setTrainDate(String trainDate) {
        this.mTrainDate = trainDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
