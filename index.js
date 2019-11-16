import React, {
  Component
} from 'react'
import {
  StyleSheet,
  NativeModules,
  requireNativeComponent,
  findNodeHandle
} from 'react-native'

const UIManager = NativeModules.UIManager
const HereMapView = requireNativeComponent("HereMapView") 

export default class MapView extends Component {

  render() {
    const { style, 
            zoom, 
            center, 
            bearing, 
            tilt, 
            route, 
            location, 
            children 
          } = this.props

    return ( 
      <>
        <HereMapView style={[styles.container, style]}
                    zoom={zoom}
                    center={center}
                    location={location}
                    bearing={bearing}
                    route={route}
                    tilt={tilt} />
        { children }
      </>
      
    )
  }

  setZoom(zoom) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.setZoom,
      [zoom])
  }

  zoomIn() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.zoomIn,
      [])
  }

  zoomOut() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.zoomOut,
      [])
  }

  setTilt(tilt) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.setTilt,
      [tilt])
  }

  setRoute(route) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.setRoute,
      [route])
  }

  setLocation(location) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.setLocation,
      [location])
  }

  animateToCoordinate(coordinate) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.animateToCoordinate,
      [coordinate])
  }

  animateToBearing(bearing) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.animateToBearing,
      [bearing])
  }

}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
});