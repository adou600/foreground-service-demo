# Android demo: Foreground Service of Type location

This ONLY purpose of this project is to demonstrate a problematic behaviour of Foreground Services of type location on Android 11 Preview.

An APK is available under `app/build/outputs/apk/debug/app-debug.apk`

## Problematic scenario

1. Install the app on Android 11 Preview
2. Open the app
3. In the location permission popup, choose "Only this time"
4. Tap the "start" button (logcat will show logs from the `PositionMonitor` about received locations)
5. Tap the home button (app should not be visible anymore, foreground service notification is still visible, locations are still received)
6. Run the following command to simulate the service being killed and restarted: `adb shell ps | grep com.example.foregroundservicedemo | awk '{print $2}' | xargs adb shell run-as com.example.foregroundservicedemo kill`

### EXPECTED behaviour

* Service is restarted.
* Location permissions are still granted because the service was not stopped by the user but by the system.
* Location udpdates are received.

### CURRENT behaviour

* Service is restarted.
* Location permissions are not granted anymore. See logcat: `PositionMonitor: start() ACCESS_FINE_LOCATION:false - ACCESS_COARSE_LOCATION:false`
* Location updates are notreceived.

### Note

If at step #3, the option "While using the app" is selected by the user, it works with the EXPECTED behaviour: permissions are granted and location updates received.