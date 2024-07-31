package com.amtech.tlismiherbs.notification

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import com.google.android.datatransport.BuildConfig
import java.util.Date
import java.util.Locale

// NotificationListener.java
class NotificationListener : NotificationListenerService() {
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        Log.d("NotificationListener", "onNotificationRemoved: Notification removed")
        // Check if the removed notification is the one you want to handle
        if (sbn.packageName == BuildConfig.VERSION_NAME) {
            Log.d("NotificationListener", "Stopping MusicService")
            // Stop the MusicService
//            Toast.makeText(this, "ifff", Toast.LENGTH_SHORT).show()
//            val soundServiceIntent = Intent(this, SoundService::class.java)
//            stopService(soundServiceIntent)
        } else {
            Log.d("notification", "else")
            // Toast.makeText(this, "else", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        val packageName = sbn.packageName
        var notificationTitle = ""
        var notificationText = ""
        if (sbn.notification.extras != null) {
            // Extract notification title and text
            notificationTitle = sbn.notification.extras.getString("android.title", "")
            notificationText =
                sbn.notification.extras.getCharSequence("android.text", "").toString()
        }


        // Log the notification details
        Log.i("NotificationListener", "Package: $packageName")
        Log.i("NotificationListener", "Title: $notificationTitle")
        Log.i("NotificationListener", "Text: $notificationText")
        var currentDate: String =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date()
            )


    }
    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i("NotificationListener", "Package: $packageName")
//        Log.i("NotificationListener", "Title: $notificationTitle")
//        Log.i("NotificationListener", "Text: $notificationText")
    }
}
