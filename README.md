# react-native-here-map

A native module to use Here Maps Lite with React Native

## Getting started

`$ npm install https://github.com/marcosantoniofilho16/react-native-here-map/tarball/master --save`

`$ yarn add https://github.com/marcosantoniofilho16/react-native-here-map/tarball/master`

### Mostly automatic installation

`$ react-native link react-native-here-map`

## Install Android

Since the HERE SDK encourages the use of Lambda expressions, Java 8 is required. Add the following inside the android closure of the app level's build.gradle file to desugar the output of the javac compiler:

In app/build.gradle add:

```
android {
  ...
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
  ...
}
```

In android/build.gradle set:

```
  def DEFAULT_MIN_SDK_VERSION = 21 <- Next to "Minimum API level", set API 21 as the minimum Android SDK.
```

In AndroidManifest.xml add:

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />


In MainApplication.java :

```
import com.here.map.HereMapPackage;

protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
            new HereMapPackage() // <------ Add this line
      );
    }
    
```

## Here Maps license
Go to [HERE website](https://developer.here.com/develop/mobile-sdks) and create your license key.


Then, open AndroidManifest.xml and update this section with your license.

```
    <!-- HEREMaps -->
    <meta-data 
        android:name="com.here.sdk.access_key_id" 
        android:value="YOUR-ACCESS-KEY-ID" />
    
    <meta-data 
        android:name="com.here.sdk.access_key_secret" 
        android:value="YOUR-ACCESS-KEY-SECRET" />
```

## Usage
```javascript
import React, { Component } from 'react'
import MapView from 'react-native-here-map'

export default class App extends Component {

  constructor(props) {
    super(props);
    this.state = {
        zoom: 14,
        route: [
          [13.37409, 52.53032],
          [13.3946, 52.5309],
          [13.39194, 52.53894],
          [13.37958, 52.54014]
        ],
        tilt: 90,
        center: [13.37409, 52.53032],
        location: [13.37409, 52.53032]
    }
  }

  render() {
    const { zoom, route, location, tilt, center } = this.state

    return (
      <MapView style={{flex: 1}} 
               zoom={zoom}
               tilt={tilt} 
               center={center}
               location={location}
               route={route} />
    )
  }

}

```
