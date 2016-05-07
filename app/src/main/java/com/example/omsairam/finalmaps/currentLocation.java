package com.example.omsairam.finalmaps;

/**
 * Created by omsairam on 4/12/2016.
 */
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


public class currentLocation extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String LOG_TAG = "Debug";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng myLatLng=new LatLng(26.190459,91.699407);
    private boolean mapLoaded=false;
    UserLoc mUserLoc=new UserLoc();
   /* private boolean mapLoaded=false;

    private GoogleMap mMap;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);*/
        //mapFragment.getMapAsync(this);


        //Build the mGoogleApiClient.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //connect the mGoogleApiClient
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {

        //Disconnect the mGoogleApiClient
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);//Update location every 10 second.
        mLocationRequest.setFastestInterval(5000);

        /*Since SDK 23, you should/need to check the permission before you call Location API functionality. Here is an example of how to do it:*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
       // Toast.makeText(getApplicationContext(), "GoogleApiClient has been suspended", Toast.LENGTH_LONG).show();//not able to connect this time only.
        Log.i(LOG_TAG, "GoogleApiClient has been suspended");

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(LOG_TAG, location.toString());
        //Toast.makeText(getApplicationContext(), "HI"+location.toString(), Toast.LENGTH_SHORT).show();
        myLatLng=new LatLng(location.getLatitude(),location.getLongitude());
        mUserLoc.setSelfLoc(myLatLng);

        /*if(!mapLoaded){

            mMap.addMarker(new MarkerOptions().position(myLatLng).title("Marker on You"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(25));
            mapLoaded=true;
        }


        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(myLatLng).title("Marker on You"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(25));*/

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(getApplicationContext(), "GoogleApiClient connection has failed", Toast.LENGTH_LONG).show();//not able to connect this time only.
        Log.i(LOG_TAG,"GoogleApiClient connection has failed");
    }

   /* public LatLng getMyLatLng() {
        return myLatLng;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        *//*currentLocation temp= new currentLocation();
        LatLng sydney = temp.getMyLatLng();

        mMap.addMarker(new MarkerOptions().position(myLatLng).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));*//*
    }*/
}
