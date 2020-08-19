package com.vinayak.jobschedulardemo

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.app.JobIntentService


class MyJobIntentService: JobIntentService() {

    companion object {
        private const val TAG = "MyJobIntentService"
        fun enqueueWork(context: Context, serviceIntent: Intent) {
            enqueueWork(context,MyJobIntentService::class.java,345,serviceIntent)
        }
    }

    override fun onCreate() {
        Log.d(TAG,"onCreate")
        super.onCreate()
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG,"onHandleWork")
        val input = intent.getStringExtra("inputExtra")
        for (i in 0..19) {
            Log.d(TAG, "$input - $i")
            if (isStopped) return
            SystemClock.sleep(1000)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onStopCurrentWork(): Boolean {
        Log.d(TAG, "onStopCurrentWork")
        return super.onStopCurrentWork()
    }
}