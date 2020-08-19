package com.vinayak.jobschedulardemo

import android.app.IntentService
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.SystemClock
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import com.vinayak.jobschedulardemo.MyApplication.Companion.CHANNEL_ID1

class MyIntentService : IntentService("ExampleIntentService") {
    private var wakeLock: WakeLock? = null
    private var keepRunning: Boolean = true
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        var powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ExampleApp:Wakelock"
        )
        wakeLock!!.acquire()
        Log.d(TAG, "Wakelock acquired")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID1)
                .setContentTitle("Example IntentService")
                .setContentText("Running...")
                .build()
            startForeground(1, notification)
        }
    }

    override fun onHandleIntent(@Nullable intent: Intent?) {
        Log.d(TAG, "onHandleIntent")
        val input = intent!!.getStringExtra("inputExtra")
        for (i in 0..19) {
            Log.d(TAG, "$input - $i")
            if(keepRunning) return
            SystemClock.sleep(1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        wakeLock!!.release()
        keepRunning = false
        Log.d(TAG, "Wakelock released")
    }

    companion object {
        private const val TAG = "MyIntentService"
    }

    init {
        setIntentRedelivery(false)
    }
}