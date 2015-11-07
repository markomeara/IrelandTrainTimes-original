package ie.markomeara.irelandtraintimes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by Mark on 04/10/2014.
 */
public class Station implements Comparator, Parcelable{

    private static final String TAG = Station.class.getSimpleName();

    private long id;
    private String name;
    private String alias;
    private String displayName;
    private double latitude;
    private double longitude;
    private String code;
    private boolean favourite;

    public Station(long id, String name, String alias, String displayName, double latitude, double longitude, String code) {
        this(id, name, alias, displayName, latitude, longitude, code, false);
    }

    public Station(long id, String name, String alias, String displayName, double latitude, double longitude, String code, boolean fav) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
        this.favourite = fav;
    }

    public Station(Parcel in){
        this.id = in.readLong();
        this.name = in.readString();
        this.alias = in.readString();
        this.displayName = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.code = in.readString();
        this.favourite = in.readByte() != 0;
    }

    public int compare(Object o1, Object o2){
        int result = 0;
        if(o1 instanceof Station && o2 instanceof Station){
            Station s1 = (Station) o1;
            Station s2 = (Station) o2;
            result = s1.getName().compareToIgnoreCase(s2.getName());
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getDisplayName() { return displayName; }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCode() {
        return code;
    }

    public long getId() {
        return id;
    }

    public boolean isFavourite() { return favourite; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(alias);
        dest.writeString(displayName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(code);
        dest.writeByte((byte) (favourite ? 1 : 0));
    }

    @Override
    public boolean equals(Object obj){
        boolean result = false;
        if(obj instanceof Station){
            Station otherStation = (Station) obj;
            if(otherStation.getId() == this.getId()){
                result = true;
            }
        }

        return result;
    }

    public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() {
        public Station createFromParcel(Parcel source) {
            return new Station(source);
        }

        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
}
