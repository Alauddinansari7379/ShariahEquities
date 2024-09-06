package com.amtech.shariahEquities.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.sellacha.tlismiherbs.R
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val tag = "FirebaseMessagingService"
    lateinit var sessionManager: SessionManager
    private val FCM_TOKEN = "fcmtoken"
    private var fcmTokenNew = ""
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "MyPrefs"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("$tag token --> $token")
        sessionManager = SessionManager(this@MyFirebaseMessagingService)
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        saveFCM(token)
        fcmTokenNew = sharedPreferences.getString(FCM_TOKEN, "").toString()

        sessionManager.fcmToken = fcmTokenNew

        //  sessionManager.fcmToken=token
        //    Toast.makeText(this,"token",Toast.LENGTH_LONG).show()
        Log.e("FCMToken", token)
    }

    private fun saveFCM(fcmToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString(FCM_TOKEN, fcmToken)
        editor.apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            if (remoteMessage.notification != null) {
//                val soundServiceIntent = Intent(this, SoundService::class.java)
//                startService(soundServiceIntent)
                showNotification(
                    remoteMessage.notification?.title,
                    remoteMessage.notification?.body
                )
            }
            //
            //            else {
//                showNotification(remoteMessage.data["title"], remoteMessage.data["message"])
//            }

        } catch (e: Exception) {
            println("$tag error -->${e.localizedMessage}")
            Log.e("NotifyError", e.localizedMessage)
        }
    }

    private fun showNotification(
        title: String?,
        body: String?
    ) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT
//        )
        // val intent1 = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_MUTABLE)

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val channelId = getString(R.string.channel_id)
        val channelName = getString(R.string.channel_name)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannels(channelId, channelName, notificationManager)
        }

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSilent(false)
            .setOngoing(false)
            // .addAction(R.drawable.logo, "Delete", pIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
        notificationManager.notify(0, notificationBuilder.build())

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels(
        channelId: String,
        channelName: String,
        notificationManager: NotificationManager
    ) {

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }

}





