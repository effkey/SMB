package com.example.here;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.here.routeCreator.RouteCreator;
import com.example.here.routeCreator.RouteCreatorTrainingSuspended;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.Waypoint;

import java.util.Arrays;

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

    private RouteCreator routeCreator;

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

        this.routeCreator = new RouteCreatorTrainingSuspended(mapView, distance);
        routeCreator.createRoute(Arrays.asList(
                new Waypoint(new GeoCoordinates(lastTourStartV1, lastTourStartV2)),
                new Waypoint(new GeoCoordinates(lastTourEndV1, lastTourEndV2))
        ));


        this.duration.setText("Długość trwania: "+timeOfActivity + " minut");
        this.avgSpeed.setText("Średnia prędkość: "+avgSpeedLastTour + " km/h");
        this.maxSpeed.setText("Najwyższa prędkość: "+maxSpeedLastTour + " km/h");
        this.kcal.setText("Spalone kalorie: "+loseKcal + " kcal");
        return view;
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