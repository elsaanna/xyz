package cc.yamyam;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by siyuan on 03.08.15.
 */
public class Locator {
    public static final int LOCATION_CHANGED = 101;
    public static final int STOPPED = 0;
    public static final int RUNNING = 1;
    public static final int UNKNOWN = 2;
    public static final int GPSSEARCHING = 3;
    public static final int GPS_EVENT_FIRST_FIX = 4;
    public static final int GPS_EVENT_STOPPED = 5;

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public static final int MIN_UPDATE_DISTANCE = 0;
    public static final int MIN_UPDATE_TIME = 0;
    public static final String TAG = PhotoApp.TAG + " | locator";
    private Handler handler;
    private Context context;
    private Location currentLocation;

    public static String provider;

    public Date getUpdatetime() {
        return updatetime;
    }



    private Date updatetime;

    private List<LocationListener> listeners;
    private LocationManager locationManager;

    private static String locatorService;

    public static String getLocatorService() {
        return locatorService;
    }


    private int status = UNKNOWN;

    private int statusGPS = UNKNOWN;


    public Locator(Context context, Handler handler) {
        this.handler = handler;
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE
        );
    }

    public Location getLastKnownLocation() {
    // get last know gps provider location if null get network provider.
        Location location=null;
        if(statusGPS!=GPSSEARCHING)
        {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location!=null) locatorService = LocationManager.GPS_PROVIDER;
        }

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null) locatorService = LocationManager.NETWORK_PROVIDER;
        }
        if (location == null) {
            Log.i(Locator.TAG, "Last known location is null");
        }
        return location;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public GpsStatus.Listener mGPSStatusListener = new GpsStatus.Listener()
    {
        public void onGpsStatusChanged(int event)
        {
            switch(event)
            {
                case GpsStatus.GPS_EVENT_STARTED:

                    statusGPS = GPSSEARCHING;
                    currentLocation = null;

                    Toast.makeText(context, "GPS_SEARCHING", Toast.LENGTH_SHORT).show();

                    Log.i(Locator.TAG, "Location has GPS searching");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    System.out.println("TAG - GPS Stopped");
                    statusGPS = GPS_EVENT_STOPPED;
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    statusGPS = GPS_EVENT_FIRST_FIX;

                /*
                 * GPS_EVENT_FIRST_FIX Event is called when GPS is locked
                 */
                    Toast.makeText(context, "GPS_LOCKED", Toast.LENGTH_SHORT).show();
                    Location gpslocation = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(gpslocation != null)
                    {
                        update(gpslocation);

                        Log.i(Locator.TAG,"GPS get location:"+gpslocation.getLatitude()+":"+gpslocation.getLongitude());
                    /*
                     * Removing the GPS status listener once GPS is locked
                     */
                        locationManager.removeGpsStatusListener(mGPSStatusListener);
                    }

                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    //                 System.out.println("TAG - GPS_EVENT_SATELLITE_STATUS");
                    break;
            }
        }
    };

    private LocationListener LocatorLocationListener() {
        return new LocationListener(){

            @Override
            public void onLocationChanged(Location location) {
                Log.i(Locator.TAG, "Location has changed "+ location.getProvider()+"x:"+location.getLatitude()+" y:"+location.getLongitude());
                update(location);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i(Locator.TAG, provider + " is enabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i(Locator.TAG, provider + " is disabled");
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.i(Locator.TAG, provider + " status has changed to "
                        + Integer.toString(status));
            }
        };
    }



    public void startUpdates(boolean instantLastLocation) {
        if (status == RUNNING) {
            Log.i(PhotoApp.TAG + " | locator", "Already started");
            return;
        }
        status = RUNNING;

        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGPSEnabled) {
            locationManager.addGpsStatusListener(mGPSStatusListener);
        }


        Log.i(Locator.TAG, "Starting all location updates");
        listeners = new LinkedList<LocationListener>();
        LocationListener locatorListener;
        Iterator<String> i = locationManager.getProviders(true).iterator();
        do {
            locatorListener = LocatorLocationListener();
            listeners.add(locatorListener);
            locationManager.requestLocationUpdates(
                    i.next(), MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE,
                    locatorListener
            );
        } while (i.hasNext());

        if (instantLastLocation)
            update(getLastKnownLocation());
    }

    public void stopUpdates() {
        Log.i(Locator.TAG, "Stopping all location updates");
        Iterator <LocationListener> i = listeners.iterator();
        do {
            locationManager.removeUpdates(i.next());
        } while (i.hasNext());

        status = STOPPED;
    }

    public void restartUpdates() {
        Log.i(Locator.TAG, "Restarting updates");

        currentLocation = null;
        updatetime = null;

        stopUpdates();
        startUpdates(false);
    }

    private void update(Location newLocation) {
        if (newLocation!=null){
            currentLocation = newLocation;
            updatetime = new Date();
            Message msg = new Message();
            msg.what = LOCATION_CHANGED;
            msg.obj = currentLocation;
            handler.sendMessage(msg);
            Log.i(Locator.TAG, "Got a new location!"+newLocation.getProvider()+" x:"+newLocation.getLatitude()+" y:"+newLocation.getLongitude());

        }
    }

    public int getStatus() {
        return status;
    }

    public int getStatusGPS() {
        return statusGPS;
    }



    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}

