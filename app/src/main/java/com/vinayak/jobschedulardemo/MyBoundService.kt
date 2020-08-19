package com.vinayak.jobschedulardemo

import android.app.Service
import android.content.Intent
import android.os.Binder

import android.os.IBinder
import android.util.Log
import java.util.*


class MyBoundService : Service() {
    private val localBinder: IBinder = MyBinder()
    override fun onBind(intent: Intent?): IBinder {
        Log.d(MyBoundService::class.java.canonicalName,"onBind")
        return localBinder
    }

    /**
     * Called when all clients have disconnected from a particular interface
     * published by the service.  The default implementation does nothing and
     * returns false.
     *
     * @param intent The Intent that was used to bind to this service,
     * as given to [               Context.bindService][Context.bindService].  Note that any extras that were included with
     * the Intent at that point will *not* be seen here.
     * @return Return true if you would like to have the service's
     * [.onRebind] method later called when new clients bind to it.
     */
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(MyBoundService::class.java.canonicalName,"onUnbind")
        return super.onUnbind(intent)
    }

    /**
     * Called when new clients have connected to the service, after it had
     * previously been notified that all had disconnected in its
     * [.onUnbind].  This will only be called if the implementation
     * of [.onUnbind] was overridden to return true.
     *
     * @param intent The Intent that was used to bind to this service,
     * as given to [               Context.bindService][Context.bindService].  Note that any extras that were included with
     * the Intent at that point will *not* be seen here.
     */
    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(MyBoundService::class.java.canonicalName,"onRebind")
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(MyBoundService::class.java.canonicalName,"onDestroy")
    }

    fun randomGenerator(): Int {
        val randomNumber = Random()
        return randomNumber.nextInt()
    }

    inner class MyBinder : Binder() {
        val service: MyBoundService
            get() = this@MyBoundService
    }
}