package ie.markomeara.irelandtraintimes.location;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markomeara on 04/05/2015.
 */
public class LocationUtils {

    private static Location lastLocation;
    public static final int LOCATION_UNKNOWN = -1;
    public static List<LocationListener> listeners = new ArrayList<LocationListener>();

    // This code was mainly taken from stackoverflow
    public static int distFromCurrentLocation(double lat2, double lng2) {

        int distanceKm = LOCATION_UNKNOWN;
        if(lastLocation != null) {

            double lat1 = lastLocation.getLatitude();
            double lng1 = lastLocation.getLongitude();

            double earthRadius = 6371000; //metres
            double dLat = Math.toRadians(lat2 - lat1);
            double dLng = Math.toRadians(lng2 - lng1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLng / 2) * Math.sin(dLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            // Distance in metres
            float dist = (float) (earthRadius * c);
            distanceKm = (int) (dist / 1000);
        }
        return distanceKm;
    }

    public static void updateLastLocation(Location location){
        lastLocation = location;

        // Notify listeners
        for(LocationListener listener : listeners){
            listener.locationUpdated();
        }
    }

    public static void notifyWhenLocationUpdated(LocationListener listener){
        boolean inListAlready = false;
        for(LocationListener existingListener : listeners){
            if(existingListener == listener){
                inListAlready = true;
                break;
            }
        }
        if(!inListAlready){
            listeners.add(listener);
        }
    }

    public static void removeNotificationListener(LocationListener listener){
        for(int i = 0; i < listeners.size(); i++){
            if(listeners.get(i) == listener){
                listeners.remove(i);
                break;
            }
        }
    }

    public interface LocationListener{
        void locationUpdated();
    }
}
