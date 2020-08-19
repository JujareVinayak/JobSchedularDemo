package com.vinayak.jobschedulardemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var message = intent?.getStringExtra("toastMessage")
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
}