package com.example.refactoringwnamqos.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

public class GeoLocationListener implements LocationListener {
    private static final GeoLocationListener INSTANCE = new GeoLocationListener();
    private static final String TAG = "GeoLocationListener";
    private LocationManager locationManager;
    private Location location;
    private float minimumAccuracy = 16f;
    private ArrayList <LocationUpdatesListener> subscribers = new ArrayList<>();
    Context context;

    public interface LocationUpdatesListener {
        void onGetLocation();
    }

//    public GeoLocationListener() {
//        init();
//    }

    public  GeoLocationListener geoLocationListener(Context context) {
        this.context = context;
        return geoLocationListener(context);
    }

    @SuppressLint("MissingPermission")
    public void init() {
//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.001f, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.001f, this);
        }
    }

    private void stopListen() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        if (this.location.getAccuracy() <= location.getAccuracy()) {
            this.location = location;
        }

        if (this.location.getAccuracy() <= minimumAccuracy) {
            stopListen();
        }
        Log.d(TAG, "onLocationChanged: ");
        notifySubscribers();

    }

    private void notifySubscribers() {
        Log.d(TAG, "notifySubscribers: " + subscribers);
        Iterator <LocationUpdatesListener> iterator = subscribers.iterator();
        while (iterator.hasNext()) {
            iterator.next().onGetLocation();
            iterator.remove();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        //GPS_PROVIDER
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void subscribe(LocationUpdatesListener listener) {
        subscribers.add(listener);
        if (getUserLocation() != null) {
            notifySubscribers();
        }
    }

    public void unsubscribe(LocationUpdatesListener listener) {
        subscribers.remove(listener);
    }

    @SuppressLint("MissingPermission")
    public Location getUserLocation() {
        return location != null ? location :
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private Location getLocationFromUtils() {
        Location location = new Location("");
//        location.setLatitude(Double.parseDouble(PrefUtils.getData(PrefUtils.LAT)));
//        location.setLongitude(Double.parseDouble(PrefUtils.getData(PrefUtils.LNG)));
        return location;
    }
}
