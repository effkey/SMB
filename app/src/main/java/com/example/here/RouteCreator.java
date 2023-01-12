package com.example.here;

import android.util.Log;

import androidx.annotation.Nullable;

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

import java.util.List;

public class RouteCreator {

    private MapView mapView;
    private List<Waypoint> currentWaypoints;
    private int currentRouteLength = 0;

    public RouteCreator(MapView mapView) {
        this.mapView = mapView;
    }

    public void createRoute(List<Waypoint> waypoints){

        this.currentWaypoints = waypoints;

        RoutingEngine routingEngine;
        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }

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
                            currentRouteLength = route.getLengthInMeters();
                            Log.d("DIST", "DISTANCE: " + currentRouteLength+"");
                            loadMapSceneTrainingSuspended();
                        }
                    }
                });
    }

    private void loadMapSceneTrainingSuspended() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, this.currentRouteLength);
                double lastTourStartV1 = this.currentWaypoints.get(0).coordinates.latitude;
                double lastTourEndV1 = this.currentWaypoints.get(currentWaypoints.size()-1).coordinates.latitude;
                double lastTourStartV2 = this.currentWaypoints.get(0).coordinates.longitude;
                double lastTourEndV2 = this.currentWaypoints.get(currentWaypoints.size()-1).coordinates.longitude;
                mapView.getCamera().lookAt(
                        new GeoCoordinates((lastTourStartV1+lastTourEndV1)/2, (lastTourStartV2+lastTourEndV2)/2), mapMeasureZoom);
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

    public int getCurrentRouteLength() {
        return this.currentRouteLength;
    }


}
