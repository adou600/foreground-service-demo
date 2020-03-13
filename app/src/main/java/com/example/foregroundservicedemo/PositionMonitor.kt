package com.example.foregroundservicedemo

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.util.Log
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

class PositionMonitor(private val context: Context) {

    companion object {
        const val TAG = "PositionMonitor"
    }

    private var fusedLocationCallback: LocationCallback? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private val fusedLocationCallBack: LocationCallback
        get() {
            if (fusedLocationCallback == null) {
                fusedLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
                        Log.d(
                            TAG,
                            "Location received with accuracy ${locationResult?.lastLocation?.accuracy}"
                        )
                    }
                }
            }
            return fusedLocationCallback as LocationCallback
        }


    fun start() {
        val hasFineLocationPermission = hasPermission(ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = hasPermission(ACCESS_COARSE_LOCATION)

        Log.d(
            TAG,
            "start() ACCESS_FINE_LOCATION:$hasFineLocationPermission - ACCESS_COARSE_LOCATION:$hasCoarseLocationPermission"
        )

        if (hasCoarseLocationPermission && hasCoarseLocationPermission) {
            val locationRequest = createLocationRequest()

            val request = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build()

            val settingsClient = LocationServices.getSettingsClient(context)
            val settingsResponseTask = settingsClient!!.checkLocationSettings(request)

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

            settingsResponseTask.addOnSuccessListener {
                fusedLocationProviderClient!!
                    .requestLocationUpdates(locationRequest, fusedLocationCallBack, null)
                    .addOnFailureListener {
                        Log.e(TAG, it.message, it)
                    }

            }
            settingsResponseTask.addOnFailureListener {
                Log.e(TAG, it.message, it)
            }

        }
    }

    private fun hasPermission(permission: String) =
        checkSelfPermission(context, permission) == PERMISSION_GRANTED

    fun stop() {
        Log.d(TAG, "stop")

        fusedLocationProviderClient?.removeLocationUpdates(fusedLocationCallback!!)
        fusedLocationCallback = null
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = 2000L
        locationRequest.fastestInterval = 2000L
        locationRequest.smallestDisplacement = 0.0f
        locationRequest.priority = PRIORITY_HIGH_ACCURACY
        return locationRequest
    }

}