package com.example.edgegesture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

object SavedConfigBroadcaster {
    fun broadcast(
        context: Context,
        reason: String,
    ) {
        runCatching {
            val intent = buildIntent(context)
            context.sendBroadcast(intent)
            DebugLog.info("saved config broadcast by $reason")
        }.onFailure {
            DebugLog.info("saved config broadcast failed by $reason: ${it.message}")
        }
    }

    fun buildIntent(context: Context): Intent {
        val prefs = chooseConfigPrefs(context)
        val actionByKey =
            buildMap {
                GestureConfig.edges.forEach { edge ->
                    GestureConfig.gestures.forEach { gesture ->
                        val key = GestureConfig.actionKey(edge, gesture)
                        val savedAction =
                            prefs.getString(key, GestureConfig.defaultAction(edge, gesture))
                                ?: GestureConfig.defaultAction(edge, gesture)
                        put(key, GestureConfig.sanitizeAction(gesture, savedAction))
                    }
                }
            }

        return GestureConfig.putConfigExtras(
            Intent(GestureConfig.ACTION_CONFIG_CHANGED),
            prefs.getBoolean(GestureConfig.KEY_ENABLED, GestureConfig.DEFAULT_ENABLED),
            prefs.getInt(GestureConfig.KEY_EDGE_WIDTH_DP, GestureConfig.DEFAULT_EDGE_WIDTH_DP),
            prefs.getInt(GestureConfig.KEY_SWIPE_DISTANCE_DP, GestureConfig.DEFAULT_SWIPE_DISTANCE_DP),
            prefs.getInt(
                GestureConfig.KEY_TRIGGER_REGION_START_PERCENT,
                GestureConfig.DEFAULT_TRIGGER_REGION_START_PERCENT,
            ),
            prefs.getInt(
                GestureConfig.KEY_TRIGGER_REGION_END_PERCENT,
                GestureConfig.DEFAULT_TRIGGER_REGION_END_PERCENT,
            ),
            prefs.getInt(GestureConfig.KEY_SWIPE_ANGLE_DEGREES, GestureConfig.DEFAULT_SWIPE_ANGLE_DEGREES),
            prefs.getInt(GestureConfig.KEY_DOUBLE_TAP_TIMEOUT_MS, GestureConfig.DEFAULT_DOUBLE_TAP_TIMEOUT_MS),
            GestureConfig.sanitizeNotificationShadeMode(
                prefs.getString(
                    GestureConfig.KEY_NOTIFICATION_SHADE_MODE,
                    GestureConfig.DEFAULT_NOTIFICATION_SHADE_MODE,
                ),
            ),
            prefs.getInt(GestureConfig.KEY_POINTER_RADIUS_DP, GestureConfig.DEFAULT_POINTER_RADIUS_DP),
            prefs.getInt(GestureConfig.KEY_POINTER_CONTROL_ALPHA, GestureConfig.DEFAULT_POINTER_CONTROL_ALPHA),
            prefs.getInt(GestureConfig.KEY_POINTER_SENSITIVITY, GestureConfig.DEFAULT_POINTER_SENSITIVITY),
            prefs.getInt(GestureConfig.KEY_POINTER_ARROW_DP, GestureConfig.DEFAULT_POINTER_ARROW_DP),
            prefs.getInt(GestureConfig.KEY_POINTER_TOUCH_AREA_DP, GestureConfig.DEFAULT_POINTER_TOUCH_AREA_DP),
            prefs.getInt(GestureConfig.KEY_POINTER_LINE_DP, GestureConfig.DEFAULT_POINTER_LINE_DP),
            prefs.getInt(GestureConfig.KEY_POINTER_MARGIN_DP, GestureConfig.DEFAULT_POINTER_MARGIN_DP),
            prefs.getInt(GestureConfig.KEY_POINTER_CANCEL_DISTANCE_DP, GestureConfig.DEFAULT_POINTER_CANCEL_DISTANCE_DP),
            prefs.getInt(GestureConfig.KEY_POINTER_TIMEOUT_MS, GestureConfig.DEFAULT_POINTER_TIMEOUT_MS),
            prefs.getInt(GestureConfig.KEY_POINTER_SMOOTHING, GestureConfig.DEFAULT_POINTER_SMOOTHING),
            prefs.getInt(GestureConfig.KEY_POINTER_MAX_SPEED, GestureConfig.DEFAULT_POINTER_MAX_SPEED),
            prefs.getInt(GestureConfig.KEY_POINTER_CURVE, GestureConfig.DEFAULT_POINTER_CURVE),
            GestureConfig.DEFAULT_POINTER_MAPPING_MODE,
            prefs.getString(
                GestureConfig.KEY_POINTER_CONTROL_STYLE,
                GestureConfig.DEFAULT_POINTER_CONTROL_STYLE,
            ) ?: GestureConfig.DEFAULT_POINTER_CONTROL_STYLE,
            prefs.getInt(GestureConfig.KEY_TRACKER_BALL_DP, GestureConfig.DEFAULT_TRACKER_BALL_DP),
            prefs.getInt(GestureConfig.KEY_TRACKER_CURSOR_DP, GestureConfig.DEFAULT_TRACKER_CURSOR_DP),
            prefs.getInt(GestureConfig.KEY_TRACKER_CANCEL_RADIUS_DP, GestureConfig.DEFAULT_TRACKER_CANCEL_RADIUS_DP),
            prefs.getInt(GestureConfig.KEY_TRACKER_SENSITIVITY, GestureConfig.DEFAULT_TRACKER_SENSITIVITY),
            prefs.getInt(GestureConfig.KEY_TRACKER_MAX_SPEED, GestureConfig.DEFAULT_TRACKER_MAX_SPEED),
            prefs.getInt(GestureConfig.KEY_TRACKER_SMOOTHING, GestureConfig.DEFAULT_TRACKER_SMOOTHING),
            prefs.getInt(GestureConfig.KEY_POINTER_COLOR_RED, GestureConfig.DEFAULT_POINTER_COLOR_RED),
            prefs.getInt(GestureConfig.KEY_POINTER_COLOR_GREEN, GestureConfig.DEFAULT_POINTER_COLOR_GREEN),
            prefs.getInt(GestureConfig.KEY_POINTER_COLOR_BLUE, GestureConfig.DEFAULT_POINTER_COLOR_BLUE),
            prefs.getBoolean(GestureConfig.KEY_HAPTIC_FEEDBACK_ENABLED, GestureConfig.DEFAULT_HAPTIC_FEEDBACK_ENABLED),
            actionByKey,
        )
    }

    private fun chooseConfigPrefs(context: Context): SharedPreferences {
        val deviceContext = context.createDeviceProtectedStorageContext()
        val devicePrefs = deviceContext.getSharedPreferences(GestureConfig.PREFS_NAME, Context.MODE_PRIVATE)
        val normalPrefs =
            runCatching {
                context.getSharedPreferences(GestureConfig.PREFS_NAME, Context.MODE_PRIVATE)
            }.getOrNull()

        val deviceHasConfig = devicePrefs.contains(GestureConfig.KEY_ENABLED)
        val normalHasConfig = normalPrefs?.contains(GestureConfig.KEY_ENABLED) == true

        if (!deviceHasConfig && normalHasConfig) return normalPrefs
        if (deviceHasConfig && !normalHasConfig) return devicePrefs
        if (normalPrefs == null) return devicePrefs

        val deviceUpdatedAt = devicePrefs.getLong(GestureConfig.KEY_CONFIG_UPDATED_AT, 0L)
        val normalUpdatedAt = normalPrefs.getLong(GestureConfig.KEY_CONFIG_UPDATED_AT, 0L)
        return if (normalUpdatedAt >= deviceUpdatedAt) normalPrefs else devicePrefs
    }
}
