package com.example.here.routeCreator;

import android.util.Log;
import android.widget.TextView;

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
import com.here.sdk.routing.PedestrianOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.util.List;

public abstract class RouteCreator {

    protected MapView mapView;
    protected List<Waypoint> currentWaypoints;
    protected int currentRouteLength = 0;

    public RouteCreator(MapView mapView) {
        this.mapView = mapView;
    }

    public void createRoute(List<Waypoint> waypoints){

        if(waypoints.size() < 2)
            return;

        this.currentWaypoints = waypoints;

        RoutingEngine routingEngine;
        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }


        routingEngine.calculateRoute(
                waypoints,
                new PedestrianOptions(),
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
                            loadMapScene();
                        }
                    }
                });
    }

    protected abstract void loadMapScene();

}
