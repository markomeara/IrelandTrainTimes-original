package ie.markomeara.irelandtraintimes.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.j256.ormlite.field.DatabaseField;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Comparator;

/**
 * Created by Mark on 04/10/2014.
 */
@Root(name = "objStation", strict = false)
public class Station implements Comparable, Parcelable{

    private static final String TAG = Station.class.getSimpleName();

    @Element(name = "StationId")
    @DatabaseField
    private long id;

    @Element(name = "StationDesc")
    @DatabaseField
    private String name;

    @Element(name = "StationAlias", required = false)
    @DatabaseField
    private String alias;

    @Element(name = "StationLatitude", required = false)
    @DatabaseField
    private double latitude;

    @Element(name = "StationLongitude", required = false)
    @DatabaseField
    private double longitude;

    @Element(name = "StationCode", required = false)
    @DatabaseField
    private String code;

    @DatabaseField
    private boolean favourite;

    public Station(){ }

    public Station(long id, String name, String alias, double latitude, double longitude, String code) {
        this(id, name, alias, latitude, longitude, code, false);
    }

    public Station(long id, String name, String alias, double latitude, double longitude, String code, boolean fav){
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
        this.favourite = fav;
    }

    public Station(Parcel in){
        this.id = in.readLong();
        this.name = in.readString();
        this.alias = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.code = in.readString();
        this.favourite = in.readByte() != 0;
    }


    @Override
    public int compareTo(Object another) {
        int result = 0;
        if(another instanceof Station){
            Station otherStation = (Station) another;
            result = this.getName().compareToIgnoreCase(otherStation.getName());
        }
        return result;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getAlias() {
        return alias;
    }

    public String getDisplayName() {
        return (alias == null || alias.isEmpty()) ? name : alias;
    }

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
