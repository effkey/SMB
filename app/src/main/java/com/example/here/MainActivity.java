package com.example.here;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Location;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private PermissionsRequestor permissionsRequestor;
    private PlatformPositioningProvider platformPositioningProvider;
    private LocationIndicator locationIndicator;
    private MapMeasure mapMeasureZoom;
    private android.location.Location startLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeHERESDK();

        setContentView(R.layout.activity_main);

        handleAndroidPermissions();
        platformPositioningProvider = new PlatformPositioningProvider(MainActivity.this);
        this.startLocation = platformPositioningProvider.getLastKnownLocation();

        this.mapView = findViewById(R.id.map_view);
        this.mapView.onCreate(savedInstanceState);
        this.loadMapScene();

        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(android.location.Location location) {
                mapView.getCamera().lookAt(
                        new GeoCoordinates(location.getLatitude(), location.getLongitude()), mapMeasureZoom);
                locationIndicator.updateLocation(convertLocation(location));
            }
        });
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
                locationIndicator.updateLocation(convertLocation(startLocation));

                mapView.addLifecycleListener(locationIndicator);
                mapView.getCamera().lookAt(
                        new GeoCoordinates(startLocation.getLatitude(), startLocation.getLongitude()), mapMeasureZoom);
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
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

    private void addLocationIndicator(GeoCoordinates geoCoordinates,
                                      LocationIndicator.IndicatorStyle indicatorStyle) {
        LocationIndicator locationIndicator = new LocationIndicator();
        locationIndicator.setLocationIndicatorStyle(indicatorStyle);

        // A LocationIndicator is intended to mark the user's current location,
        // including a bearing direction.
        // For testing purposes, we create a Location object. Usually, you may want to get this from
        // a GPS sensor instead.
        Location location = new Location(geoCoordinates);
        location.time = new Date();
        location.bearingInDegrees = 0.0;

        locationIndicator.updateLocation(location);

        // A LocationIndicator listens to the lifecycle of the map view,
        // therefore, for example, it will get destroyed when the map view gets destroyed.
        mapView.addLifecycleListener(locationIndicator);
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
        platformPositioningProvider.stopLocating();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}