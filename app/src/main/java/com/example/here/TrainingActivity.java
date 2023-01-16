package com.example.here;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.here.routeCreator.RouteCreatorTrainingActive;
import com.example.here.routeCreator.RouteCreatorTrainingSuspended;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class TrainingActivity extends AppCompatActivity {

    private Button endButton;
    private Button pauseReturnButton;
    private TextView curSpeedTextView;
    private TextView avgSpeedTextView;
    private TextView maxSpeedTextView;
    private TextView distanceTextView;
    private TextView timeTextView;
    private TextView kcalTextView;
    private MapView mapView;

    private int timeInSec;
    private boolean trainingActive = true;

    private float curSpeed = 0;
    private float avgSpeed = 0;
    private float maxSpeed = 0;
    private float distance = 0;
    private int kcal = 0;

    private PermissionsRequestor permissionsRequestor;
    private MapMeasure mapMeasureZoom;
    //    private android.location.Location startLocation;
    private android.location.Location pastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private float distanceUntilWaypoint = 10.0f;
    private LocationIndicator locationIndicator;
    private LocationCallback locationCallback;
    private List<Waypoint> currentWaypoints = new ArrayList<>();
    RouteCreatorTrainingActive routeCreatorTrainingActive;
    RouteCreatorTrainingSuspended routeCreatorTrainingSuspended;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.training_activity);
        this.mapView = findViewById(R.id.map_view);
        this.endButton = findViewById(R.id.end_trening);
        this.pauseReturnButton = findViewById(R.id.return_pause_training);
        this.timeTextView = findViewById(R.id.time);
        this.curSpeedTextView = findViewById(R.id.cur_speed);
        this.avgSpeedTextView = findViewById(R.id.avg_speed);
        this.maxSpeedTextView = findViewById(R.id.max_speed);
        this.distanceTextView = findViewById(R.id.distance);
        this.kcalTextView = findViewById(R.id.kcal);
        this.locationIndicator = new LocationIndicator();

        this.mapView.onCreate(savedInstanceState);

        this.pauseReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trainingActive){
                    pauseReturnButton.setText(R.string.paused);
                    trainingActive =!trainingActive;
                    pauseReturnButton.setBackgroundColor(getResources().getColor(R.color.orange));
                    routeCreatorTrainingSuspended.createRoute(currentWaypoints);
                }else{
                    pauseReturnButton.setText(R.string.started);
                    trainingActive =!trainingActive;
                    pauseReturnButton.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
        });
        this.endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        displayTrainingData();
        handleAndroidPermissions();
    }

    private void startTrainingActivity() {
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
                    pastLocation = location;
                }
            });

        }
        else {
//            Toast.makeText(getApplicationContext(),"Permission denied",Toast.LENGTH_SHORT).show();
            Log.d("PERM", "NO PERMISSIONS");
            finish();
        }
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                android.location.Location location = locationResult.getLastLocation();
//                mapView.onCreate(savedInstanceState);
//                mapView.getCamera().lookAt(
//                        new GeoCoordinates(location.getLatitude(), location.getLongitude()), mapMeasureZoom);
                if(locationIndicator != null)
                    locationIndicator.updateLocation(LocationConverter.convertToHERE(location));

                float currentDistance = pastLocation.distanceTo(location);
                distanceUntilWaypoint -= currentDistance;

                if(distanceUntilWaypoint <= 0) {
                    distanceUntilWaypoint = 10.0f;
                    currentWaypoints.add(new Waypoint(new GeoCoordinates(location.getLatitude(), location.getLongitude())));
                }

                if (trainingActive) {

                    distance += currentDistance;

                    avgSpeed = distance / timeInSec;
                    curSpeed = location.getSpeed();
                    if (curSpeed > maxSpeed)
                        maxSpeed = curSpeed;
                    displayTrainingData();

                    pastLocation = new android.location.Location(location);

                    routeCreatorTrainingActive.createRoute(currentWaypoints);
                }
            }
        };



        new TimeMeasure().start();

        loadMapScene();

        this.routeCreatorTrainingActive = new RouteCreatorTrainingActive(this.mapView, this.locationIndicator);
        this.routeCreatorTrainingSuspended = new RouteCreatorTrainingSuspended(this.mapView);

        this.fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                double distanceInMeters = 1000 * 10;
                mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);

                locationIndicator = new LocationIndicator();
                locationIndicator.setLocationIndicatorStyle(LocationIndicator.IndicatorStyle.PEDESTRIAN);
                locationIndicator.updateLocation(LocationConverter.convertToHERE(this.pastLocation)); // start

                mapView.addLifecycleListener(locationIndicator);
                mapView.getCamera().lookAt(
                        new GeoCoordinates(pastLocation.getLatitude(), pastLocation.getLongitude()), mapMeasureZoom); //start
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

    private void displayTrainingData() {
        this.curSpeedTextView.setText(getString(R.string.curSpeedText, curSpeed));
        this.avgSpeedTextView.setText(getString(R.string.avgSpeedText, avgSpeed));
        this.maxSpeedTextView.setText(getString(R.string.maxSpeedText, maxSpeed));
        this.distanceTextView.setText(getString(R.string.distanceText, distance));
        this.kcalTextView.setText(getString(R.string.kcalText, kcal));
    }

    private class TimeMeasure extends Thread{
        @Override
        public void run() {
            try {
                while(true)
                    if(trainingActive){
                        timeInSec++;
                        timeTextView.setText("Czas: "+String.format("%02d:%02d:%02d",timeInSec/3600,timeInSec%3600/60, timeInSec%60));
                        sleep(1000);
                    }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void handleAndroidPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startTrainingActivity();
            return;
        }
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                startTrainingActivity();
            }

            @Override
            public void permissionsDenied() {
                Toast.makeText(getApplicationContext(),"Cannot start app: Location service and permissions are needed for this app.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        if(this.mapView != null)
            this.mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(this.mapView != null)
            this.mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(this.mapView != null)
            this.mapView.onDestroy();
        if(this.fusedLocationProviderClient != null)
            this.fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if(this.mapView != null)
            this.mapView.onSaveInstanceState(outState);
    }

}