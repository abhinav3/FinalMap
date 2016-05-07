package com.example.omsairam.finalmaps;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by omsairam on 4/12/2016.
 */
public class LocBuzzService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final String TAG = "HelloService";
    private boolean isRunning = false;
    public Vibrator vibrator;
    private  TextView mTextView;

    @Override
    public void onCreate() {
      super.onCreate();
        isRunning=true;

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        Toast.makeText(getApplicationContext(), "Service On Command", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Bundle b=intent.getExtras();
                    //Double lat = b.getDouble("lat");
                    //Double lng = b.getDouble("lng");


                    //vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    //vibrator.vibrate(3000);
                    Toast.makeText(getApplicationContext(), "Inside Thread", Toast.LENGTH_LONG).show();
                    mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(LocBuzzService.this)
                            .addOnConnectionFailedListener(LocBuzzService.this)
                            .build();
                    mGoogleApiClient.connect();


                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));

                }

                // stopSelf();
            }
        }).start();


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.i(TAG, "Service onBind");
        Toast.makeText(getApplicationContext(), "Service On Bind", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "Service onDestroy");
        Toast.makeText(getApplicationContext(), "Service Destroyed", Toast.LENGTH_LONG).show();
        //isRunning = false;
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(TAG, "Onconnected");
        Toast.makeText(getApplicationContext(), "Google Client connected", Toast.LENGTH_LONG).show();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000); // Update location every second
        //LocationServices.FusedLocationApi.requestLocationUpdates(
        //mGoogleApiClient, mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "LocationChanged");
        Double lat1 = location.getLatitude();
        Double lng1 = location.getLongitude();
        Log.i(TAG, String.valueOf(lat1) + ",  " + String.valueOf(lng1));
        Toast.makeText(getApplicationContext(),"Location Changed", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
