package com.example.here;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Location;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private PermissionsRequestor permissionsRequestor;
    private LocationIndicator locationIndicator;
    private MapMeasure mapMeasureZoom;
//    private android.location.Location startLocation;
    private android.location.Location pastLocation;
    private float distance = 0.0f;
    private TextView speedText, avgSpeedText, distanceText, timeText;
    private float speed = 0, avgSpeed = 0;
    private long time, startTime;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            android.location.Location location = locationResult.getLastLocation();
            mapView.getCamera().lookAt(
                    new GeoCoordinates(location.getLatitude(), location.getLongitude()), mapMeasureZoom);
            locationIndicator.updateLocation(convertLocation(location));

            distance += pastLocation.distanceTo(location);
            pastLocation = new android.location.Location(location);

            time = (System.nanoTime() - startTime) / 1_000_000_000;

            avgSpeed = distance/time;

            speed = location.getSpeed();

            displayData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeHERESDK();

        setContentView(R.layout.activity_main);
        this.mapView = findViewById(R.id.map_view);
        this.mapView.onCreate(savedInstanceState);

        handleAndroidPermissions(savedInstanceState);

    }

    private void startLocating(Bundle savedInstanceState) {
        this.locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    pastLocation = location; //start
                }
            });
        }
        else {
//            Toast.makeText(getApplicationContext(),"Permission denied",Toast.LENGTH_SHORT).show();
            Log.d("PERM", "NO PERMISSIONS");
            finish();
        }

//        this.pastLocation = startLocation;

        this.speedText = findViewById(R.id.speedTextView);
        this.avgSpeedText = findViewById(R.id.avgSpeedTextView);
        this.distanceText = findViewById(R.id.distanceTextView);
        this.timeText = findViewById(R.id.timeTextView);

        this.loadMapScene();

        displayData();

        this.startTime = System.nanoTime();
        this.fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void displayData() {
        speedText.setText(getString(R.string.speed, speed));
        distanceText.setText(getString(R.string.distance, distance));
        avgSpeedText.setText(getString(R.string.avg, avgSpeed));
        timeText.setText(getString(R.string.time, time));
    }

    private void initializeHERESDK() {
        String accessKeyID = "-BT3t43rvbrnst8P5yJBig";
        String accessKeySecret = "a3jWnLaAjsC10STdlzEgqsGrncFC18grLmXjS5MTBfAFqjV4e615e_67iNqnYfbedjcle3CQavPptu7L9c95KA";
        SDKOptions options = new SDKOptions(accessKeyID, accessKeySecret);
        try {
            Context context = this;
            SDKNativeEngine.makeSharedInstance(context, options);
            Log.d("HERE_initialize: ", "easy peazy");
        } catch (InstantiationErrorException e) {
            Log.d("HERE_initialize: ", "failure");
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }

    private void disposeHERESDK() {
        SDKNativeEngine sdkNativeEngine = SDKNativeEngine.getSharedInstance();
        if (sdkNativeEngine != null) {
            sdkNativeEngine.dispose();
            SDKNativeEngine.setSharedInstance(null);
        }
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                double distanceInMeters = 1000 * 10;
                mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);

                locationIndicator = new LocationIndicator();
                locationIndicator.setLocationIndicatorStyle(LocationIndicator.IndicatorStyle.PEDESTRIAN);
                locationIndicator.updateLocation(convertLocation(this.pastLocation)); // start

                mapView.addLifecycleListener(locationIndicator);
                mapView.getCamera().lookAt(
                        new GeoCoordinates(pastLocation.getLatitude(), pastLocation.getLongitude()), mapMeasureZoom); //start
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }


    private void handleAndroidPermissions(Bundle savedInstanceState) {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                startLocating(savedInstanceState);
            }

            @Override
            public void permissionsDenied() {
                Toast.makeText(getApplicationContext(),"Cannot start app: Location service and permissions are needed for this app.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private Location convertLocation(android.location.Location nativeLocation) {
        GeoCoordinates geoCoordinates = new GeoCoordinates(
                nativeLocation.getLatitude(),
                nativeLocation.getLongitude(),
                nativeLocation.getAltitude());

        Location location = new Location(geoCoordinates);

        if (nativeLocation.hasBearing()) {
            location.bearingInDegrees = (double) nativeLocation.getBearing();
        }

        if (nativeLocation.hasSpeed()) {
            location.speedInMetersPerSecond = (double) nativeLocation.getSpeed();
        }

        if (nativeLocation.hasAccuracy()) {
            location.horizontalAccuracyInMeters = (double) nativeLocation.getAccuracy();
        }

        return location;
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        disposeHERESDK();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}