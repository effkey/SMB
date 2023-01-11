package com.example.here;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
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

public class RunFragment extends Fragment {

    private View view;

    private TextView duration, avgSpeed, maxSpeed, distance, kcal;
    private double lastTourStartV1 = 53.41178404163292, lastTourStartV2 = 23.516119474276664,           // pobierane z bazy danych / z pamieci urzadzenia
            lastTourEndV1 = 53.1276662351446, lastTourEndV2 = 23.160716949523863;
    private int avgSpeedLastTour = 25;  // km/h
    private int timeOfActivity = 78;    // minuty
    private int maxSpeedLastTour = 43;  // km/s
    private int loseKcal = 1527;

    private MapView mapView;
    private Button startTraining;

    public RunFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_run, container, false);


        this.mapView = view.findViewById(R.id.map_view);
        this.startTraining = view.findViewById(R.id.start_new_training);
        this.duration = view.findViewById(R.id.duration);
        this.avgSpeed = view.findViewById(R.id.avg_speed);
        this.maxSpeed = view.findViewById(R.id.max_speed);
        this.distance = view.findViewById(R.id.distance);
        this.kcal = view.findViewById(R.id.kcal);

        this.startTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TrainingActivity.class));
            }
        });

        this.mapView.onCreate(savedInstanceState);
        setToLastRoute(new Waypoint(new GeoCoordinates(lastTourStartV1, lastTourStartV2)), new Waypoint(new GeoCoordinates(lastTourEndV1, lastTourEndV2)));     //rysowanie poprzedniej trasy po wspolrzednych
        this.duration.setText("Długość trwania: "+timeOfActivity + " minut");
        this.avgSpeed.setText("Średnia prędkość: "+avgSpeedLastTour + " km/h");
        this.maxSpeed.setText("Najwyższa prędkość: "+maxSpeedLastTour + " km/h");
        this.kcal.setText("Spalone kalorie: "+loseKcal + " kcal");
        return view;
    }

    private void setToLastRoute(Waypoint start, Waypoint end){
        RoutingEngine routingEngine;
        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }

        Waypoint startWaypoint = start;
        Waypoint destinationWaypoint = end;

        List<Waypoint> waypoints =
                new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

        routingEngine.calculateRoute(
                waypoints,
                new BicycleOptions(),
                new CalculateRouteCallback() {
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                        if (routingError == null) {
                            Route route = routes.get(0);
                            GeoPolyline routeGeoPolyline = route.getGeometry();
                            float widthInPixels = 10;
                            MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline,
                                    widthInPixels,
                                    Color.valueOf(0,1,0)); // RGBA

                            mapView.getMapScene().addMapPolyline(routeMapPolyline);
//                            long estimatedTravelTimeInSeconds = route.getDuration().getSeconds();     //przewidywany czas
                            int routeLengthInMeters = route.getLengthInMeters();    //mierzy odleglosc
                            distance.setText("Odległość: " + routeLengthInMeters/1000.0 + " km");
                            Log.d("wyliczona odleglosc: ", routeLengthInMeters+"");
                            loadMapScene(routeLengthInMeters);
                        }
                    }
                });
    }

    private void loadMapScene(int routeLengthInMeters) {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, routeLengthInMeters);
                mapView.getCamera().lookAt(
                        new GeoCoordinates((lastTourStartV1+lastTourEndV1)/2, (lastTourStartV2+lastTourEndV2)/2), mapMeasureZoom);
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

//    @Override
//    public void onPause() {
//        mapView.onPause();
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        mapView.onResume();
//        super.onResume();
//    }
//
//    @Override
//    public void onDestroy() {
//        mapView.onDestroy();
//        super.onDestroy();
//    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        mapView.onSaveInstanceState(outState);
//        super.onSaveInstanceState(outState);
//    }
}