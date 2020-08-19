package com.vinayak.jobschedulardemo

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class MyJobService:JobService() {

    val TAG = MyJobService::class.java.canonicalName
    var isJobCancelled = false
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG,"Job is started.")
        doBackgroundWork(params)
        return true //true keep device awake, false release wakelock. Use true when you start a background thread. For small job
        //with in onStartJob use false.
    }

    private fun doBackgroundWork(params: JobParameters?) {

        Thread {
            for (i in 1..10){
                Log.d(TAG,"Job is running: $i")
                if(isJobCancelled) {
                    return@Thread
                }
                try {
                    Thread.sleep(1000)
                }
                catch (e:InterruptedException) {

                }
            }
            Log.d(TAG,"Job Finished Successfully.")
            jobFinished(params,false)
        }.start()
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG,"Job is cancelled before completion")
        isJobCancelled = true
        return true
    }
}