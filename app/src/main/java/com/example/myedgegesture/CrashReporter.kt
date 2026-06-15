package com.example.myedgegesture

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Simple crash reporter that saves uncaught exceptions to a log file.
 * The log can be viewed in the app's files directory or via LSPosed logs.
 *
 * Usage: Call CrashReporter.install(context) in Application.onCreate or MainActivity.onCreate
 */
object CrashReporter {
    private const val LOG_FILE_NAME = "crash_log.txt"
    private const val MAX_LOG_SIZE = 256 * 1024 // 256KB max

    fun install(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                saveCrashLog(context, thread, throwable)
            } catch (_: Throwable) {
                // Don't crash the crash handler
            }

            // Forward to default handler (system crash dialog)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * Get the crash log content, or null if no log exists
     */
    fun getLastCrashLog(context: Context): String? {
        val file = getLogFile(context)
        return if (file.exists() && file.length() > 0) {
            file.readText()
        } else {
            null
        }
    }

    /**
     * Clear the crash log
     */
    fun clearCrashLog(context: Context) {
        val file = getLogFile(context)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun saveCrashLog(
        context: Context,
        thread: Thread,
        throwable: Throwable,
    ) {
        val file = getLogFile(context)

        // Truncate if too large
        if (file.exists() && file.length() > MAX_LOG_SIZE) {
            file.delete()
        }

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val stackTrace = StringWriter().also { throwable.printStackTrace(PrintWriter(it)) }.toString()

        val report =
            buildString {
                appendLine("=== CRASH REPORT ===")
                appendLine("Time: $timestamp")
                appendLine("Thread: ${thread.name}")
                appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                appendLine("App Version: ${getAppVersion(context)}")
                appendLine()
                appendLine("Exception: ${throwable.javaClass.name}")
                appendLine("Message: ${throwable.message}")
                appendLine()
                appendLine("Stack Trace:")
                appendLine(stackTrace)
                appendLine()
            }

        file.appendText(report)
    }

    private fun getLogFile(context: Context): File {
        return File(context.filesDir, LOG_FILE_NAME)
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            "${info.versionName} (${info.longVersionCode})"
        } catch (_: Throwable) {
            "unknown"
        }
    }
}
