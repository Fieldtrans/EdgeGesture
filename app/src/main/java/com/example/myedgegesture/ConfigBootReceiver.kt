package com.example.myedgegesture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ConfigBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        SavedConfigBroadcaster.broadcast(
            context.applicationContext,
            intent.action ?: "boot"
        )
    }
}
