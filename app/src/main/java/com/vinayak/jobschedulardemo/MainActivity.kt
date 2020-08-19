package com.vinayak.jobschedulardemo

import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.canonicalName
    var jobId = 555
    lateinit var editText: EditText
    lateinit var notificationManager: NotificationManagerCompat

    var myBoundService:MyBoundService? = null
    var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.input_text)
        notificationManager = NotificationManagerCompat.from(this)
    }

    fun startTheJob(view: View) {
        var componentName = ComponentName(this, MyJobService::class.java)
        var jobInfo = JobInfo.Builder(jobId, componentName)
            .setRequiresCharging(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setPeriodic(15 * 1000 * 60) //as minimum periodic request is 15min.
            .build()
        var jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        var resultCode = jobScheduler.schedule(jobInfo)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled successfully")
        } else {
            Log.d(TAG, "Job scheduling failed")
        }
    }

    fun stopTheJob(view: View) {
        var jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(jobId)
        Log.d(TAG, "Job is cancelled before completion")
    }

    fun startForegroundService(view: View) {
        var inputText = editText.text.toString()
        var serviceIntent = Intent(this, MyForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", inputText)
        ContextCompat.startForegroundService(
            this,
            serviceIntent
        ) //Use this to start Service when app is in background.
        // To start service with an implicit promise that the Service will call startForeground(int id, Notification notification) for >=O.
        // If startForeground(int id, Notification notification) not started app will kill service with in 5 to 10 seconds.
        startService(serviceIntent) //To start service as a normal service before Android O versions. Use to start a service when app is opened.
    }

    fun stopForegroundService(view: View) {
        var serviceIntent = Intent(this, MyForegroundService::class.java)
        stopService(serviceIntent)
    }

    fun startIntentService(view: View) {
        var inputText = editText.text.toString()
        var serviceIntent = Intent(this, MyIntentService::class.java)
        serviceIntent.putExtra("inputExtra", inputText)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun enqueueWork(v: View?) {
        val input: String = editText.text.toString()
        val serviceIntent = Intent(this, MyJobIntentService::class.java)
        serviceIntent.putExtra("inputExtra", input)
        MyJobIntentService.enqueueWork(this, serviceIntent)
    }

    fun actionNotification(view: View) {
        var activityIntent = Intent(this, MainActivity::class.java)
        var contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)
        var broadcastIntent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("toastMessage", editText.text.toString())
        var actionIntent =
            PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, MyApplication.CHANNEL_ID1)
                .setContentTitle("My Foreground Service")
                .setContentText("Action Toast")
                .setSmallIcon(R.drawable.ic_android)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setProgress(100, 0, false)
                .setContentIntent(contentIntent)
                .setColor(Color.GREEN)
                .setAutoCancel(true) //When we tap on notification launches the activity given in content intent and clears itself.
                .addAction(R.drawable.ic_android, "Toast", actionIntent)
        var notification = builder.build()
        notificationManager.notify(1, notification)
    }

    fun textAndInboxStyle(view: View) {
        var activityIntent = Intent(this, MainActivity::class.java)
        var contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)
        var broadcastIntent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("toastMessage", editText.text.toString())
        var actionIntent =
            PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, MyApplication.CHANNEL_ID1)
                .setContentTitle("My Foreground Service")
                .setContentText("Action Toast")
                .setSmallIcon(R.drawable.ic_android)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setStyle(
                    NotificationCompat.BigTextStyle().setBigContentTitle("Iam Big Text title")
                        .bigText(
                            "Iam nfksfksfnssnfasnfnskk " +
                                    "long text dbsddjsbsbjkaskbscbskjcbsnkcbscsck" +
                                    "sncscaskjcskcsnkc ssncnsasnnbsnbscn sc s c"
                        ).setSummaryText("Summary")
                )
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setProgress(100, 0, false)
                .setContentIntent(contentIntent)
                .setColor(Color.GREEN)
                .setAutoCancel(true) //When we tap on notification launches the activity given in content intent and clears itself.
                .addAction(R.drawable.ic_android, "Toast", actionIntent)
        var notification = builder.build()
        //notificationManager.notify(1, notification)
        var builder2: NotificationCompat.Builder =
            NotificationCompat.Builder(this, MyApplication.CHANNEL_ID2)
                .setContentTitle("My Foreground Service")
                .setContentText("Action Toast")
                .setSmallIcon(R.drawable.ic_android)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setProgress(100, 0, false)
                .setColor(Color.GREEN)
                .setAutoCancel(true) //When we tap on notification launches the activity given in content intent and clears itself.
                .addAction(R.drawable.ic_android, "Toast", actionIntent)
        builder2.setStyle(NotificationCompat.InboxStyle().addLine("Line1").addLine("Line2").addLine("Line3").addLine("Line4").addLine("Line5"))
        var  notification2 = builder2.build()
      //  notificationManager.notify(2, notification2)
        var builder3: NotificationCompat.Builder =
            NotificationCompat.Builder(this, MyApplication.CHANNEL_ID3)
                .setContentTitle("My Foreground Service")
                .setContentText("Action Toast")
                .setSmallIcon(R.drawable.ic_android)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setProgress(100, 0, false)
                .setColor(Color.GREEN)
                .setAutoCancel(true) //When we tap on notification launches the activity given in content intent and clears itself.
                .addAction(R.drawable.ic_android, "Play", actionIntent)
                .addAction(R.drawable.ic_android, "Play", actionIntent)
                .addAction(R.drawable.ic_android, "Play", actionIntent)
                .addAction(R.drawable.ic_android, "Play", actionIntent)
                .addAction(R.drawable.ic_android, "Play", actionIntent)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
        var  notification3 = builder3.build()
        notificationManager.notify(3, notification3)
    }

    override fun onStart() {
        super.onStart()
        Log.d(MainActivity::class.java.canonicalName,"onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(MainActivity::class.java.canonicalName,"onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.d(MainActivity::class.java.canonicalName,"onStop")
    }

    private var boundServiceConnection = object :ServiceConnection{

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var binder = service as MyBoundService.MyBinder
            myBoundService = binder.service
            isBound = true
            val runnable = Runnable {
                Toast.makeText(
                    this@MainActivity,
                    java.lang.String.valueOf(myBoundService?.randomGenerator()),
                    Toast.LENGTH_SHORT
                ).show()
            }

            val handler = Handler()
            handler.postDelayed(runnable, 3000)
            Log.d(MainActivity::class.java.canonicalName,"onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            myBoundService = null
            isBound = false
            Log.d(MainActivity::class.java.canonicalName,"onServiceDisconnected")
        }

    }

    fun doBind(v: View?) {
        val intent = Intent(this, MyBoundService::class.java)
        startService(intent)
        bindService(intent, boundServiceConnection, Context.BIND_NOT_FOREGROUND) //if BIND_AUTO_CREATE used onServiceDisconnected is not called.
    }

    fun doUnbind(v: View?) {
        val intent = Intent(this, MyBoundService::class.java)
       // unbindService(boundServiceConnection)
        isBound = false
        stopService(intent) //When you stop a bound service without calling unbind onServiceDisconnected,onUnbind  onDestroy are called as per this example project.
    }


}