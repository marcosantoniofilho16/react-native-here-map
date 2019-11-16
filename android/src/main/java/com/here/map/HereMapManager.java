package com.here.map;

import android.util.Log;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.here.sdk.core.GeoCoordinates;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;

public class HereMapManager extends SimpleViewManager<HereMapView> {

    private static final int COMMAND_ZOOM_IN = 0;
    private static final int COMMAND_ZOOM_OUT = 1;
    private static final int COMMAND_SET_ZOOM = 2;
    private static final int COMMAND_SET_TILT = 3;
    private static final int COMMAND_SET_ROUTE = 4;
    private static final int COMMAND_SET_LOCATION = 5;
    private static final int COMMAND_ANIMATE_TO_COORDINATE = 6;
    private static final int COMMAND_ANIMATE_TO_BEARING = 7;

    private static final String TAG = "ReactNative";

    @Override
    public String getName() {
        return "HereMapView";
    }

    @Override
    protected HereMapView createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new HereMapView(reactContext);
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        Map<String, Integer> commandsMap = new HashMap<>();

        commandsMap.put("zoomIn", COMMAND_ZOOM_IN);
        commandsMap.put("zoomOut", COMMAND_ZOOM_OUT);
        commandsMap.put("setZoom", COMMAND_SET_ZOOM);
        commandsMap.put("setTilt", COMMAND_SET_TILT);
        commandsMap.put("setRoute", COMMAND_SET_ROUTE);
        commandsMap.put("setLocation", COMMAND_SET_LOCATION);
        commandsMap.put("animateToCoordinate", COMMAND_ANIMATE_TO_COORDINATE);
        commandsMap.put("animateToBearing", COMMAND_ANIMATE_TO_BEARING);

        return commandsMap;
    }

    @Override
    public void receiveCommand(HereMapView view,
                               int commandType,
                               @Nullable ReadableArray args) {

        Assertions.assertNotNull(view);
        Assertions.assertNotNull(args);
        Log.d(TAG,"commandType " + commandType);

        if (COMMAND_ZOOM_IN  == commandType) {
            if (view.getZoomLevel() < 20) {
                view.setZoomLevel(view.getZoomLevel() + 1);
            }
        }
        else if (COMMAND_ZOOM_OUT  == commandType) {
            if (view.getZoomLevel() > 0) {
                view.setZoomLevel(view.getZoomLevel() - 1);
            }
        }
        else if (COMMAND_SET_ZOOM  == commandType) {
            double zoomLevel = args.getDouble(0);
            view.setZoomLevel(zoomLevel);
        }
        else if (COMMAND_SET_TILT  == commandType) {
            double tilt = args.getDouble(0);
            view.setTilt(tilt);
        }
        else if (COMMAND_SET_ROUTE  == commandType) {
            ReadableArray coordinates = args.getArray(0);
            view.setRoute(coordinates);
        }
        else if (COMMAND_SET_LOCATION  == commandType) {
            ReadableArray location = args.getArray(0);
            view.setLocation(new GeoCoordinates(location.getDouble(1), location.getDouble(0)));
        }
        else if (COMMAND_ANIMATE_TO_COORDINATE  == commandType) {
            ReadableArray coordinate = args.getArray(0);
            GeoCoordinates geoCoordinates = new GeoCoordinates(coordinate.getDouble(1), coordinate.getDouble(0));
            view.setLocation(geoCoordinates);
            view.setCenter(geoCoordinates);
        }
        else if (COMMAND_ANIMATE_TO_BEARING  == commandType) {
            double bearing = args.getDouble(0);
            view.setBearing(bearing);
        }
        else {
            throw new IllegalArgumentException(String.format(
                        "Unsupported command %d received by %s.",
                        commandType,
                        getClass().getSimpleName()));
        }

    }

    @ReactProp(name = "center")
    public void setCenter(HereMapView view, @Nullable ReadableArray center) {
        view.setCenter(new GeoCoordinates(center.getDouble(1), center.getDouble(0)));
    }

    @ReactProp(name = "bearing", defaultDouble = 0)
    public void setBearing(HereMapView view, double bearing) {
        view.setBearing(bearing);
    }

    @ReactProp(name = "tilt", defaultDouble = 0)
    public void setTilt(HereMapView view, double tilt) {
        view.setTilt(tilt);
    }

    @ReactProp(name = "zoom", defaultDouble = 1)
    public void setZoomLevel(HereMapView view, double zoomLevel) {
        view.setZoomLevel(zoomLevel);
    }

    @ReactProp(name = "route")
    public void setRoute(HereMapView view, @Nullable ReadableArray coordinates) {
        view.setRoute(coordinates);
    }

    @ReactProp(name = "location")
    public void setLocation(HereMapView view, @Nullable ReadableArray location) {
        view.setLocation(new GeoCoordinates(location.getDouble(1), location.getDouble(0)));
    }

}
