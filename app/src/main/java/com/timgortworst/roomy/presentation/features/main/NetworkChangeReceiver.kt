package com.timgortworst.roomy.presentation.features.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager

abstract class NetworkChangeReceiver(private val context: Context) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (isOnline(context)) {
                networkStatusChanged(true)
            } else {
                networkStatusChanged(false)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            //should check null because in airplane mode it will be null
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }

    fun register() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(this, intentFilter)
    }

    fun unregister() {
        context.unregisterReceiver(this)
    }

    abstract fun networkStatusChanged(isEnabled: Boolean)
}