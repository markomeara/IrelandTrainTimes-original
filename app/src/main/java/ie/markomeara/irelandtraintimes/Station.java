package ie.markomeara.irelandtraintimes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by Mark on 04/10/2014.
 */
public class Station implements Comparator{

    private long id;
    private String name;
    private String alias;
    private double latitude;
    private double longitude;
    private String code;
    private boolean favourite;

    public Station(long id, String name, String alias, double latitude, double longitude, String code) {
        this(id, name, alias, latitude, longitude, code, false);
    }

    public Station(long id, String name, String alias, double latitude, double longitude, String code, boolean fav) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
        this.favourite = fav;
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

}
