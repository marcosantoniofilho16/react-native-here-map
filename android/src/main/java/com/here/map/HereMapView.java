package com.here.map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.here.map.R;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapviewlite.CameraObserver;
import com.here.sdk.mapviewlite.CameraUpdate;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapPolyline;
import com.here.sdk.mapviewlite.MapPolylineStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PixelFormat;
import com.here.sdk.mapviewlite.SceneError;

import java.util.ArrayList;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;

public class HereMapView extends MapViewLite {

    private static final String TAG = "ReactNative";

    private GeoCoordinates center = new GeoCoordinates(-4.5607442091831984, -397.76870042961644);

    private double bearing = 0;

    private double tilt = 0;

    private double zoomLevel = 1;

    private MapPolyline mapPolyline;

    private MapMarker mapMarker;

    public HereMapView(Context context) {
        super(context);

        Bundle initialProps = new Bundle();
        this.onCreate(initialProps);

        this.getMapScene().loadScene(MapStyle.NORMAL_DAY, (@Nullable SceneError sceneError) -> {
            Log.d(TAG,"loadScene");
            if (sceneError == null) {
                this.getCamera().setTarget(center);
                this.getCamera().setBearing(bearing);
                this.getCamera().setTilt(tilt);
                this.getCamera().setZoomLevel(zoomLevel);
                this.getCamera().addObserver(cameraObserver);
            }
        });

    }

    private final CameraObserver cameraObserver = (@NonNull CameraUpdate cameraUpdate) -> {
        GeoCoordinates center = cameraUpdate.target;
        double bearing = cameraUpdate.bearing;
        double tilt = cameraUpdate.tilt;
        double zoomLevel = cameraUpdate.zoomLevel;

        if (this.center != center) this.center = center;
        if (this.bearing != bearing) this.bearing = bearing;
        if (this.tilt != tilt) this.tilt = tilt;
        if (this.zoomLevel != zoomLevel) this.zoomLevel = zoomLevel;
    };

    public MapPolyline createPolyline(ReadableArray coordinates) {
        ArrayList<GeoCoordinates> route = new ArrayList<>();

        for(int i = 0; i < coordinates.size(); i++) {
            ReadableArray coordinate = coordinates.getArray(i);
            route.add(new GeoCoordinates(coordinate.getDouble(1), coordinate.getDouble(0)));
        }

        GeoPolyline geoPolyline;
        try {
            geoPolyline = new GeoPolyline(route);
        } catch (InstantiationErrorException e) {
            // Less than two vertices.
            return null;
        }

        MapPolylineStyle mapPolylineStyle = new MapPolylineStyle();
        mapPolylineStyle.setWidth(20);
        mapPolylineStyle.setColor(0x01E9FFA0, PixelFormat.RGBA_8888);
        MapPolyline mapPolyline = new MapPolyline(geoPolyline, mapPolylineStyle);

        return mapPolyline;
    }

    public GeoCoordinates getCenter() {
        return center;
    }

    public void setRoute(ReadableArray coordinates) {
        MapScene mapScene = this.getMapScene();

        if (mapPolyline != null)
            mapScene.removeMapPolyline(mapPolyline);

        mapPolyline = this.createPolyline(coordinates);
        mapScene.addMapPolyline(mapPolyline);
    }

    public void setCenter(GeoCoordinates center) {
        this.getCamera().setTarget(center);
        this.center = center;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.getCamera().setBearing(bearing);
        this.bearing = bearing;
    }

    public double getTilt() {
        return tilt;
    }

    public void setTilt(double tilt) {
        this.getCamera().setTilt(tilt);
        this.tilt = tilt;
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.getCamera().setZoomLevel(zoomLevel);
        this.zoomLevel = zoomLevel;
    }

    public CameraObserver getCameraObserver() {
        return cameraObserver;
    }

    public void setLocation(GeoCoordinates location) {
        if (mapMarker != null) {
            mapMarker.setCoordinates(location);
            return;
        }

        MapImage mapImage = MapImageFactory.fromResource(this.getResources(), R.drawable.navigation);

        mapMarker = new MapMarker(location);

        MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
        mapMarkerImageStyle.setScale(0.20f);
        mapMarker.addImage(mapImage, mapMarkerImageStyle);

        MapScene mapScene = this.getMapScene();
        mapScene.addMapMarker(mapMarker);
    }
}
