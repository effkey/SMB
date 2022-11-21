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

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeHERESDK();

//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
//                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//        }else{
//            Log.d("Permissions: ", "Accepted");
//        }

        setContentView(R.layout.activity_main);
        this.mapView = findViewById(R.id.map_view);
        this.mapView.onCreate(savedInstanceState);
        this.loadMapScene();
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
                MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);
                mapView.getCamera().lookAt(
                        new GeoCoordinates(53.41178404163292, 23.516119474276664), mapMeasureZoom);
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
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
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}