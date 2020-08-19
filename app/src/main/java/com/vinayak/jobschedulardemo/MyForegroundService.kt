package com.vinayak.jobschedulardemo

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vinayak.jobschedulardemo.MyApplication.Companion.CHANNEL_ID1


class MyForegroundService:Service() {

    private var keepRunning: Boolean = true
    val TAG = MyForegroundService::class.java.canonicalName
    lateinit var thread: Thread
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val inputMessage = intent.getStringExtra("inputExtra")
        val notificationIntent = Intent(this,MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this,0,notificationIntent,0   )
        var notification = NotificationCompat.Builder(this,CHANNEL_ID1).setContentTitle("My Foreground Service").
        setContentText(inputMessage).setSmallIcon(R.drawable.ic_android).setContentIntent(pIntent).setProgress(100,0,false).setNotificationSilent()
        startForeground(1,notification.build()) // If not used service will be destoryed by System OS automatically after some time,
        // say 1 minute for  startService(serviceIntent) and 5 to 10 seconds for ContextCompat.startForegroundService(this,serviceIntent)
        doBackgroundWork(notification)
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy")
        keepRunning = false
        Toast.makeText(this,"Download got interrupted",Toast.LENGTH_SHORT).show()
    }

    /**
     * Do some heavy background work in the Thread, as service run on UI Thread. Once work done call stopSelf() to stop service by itself.
     */
    private fun doBackgroundWork(builder: NotificationCompat.Builder) {

        thread = Thread {
            for (i in 1..10) {
                if (keepRunning) {
                    builder.setProgress(100, i * 10, false)
                    var notificationManager = NotificationManagerCompat.from(this) //getSystemService(NotificationManager::class.java)
                    notificationManager.notify(1, builder.build())
                    Log.d(TAG, "Download in progress: ${i*10}")
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {

                    }
                    continue
                }
                return@Thread
            }
            stopSelf() //Comment this to keep foreground service running.
            Log.d(TAG,"Background work is finished successfully.")
        }
        thread.start()
    }
}