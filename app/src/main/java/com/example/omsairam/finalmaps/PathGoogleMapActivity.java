package com.example.omsairam.finalmaps;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class PathGoogleMapActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , LocationListener ,SensorEventListener, OnMapReadyCallback{


    //KAmeng = (26.190459,91.699407)
    /*private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);*/
    /*UserLoc mUserLoc=new UserLoc();
    FrndLoc mFrndLoc=new FrndLoc();

    private final String LOG_TAG = "Debug";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng myLatLng=new LatLng(26.190459,91.699407);*/
    //private static final LatLng KAMENG_REGION = new LatLng(26.190459,91.699407);
    /*private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);*/

    //private static final LatLng LOHIT_REGION = new LatLng(26.188671,91.696207);
   /* private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);*/

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    private LocationRequest mLocationRequest;
    private SensorManager sensorManager;
    private float accelerationThreshold = 2;
    private long lastUpdate;
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private final long startTime = 5 * 1000;
    private final long interval = 1 * 1000;
    private TextView currentX, currentY, currentZ;
    private UserLoc mUserLoc = new UserLoc();
    private FrndLoc mFrndLoc=new FrndLoc();
    private LatLng myLatLng=new LatLng(26.190459,91.699407);

    GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_path_google_map);


        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));



        currentX=(TextView) findViewById(R.id.XAcc);
        currentY=(TextView) findViewById(R.id.YAcc);
        currentZ=(TextView) findViewById(R.id.ZAcc);
        buildGoogleApiClient();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        countDownTimer = new MyCountDownTimer(startTime, interval);

        //waiting for 5 sec to get the locations updates then disconnects the client.
        countDownTimer.start();

        // load map
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fm.getMapAsync(this);
        //Starting service


        /*Intent intent = new Intent(this, LocBuzzService.class);
        startService(intent);*/
       // Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_SHORT).show();

    }



    @Override
    public void onMapReady(GoogleMap mgoogleMap) {
        LatLng KAMENG_REGION= mUserLoc.getSelfLoc();
        googleMap=mgoogleMap;
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KAMENG_REGION, 13));
        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(KAMENG_REGION, 16));
        addMarkers();

    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    //Toast.makeText(getApplicationContext(),point.toString(),Toast.LENGTH_SHORT).show();
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(18);
                polyLineOptions.color(Color.BLUE);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelerationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelerationSquareRoot >= accelerationThreshold) //value is =4
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            currentX.setText(Float.toString(x));
            currentY.setText(Float.toString(y));
            currentZ.setText(Float.toString(z));
            lastUpdate = actualTime;
            fireGoogleClinet();
            Toast.makeText(this, "Device was shuffled, firing Google APi Client", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }



    public class MyCountDownTimer extends CountDownTimer { //CountDown Timer Class.
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            if(mGoogleApiClient.isConnected()){
                mGoogleApiClient.disconnect();
                Toast.makeText(getApplicationContext(),"Google client Disconnected, 5 Sec ended", Toast.LENGTH_SHORT).show();
                timerHasStarted = false;
                countDownTimer.cancel();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

    }

    public void fireGoogleClinet(){
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Toast.makeText(getApplicationContext(),"Google client is now connected", Toast.LENGTH_SHORT).show();
            countDownTimer.start();
            /*if (!timerHasStarted) { //waiting for 5 sec to get the locations updates then disconnects the client.
                countDownTimer.start();
                timerHasStarted = true;
            } else {
                countDownTimer.cancel();
                timerHasStarted = false;
            }*/
        }
        else{
            Toast.makeText(getApplicationContext(),"Google client is already Connected", Toast.LENGTH_SHORT).show();
            countDownTimer.start();
            /*if (!timerHasStarted) { //waiting for 5 sec to get the locations updates then disconnects the client.
                countDownTimer.start();
                timerHasStarted = true;
            } else {
                countDownTimer.cancel();
                timerHasStarted = false;
            }*/
        }

    }



    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);//Update location every 10 second.
        mLocationRequest.setFastestInterval(5000);

        /*Since SDK 23, you should/need to check the permission before you call Location API functionality. Here is an example of how to do it:*/



        //Set the last location.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText("In On connected\n"+String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText("In On connected\n" + String.valueOf(mLastLocation.getLongitude()));
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Implement the access granting popup.

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }


    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override

    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Toast.makeText(getApplicationContext(), "GoogleApiClient has been suspended", Toast.LENGTH_SHORT).show();//not able to connect this time only.
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {


        //We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && location != null) {
            mLatitudeText.setText(String.valueOf(location.getLatitude()));
            mLongitudeText.setText(String.valueOf(location.getLongitude()));
            Log.i(TAG, location.toString());
            Toast.makeText(getApplicationContext(), "HI " + location.toString(), Toast.LENGTH_SHORT).show();
            myLatLng =new LatLng(location.getLatitude(),location.getLongitude());
            mUserLoc.setSelfLoc(myLatLng);
            //now reload the map
            String url = getMapsApiDirectionsUrl();
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(KAMENG_REGION, 16));
            googleMap.clear();
            addMarkers();

        }
        else{
            Toast.makeText(getApplicationContext(), "HI first connect the client", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "GoogleApiClient connection has failed", Toast.LENGTH_SHORT).show();//not able to connect this time only.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }




    private String getMapsApiDirectionsUrl() {

        LatLng KAMENG_REGION= mUserLoc.getSelfLoc();
        LatLng LOHIT_REGION= mFrndLoc.getFrndLoc();

        String waypoints = "waypoints=optimize:true|"
                + KAMENG_REGION.latitude + "," + KAMENG_REGION.longitude
                + "|" + "|" + LOHIT_REGION.latitude + ","
                + LOHIT_REGION.longitude;/* + "|" +"|" + WALL_STREET.latitude + ","
                + WALL_STREET.longitude;*/

        String sensor = "sensor=false";
        String origin = "origin=" + KAMENG_REGION.latitude + "," + KAMENG_REGION.longitude;
        String destination = "destination=" + LOHIT_REGION.latitude + "," + LOHIT_REGION.longitude;
        String params = origin + "&" + destination + "&%20" + waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;


        return url;
    }

    private void addMarkers() {

        LatLng KAMENG_REGION= mUserLoc.getSelfLoc();
        LatLng LOHIT_REGION= mFrndLoc.getFrndLoc();
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(LOHIT_REGION)
                    .title("LOHIT_REGION"));
            googleMap.addMarker(new MarkerOptions().position(KAMENG_REGION)
                    .title("KAMENG_REGION"));
           /* googleMap.addMarker(new MarkerOptions().position(WALL_STREET)
                    .title("Third Point"));*/
        }
    }


}

