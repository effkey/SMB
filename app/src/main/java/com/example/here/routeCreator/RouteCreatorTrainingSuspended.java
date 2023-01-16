package com.example.here.routeCreator;

import android.util.Log;
import android.widget.TextView;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

public class RouteCreatorTrainingSuspended extends RouteCreator{
    protected TextView distanceView = null;

    public RouteCreatorTrainingSuspended(MapView mapView) {
        super(mapView);
    }
    public RouteCreatorTrainingSuspended(MapView mapView, TextView distanceView) {
        super(mapView);
        this.distanceView = distanceView;
    }

    @Override
    protected void loadMapScene() {
        if(distanceView != null)
            distanceView.setText("Odległość: " + currentRouteLength/1000.0 + " km");
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
}
