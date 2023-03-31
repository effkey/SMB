package com.example.here;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class SetRouteActivity extends AppCompatActivity {

    private MapView mapView;
    private Button resetRoute;
    private Button setRoute;
    private Button cancelButton;
    private Button skipSetRoute;

    // Czy wszystko z tego potrzebne?? (skopiowane z TrainingActivity)
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
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_route_activity);

        this.mapView = findViewById(R.id.map_view);
        this.resetRoute = findViewById(R.id.reset_route);
        this.setRoute = findViewById(R.id.set_route);
        this.cancelButton = findViewById(R.id.cancel_button);
        this.skipSetRoute = findViewById(R.id.skip_route_button);
        this.locationIndicator = new LocationIndicator();

        this.mapView.onCreate(savedInstanceState);

        resetRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Zresetuj zaznaczoną trasę
            }
        });

        setRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Zapisz zaznaczoną trasę
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Anuluj i wyjdź z widoku
                Intent intent = new Intent(SetRouteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        skipSetRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetRouteActivity.this, TrainingActivity.class);
                startActivity(intent);
            }
        });

    }
}
