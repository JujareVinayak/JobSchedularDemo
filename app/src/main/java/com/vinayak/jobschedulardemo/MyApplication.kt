package com.vinayak.jobschedulardemo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var notificationServiceChannel1 = NotificationChannel(CHANNEL_ID1,"My Service Channel1",NotificationManager.IMPORTANCE_DEFAULT)
            var notificationServiceChannel2 = NotificationChannel(CHANNEL_ID2,"My Service Channel2",NotificationManager.IMPORTANCE_DEFAULT)
            var notificationServiceChannel3 = NotificationChannel(CHANNEL_ID3,"My Service Channel3",NotificationManager.IMPORTANCE_DEFAULT)
            var notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationServiceChannel1)
            notificationManager.createNotificationChannel(notificationServiceChannel2)
            notificationManager.createNotificationChannel(notificationServiceChannel3)
        }
    }

    companion object {
        const val CHANNEL_ID1 = "MyServiceChannel1"
        const val CHANNEL_ID2 = "MyServiceChannel2"
        const val CHANNEL_ID3 = "MyServiceChannel3"
    }
}