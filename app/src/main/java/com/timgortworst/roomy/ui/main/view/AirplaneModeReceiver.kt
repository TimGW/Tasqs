package com.timgortworst.roomy.ui.main.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings

abstract class AirplaneModeReceiver(private val context: Context) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 0) {
            airplaneModeChanged(false)
        } else {
            airplaneModeChanged(true)
        }
    }

    fun register() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        context.registerReceiver(this, intentFilter)
    }

    fun unregister() {
        context.unregisterReceiver(this)
    }

    abstract fun airplaneModeChanged(isEnabled: Boolean)

}
