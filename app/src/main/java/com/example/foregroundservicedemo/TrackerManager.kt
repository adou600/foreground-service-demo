package com.example.foregroundservicedemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

object TrackerManager : ServiceConnection {

    private const val TAG = "PositionMonitor"

    private var trackingService: TrackingService? = null
    private var isBoundToTrackingService: Boolean = false

    fun getTrackingService(): TrackingService? {
        return trackingService
    }

    fun stopTrackingService(context: Context?) {
        Log.d(TAG, "stopTrackingService isBoundToTrackingService=$isBoundToTrackingService")

        val intent = Intent(context, TrackingService::class.java)
        if (isBoundToTrackingService) {
            context?.unbindService(this)
        }
        context?.stopService(intent)
        trackingService = null
    }

    fun startTrackingService(context: Context?) {
        Log.d(TAG, "startTrackingService isBoundToTrackingService=$isBoundToTrackingService")

        if (!isBoundToTrackingService) {
            val intent = Intent(context, TrackingService::class.java)
            context?.startForegroundService(intent)
            context?.bindService(
                intent,
                this,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d(TAG, "onServiceDisconnected")
        isBoundToTrackingService = false
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "onServiceConnected")
        isBoundToTrackingService = true
        val binder: TrackingService.TrackingServiceBinder =
            service as TrackingService.TrackingServiceBinder
        trackingService = binder.trackingService
    }

}