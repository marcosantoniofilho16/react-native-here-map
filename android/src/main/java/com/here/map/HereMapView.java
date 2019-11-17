package com.here.map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.here.map.R;
import com.here.sdk.core.Angle;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.GestureState;
import com.here.sdk.gestures.PanListener;
import com.here.sdk.gestures.PinchRotateListener;
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

    private GeoCoordinates location;

    private double bearing = 0;

    private double tilt = 0;

    private double zoomLevel = 1;

    private MapPolyline mapPolyline;

    private MapMarker mapMarker;

    private Boolean isCentralize = true;

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

                this.getGestures().setPanListener(panListener);
                this.getGestures().setPinchRotateListener(pinchRotateListener);

                WritableMap params = Arguments.createMap();
                params.putBoolean("isLoaded", true);
                sendEvent("onLoad", params);
            }
        });

    }

    private final PinchRotateListener pinchRotateListener = (@NonNull
                                                                     GestureState state,
                                                             @NonNull
                                                                     Point2D pinchOrigin,
                                                             @NonNull
                                                                     Point2D rotationOrigin,
                                                             float twoFingerDistance,
                                                             @NonNull
                                                                     Angle rotation) -> {

        GeoCoordinates pinchOriginGeo = this.getCamera().viewToGeoCoordinates(pinchOrigin);
        GeoCoordinates rotationOriginGeo = this.getCamera().viewToGeoCoordinates(rotationOrigin);

        WritableMap pinchOriginMap = Arguments.createMap();
        pinchOriginMap.putDouble("latitude", pinchOriginGeo.latitude);
        pinchOriginMap.putDouble("longitude", pinchOriginGeo.longitude);

        WritableMap rotationOriginMap = Arguments.createMap();
        rotationOriginMap.putDouble("latitude", rotationOriginGeo.latitude);
        rotationOriginMap.putDouble("longitude", rotationOriginGeo.longitude);

        WritableMap params = Arguments.createMap();
        params.putString("state", state.name());
        params.putMap("pinchOrigin", pinchOriginMap);
        params.putMap("rotationOrigin", rotationOriginMap);
        params.putDouble("twoFingerDistance", twoFingerDistance);
        params.putDouble("rotation", rotation.getDegrees());
        sendEvent("onRotate", params);

        setCentralize(false);
    };

    private final PanListener panListener = (@NonNull
                                                     GestureState state,
                                             @NonNull
                                                     Point2D origin,
                                             @NonNull
                                                     Point2D translation,
                                             float velocity) -> {
        GeoCoordinates originGeo = this.getCamera().viewToGeoCoordinates(origin);
        GeoCoordinates translationGeo = this.getCamera().viewToGeoCoordinates(translation);

        WritableMap originMap = Arguments.createMap();
        originMap.putDouble("latitude", originGeo.latitude);
        originMap.putDouble("longitude", originGeo.longitude);

        WritableMap translationMap = Arguments.createMap();
        translationMap.putDouble("latitude", translationGeo.latitude);
        translationMap.putDouble("longitude", translationGeo.longitude);

        WritableMap params = Arguments.createMap();
        params.putString("state", state.name());
        params.putMap("origin", originMap);
        params.putMap("translation", translationMap);
        params.putDouble("velocity", velocity);
        sendEvent("onPan", params);

        setCentralize(false);
    };

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

    private void sendEvent(String eventName,
                           @Nullable WritableMap params) {
        ReactContext reactContext = (ReactContext) this.getContext();
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
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

        this.location = location;
    }

    public Boolean isCentralize() {
        return isCentralize;
    }

    public void setCentralize(Boolean centralize) {
        WritableMap params = Arguments.createMap();
        params.putBoolean("recentralize", !centralize);
        sendEvent("onReCentralize", params);    

        this.isCentralize = centralize;
    }

    public GeoCoordinates getLocation() {
        return location;
    }

}
