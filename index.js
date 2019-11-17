import React, {
  Component
} from 'react'
import {
  StyleSheet,
  NativeModules,
  requireNativeComponent,
  findNodeHandle,
  NativeEventEmitter
} from 'react-native'

const UIManager = NativeModules.UIManager
const HereMapView = requireNativeComponent("HereMapView") 

export default class MapView extends Component {

  static defaultProps = {
    onLoad: () => {},
    onPan: () => {},
    onRotate: () => {},
    onReCentralize: () => {}
  }

  constructor(props) {
    super(props)
    this.state = {
      isLoaded: false
    }
  }

  componentDidMount() {
    const eventEmitter = new NativeEventEmitter(HereMapView);
    eventEmitter.addListener('onLoad', (event) => {
       this.setState(event)
       this.props.onLoad(event)
    })
    eventEmitter.addListener('onPan', (event) => {
      this.props.onPan(event)
    })
    eventEmitter.addListener('onRotate', (event) => {
      this.props.onRotate(event)
    })
    eventEmitter.addListener('onReCentralize', (event) => {
      this.props.onReCentralize(event)
    })
  }

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
    const { isLoaded } = this.state

    return ( 
      <>
        <HereMapView style={[styles.container, style]}
                    zoom={zoom}
                    center={center}
                    location={location}
                    bearing={bearing}
                    route={route}
                    tilt={tilt} />
        { isLoaded ? children : undefined }
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

  setCenter(location) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.setCenter,
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

  centralize() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.HereMapView.Commands.centralize,
      [])
  }

}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
});