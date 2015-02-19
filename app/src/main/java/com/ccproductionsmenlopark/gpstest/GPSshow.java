package com.ccproductionsmenlopark.gpstest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;

//import android.text.format.DateFormat;
import java.text.DateFormat;

import java.util.Date;

import android.location.Location;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


public class GPSshow extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient = null;
    private boolean mRequestingLocationUpdates = true;

    private Location mLastLocation;
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    private LocationRequest mLocationRequest;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private TextView mLastUpdateTimeText;
    private TextView mAltitudeText;
    private TextView mAccuracyText;


    static final int DIALOG_REQUEST_CODE = 1020;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gpsshow);

        buildGoogleApiClient();

        mLatitudeText = (TextView)findViewById(R.id.mLatitudeText);
        mLongitudeText = (TextView)findViewById(R.id.mLongitudeText);
        mLastUpdateTimeText = (TextView)findViewById(R.id.mLastUpdateTimeTextView);
        mAltitudeText = (TextView)findViewById(R.id.mAltitude);
        mAccuracyText = (TextView)findViewById(R.id.mAccuracy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();

        int playServicesInstalled = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (playServicesInstalled != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(playServicesInstalled, this,
                    DIALOG_REQUEST_CODE);
        }

        boolean isConnected = mGoogleApiClient.isConnected();
        if (isConnected && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        //createLocationRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gpsshow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        } else {
            mLatitudeText.setText("LAST LOCATION == null");
        }
        if (mRequestingLocationUpdates) {
            createLocationRequest();
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnectionSuspended (int cause) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        boolean isConnected = mGoogleApiClient.isConnected();
        if (isConnected) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        boolean isConnected = mGoogleApiClient.isConnected();
        if (isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI(mCurrentLocation);
    }

    private void updateUI(Location location) {
        mLatitudeText.setText("Lat: " + String.valueOf(location.getLatitude()));
        mLongitudeText.setText("Long: " +String.valueOf(location.getLongitude()));
        mLastUpdateTimeText.setText("Time: " +mLastUpdateTime);
        mAccuracyText.setText("Accuracy: " +String.valueOf(location.getAccuracy()));
        if (location.hasAltitude()) {
            mAltitudeText.setText("Altitude: " + String.valueOf(location.getAltitude()));
        } else {
            mAltitudeText.setText("Altitude: " + "no data");

        }
    }
}
