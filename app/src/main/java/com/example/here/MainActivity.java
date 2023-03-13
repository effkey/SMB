package com.example.here;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.BicycleOptions;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;

// W MainActivity znajduję się tylko dolny pasek z menu z tego tutoriala - https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeHERESDK();

// Przełączanie pomiędzy fragmentami w dolnym menu

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnItemSelectedListener) this);

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home);

        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    HomeFragment homeFragment = new HomeFragment();
    RunFragment runFragment = new RunFragment();
    CupFragment cupFragment = new CupFragment();
    MovingFragment movingFragment = new MovingFragment();
    PersonFragment personFragment = new PersonFragment();

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;
            case R.id.run:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, runFragment).commit();
                return true;
            case R.id.cup:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, cupFragment).commit();
                return true;
            case R.id.moving:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, movingFragment).commit();
                return true;
            case R.id.person:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, personFragment).commit();
                return true;
        }
        return false;
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

    @Override
    protected void onDestroy() {
        disposeHERESDK();
        super.onDestroy();
    }
}

//public class MainActivity extends AppCompatActivity {
//
//    private double lastTourStartV1 = 53.41178404163292, lastTourStartV2 = 23.516119474276664,           // pobierane z bazy danych / z pamieci urzadzenia
//            lastTourEndV1 = 53.1276662351446, lastTourEndV2 = 23.160716949523863;
//    private int avgSpeedLastTour = 25;  // km/h
//    private int timeOfActivity = 78;    // minuty
//    private int maxSpeedLastTour = 43;  // km/s
//    private int loseKcal = 1527;
//
//    private MapView mapView;
//    private TextView duration, avgSpeed, maxSpeed, distance, kcal;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initializeHERESDK();
//        this.mapView = findViewById(R.id.map_view);
//        this.mapView.onCreate(savedInstanceState);
//        setToLastRoute(new Waypoint(new GeoCoordinates(lastTourStartV1, lastTourStartV2)), new Waypoint(new GeoCoordinates(lastTourEndV1, lastTourEndV2)));     //rysowanie poprzedniej trasy po wspolrzednych
//    }
//

//
//    private void setToLastRoute(Waypoint start, Waypoint end){
//        RoutingEngine routingEngine;
//        try {
//            routingEngine = new RoutingEngine();
//        } catch (InstantiationErrorException e) {
//            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
//        }
//
//        Waypoint startWaypoint = start;
//        Waypoint destinationWaypoint = end;
//
//        List<Waypoint> waypoints =
//                new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));
//
//        routingEngine.calculateRoute(
//                waypoints,
//                new BicycleOptions(),
//                new CalculateRouteCallback() {
//                    @Override
//                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
//                        if (routingError == null) {
//                            Route route = routes.get(0);
//                            GeoPolyline routeGeoPolyline = route.getGeometry();
//                            float widthInPixels = 10;
//                            MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline,
//                                    widthInPixels,
//                                    Color.valueOf(0,1,0)); // RGBA
//
//                            mapView.getMapScene().addMapPolyline(routeMapPolyline);
////                            long estimatedTravelTimeInSeconds = route.getDuration().getSeconds();     //przewidywany czas
//                            int routeLengthInMeters = route.getLengthInMeters();    //mierzy odleglosc
//                            distance.setText("Odległość: " + routeLengthInMeters/1000.0 + " km");
//                            Log.d("wyliczona odleglosc: ", routeLengthInMeters+"");
//                            loadMapScene(routeLengthInMeters);
//                        }
//                    }
//                });
//    }
//
//    private void loadMapScene(int routeLengthInMeters) {
//        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
//            if (mapError == null) {
//                MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, routeLengthInMeters);
//                mapView.getCamera().lookAt(
//                        new GeoCoordinates((lastTourStartV1+lastTourEndV1)/2, (lastTourStartV2+lastTourEndV2)/2), mapMeasureZoom);
//            } else {
//                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
//            }
//        });
//    }
//}