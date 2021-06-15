package com.apm2021.rankcity


import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.widget.Chronometer
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat


class RouteService : Service() {

//    private lateinit var chronometer: Chronometer
//    var pauseOffSet: Long = 0
    lateinit var time: String

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        val chrono = intent.getStringExtra("chronometer")
        createNotificationChannel()
        val notificationIntent = Intent(this, MapsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Ruta en progreso")
            .setContentText(chrono)
            .setSmallIcon(R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        //do heavy work on a background thread


        //stopSelf();
        return START_NOT_STICKY
    }

//    private fun startChronometer() {
//        chronometer.base = SystemClock.elapsedRealtime() - pauseOffSet
//        chronometer.start()
//    }
//
//    private fun stopChronometer() {
//        chronometer.stop()
//        pauseOffSet = SystemClock.elapsedRealtime() - chronometer.base
//    }

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