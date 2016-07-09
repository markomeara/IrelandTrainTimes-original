package ie.markomeara.irelandtraintimes.utils;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationUtils {

    public static final int LOCATION_UNKNOWN = -1;
    private static Location sLastLocation;
    public static List<LocationListener> sListeners = new ArrayList<LocationListener>();

    // This code was mainly taken from stackoverflow
    public static int distFromCurrentLocation(double lat2, double lng2) {

        int distanceKm = LOCATION_UNKNOWN;
        if(sLastLocation != null) {

            double lat1 = sLastLocation.getLatitude();
            double lng1 = sLastLocation.getLongitude();

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
        sLastLocation = location;

        // Notify listeners
        for(LocationListener listener : sListeners){
            listener.locationUpdated();
        }
    }

    public static void notifyWhenLocationUpdated(LocationListener listener){
        boolean inListAlready = false;
        for(LocationListener existingListener : sListeners){
            if(existingListener == listener){
                inListAlready = true;
                break;
            }
        }
        if(!inListAlready){
            sListeners.add(listener);
        }
    }

    public static void removeNotificationListener(LocationListener listener){
        for(int i = 0; i < sListeners.size(); i++){
            if(sListeners.get(i) == listener){
                sListeners.remove(i);
                break;
            }
        }
    }

    public interface LocationListener{
        void locationUpdated();
    }
}
