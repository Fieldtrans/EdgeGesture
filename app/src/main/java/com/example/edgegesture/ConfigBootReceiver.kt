package com.example.edgegesture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ConfigBootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        SavedConfigBroadcaster.broadcast(
            context.applicationContext,
            intent.action ?: "boot",
        )
    }
}
