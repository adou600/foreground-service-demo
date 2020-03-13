package com.example.foregroundservicedemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class TrackingService : Service() {

    companion object {
        const val TAG = "TrackingService"
        const val NOTIFICATION_CUSTOM = "custom_notification"
        const val TRACKING_NOTIFICATION_ID = 134231
        const val CHANNEL_ID = "TrackingChannelId"
    }

    private val binder = TrackingServiceBinder(this)
    private var positionMonitor: PositionMonitor? = null

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")

        if (positionMonitor == null) {
            positionMonitor = PositionMonitor(applicationContext)
        }
        positionMonitor?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
        positionMonitor?.stop()
        positionMonitor = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        var notification: Notification? = null
        if (intent != null) {
            notification = intent.getParcelableExtra(NOTIFICATION_CUSTOM)
        }
        if (notification == null) {
            notification = buildDefaultNotification()
        }

        startForeground(TRACKING_NOTIFICATION_ID, notification)
        return START_STICKY
    }

    private fun buildDefaultNotification(): Notification? {
        val icon: Int = R.drawable.ic_launcher_foreground
        val title = getString(R.string.notification_title)
        val message = getString(R.string.notification_message)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setOngoing(true)

        val channelName: String = applicationContext.getString(R.string.notification_title)
        val channel =
            NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return notificationBuilder.build()
    }

    class TrackingServiceBinder(val trackingService: TrackingService) : Binder()
}
