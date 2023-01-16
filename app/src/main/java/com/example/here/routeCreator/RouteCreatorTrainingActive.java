package com.example.here.routeCreator;

import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import com.example.here.LocationConverter;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

public class RouteCreatorTrainingActive extends RouteCreator{
    private LocationIndicator locationIndicator;

    public RouteCreatorTrainingActive(MapView mapView, LocationIndicator locationIndicator) {
        super(mapView);
        this.locationIndicator = locationIndicator;
    }

    @Override
    protected void loadMapScene() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                double distanceInMeters = 1000 * 10;
                MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);

                GeoCoordinates coordinates = this.currentWaypoints.get(currentWaypoints.size()-1).coordinates;
                Location pastLocation = new Location("");
                pastLocation.setLatitude(coordinates.latitude);
                pastLocation.setLongitude(coordinates.longitude);

//                locationIndicator = new LocationIndicator();
//                locationIndicator.setLocationIndicatorStyle(LocationIndicator.IndicatorStyle.PEDESTRIAN);
//                locationIndicator.updateLocation(
//                        LocationConverter.convertToHERE(pastLocation)
//                );
//
//                mapView.addLifecycleListener(locationIndicator);
                /*
                mapView.getCamera().lookAt(
                        new GeoCoordinates(pastLocation.getLatitude(), pastLocation.getLongitude()), mapMeasureZoom);*/
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }
}
