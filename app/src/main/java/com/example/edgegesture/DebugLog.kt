package com.example.edgegesture

import android.content.Context
import android.os.SystemClock
import android.util.Log

object DebugLog {
    private const val TAG = "EdgeGesture"

    fun info(message: String) {
        write(message)
    }

    fun always(message: String) {
        write(message)
    }

    private fun write(message: String) {
        val line = "$TAG: $message"
        if (!writeXposed(line)) {
            Log.i(TAG, message)
        }
    }

    private fun writeXposed(line: String): Boolean {
        return try {
            Class.forName("de.robv.android.xposed.XposedBridge")
                .getMethod("log", String::class.java)
                .invoke(null, line)
            true
        } catch (_: Throwable) {
            false
        }
    }

    fun markStatus(
        context: Context,
        key: String,
        message: String,
    ) {
        try {
            val moduleContext =
                context.createPackageContext(
                    "com.example.edgegesture",
                    Context.CONTEXT_IGNORE_SECURITY,
                )
            moduleContext.createDeviceProtectedStorageContext()
                .getSharedPreferences(GestureConfig.STATUS_PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(key, SystemClock.elapsedRealtime())
                .putString(GestureConfig.KEY_STATUS_LAST_MESSAGE, message)
                .commit()
        } catch (t: Throwable) {
            always("status write failed: ${t.message}")
        }
    }
}
