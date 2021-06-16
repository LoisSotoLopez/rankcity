package com.apm2021.rankcity


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.Chronometer
import android.widget.RemoteViews
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat


class RouteService : Service() {

    lateinit var time: String

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        val chrono = intent.getLongExtra("chronometer", 0)
        createNotificationChannel()
        val notificationIntent = Intent(this, MapsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
//        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val layout: View = inflater.inflate(R.layout.notification_service, null)
//        val chronometer = layout.findViewById<Chronometer>(R.id.chronometer_notification)
//        chronometer.start()
        val remoteViews = RemoteViews(packageName,R.layout.notification_service)
        remoteViews.setChronometer(R.id.chronometer_notification, chrono, null, true)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Ruta en progreso")
//            .setContent(RemoteViews(packageName,R.layout.notification_service))
            .setCustomContentView(remoteViews)
//            .setContentText(chrono)
            .setSmallIcon(R.drawable.rankcity_logo)
            .setContentIntent(pendingIntent)
//            .setUsesChronometer(true)
//            .setChronometerCountDown(true)
            .build()
        startForeground(1, notification)

        //do heavy work on a background thread


        //stopSelf();
        return START_NOT_STICKY
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
    }

}