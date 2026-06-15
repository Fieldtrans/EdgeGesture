package com.example.myedgegesture.data.repository

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock
import com.example.myedgegesture.GestureConfig
import com.example.myedgegesture.HookHealth
import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.ui.viewmodel.HookStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing gesture configuration
 *
 * Handles all data operations related to gesture settings,
 * including loading from and saving to SharedPreferences.
 */
class ConfigRepository(private val context: Context) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(GestureConfig.PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val devicePrefs: SharedPreferences by lazy {
        context.createDeviceProtectedStorageContext()
            .getSharedPreferences(GestureConfig.PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val statusPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(GestureConfig.STATUS_PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val deviceStatusPrefs: SharedPreferences by lazy {
        context.createDeviceProtectedStorageContext()
            .getSharedPreferences(GestureConfig.STATUS_PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Load settings from SharedPreferences
     */
    suspend fun loadSettings(): SettingsState =
        withContext(Dispatchers.IO) {
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

            SettingsState(
                enabled = prefs.getBoolean(GestureConfig.KEY_ENABLED, GestureConfig.DEFAULT_ENABLED),
                edgeWidthDp = prefs.getInt(GestureConfig.KEY_EDGE_WIDTH_DP, GestureConfig.DEFAULT_EDGE_WIDTH_DP),
                swipeDistanceDp = prefs.getInt(GestureConfig.KEY_SWIPE_DISTANCE_DP, GestureConfig.DEFAULT_SWIPE_DISTANCE_DP),
                triggerRegionStartPercent =
                    prefs.getInt(
                        GestureConfig.KEY_TRIGGER_REGION_START_PERCENT,
                        GestureConfig.DEFAULT_TRIGGER_REGION_START_PERCENT,
                    ),
                triggerRegionEndPercent =
                    prefs.getInt(
                        GestureConfig.KEY_TRIGGER_REGION_END_PERCENT,
                        GestureConfig.DEFAULT_TRIGGER_REGION_END_PERCENT,
                    ),
                swipeAngleDegrees = prefs.getInt(GestureConfig.KEY_SWIPE_ANGLE_DEGREES, GestureConfig.DEFAULT_SWIPE_ANGLE_DEGREES),
                doubleTapTimeoutMs =
                    prefs.getInt(
                        GestureConfig.KEY_DOUBLE_TAP_TIMEOUT_MS,
                        GestureConfig.DEFAULT_DOUBLE_TAP_TIMEOUT_MS,
                    ),
                notificationShadeMode =
                    GestureConfig.sanitizeNotificationShadeMode(
                        prefs.getString(
                            GestureConfig.KEY_NOTIFICATION_SHADE_MODE,
                            GestureConfig.DEFAULT_NOTIFICATION_SHADE_MODE,
                        ),
                    ),
                pointerRadiusDp = prefs.getInt(GestureConfig.KEY_POINTER_RADIUS_DP, GestureConfig.DEFAULT_POINTER_RADIUS_DP),
                pointerControlAlpha =
                    prefs.getInt(
                        GestureConfig.KEY_POINTER_CONTROL_ALPHA,
                        GestureConfig.DEFAULT_POINTER_CONTROL_ALPHA,
                    ),
                pointerSensitivity = prefs.getInt(GestureConfig.KEY_POINTER_SENSITIVITY, GestureConfig.DEFAULT_POINTER_SENSITIVITY),
                pointerArrowDp = prefs.getInt(GestureConfig.KEY_POINTER_ARROW_DP, GestureConfig.DEFAULT_POINTER_ARROW_DP),
                pointerTouchAreaDp =
                    prefs.getInt(
                        GestureConfig.KEY_POINTER_TOUCH_AREA_DP,
                        GestureConfig.DEFAULT_POINTER_TOUCH_AREA_DP,
                    ),
                pointerLineDp = prefs.getInt(GestureConfig.KEY_POINTER_LINE_DP, GestureConfig.DEFAULT_POINTER_LINE_DP),
                pointerMarginDp = prefs.getInt(GestureConfig.KEY_POINTER_MARGIN_DP, GestureConfig.DEFAULT_POINTER_MARGIN_DP),
                pointerCancelDistanceDp =
                    prefs.getInt(
                        GestureConfig.KEY_POINTER_CANCEL_DISTANCE_DP,
                        GestureConfig.DEFAULT_POINTER_CANCEL_DISTANCE_DP,
                    ),
                pointerTimeoutMs = prefs.getInt(GestureConfig.KEY_POINTER_TIMEOUT_MS, GestureConfig.DEFAULT_POINTER_TIMEOUT_MS),
                pointerSmoothing = prefs.getInt(GestureConfig.KEY_POINTER_SMOOTHING, GestureConfig.DEFAULT_POINTER_SMOOTHING),
                pointerMaxSpeed = prefs.getInt(GestureConfig.KEY_POINTER_MAX_SPEED, GestureConfig.DEFAULT_POINTER_MAX_SPEED),
                pointerCurve = prefs.getInt(GestureConfig.KEY_POINTER_CURVE, GestureConfig.DEFAULT_POINTER_CURVE),
                pointerControlStyle =
                    prefs.getString(
                        GestureConfig.KEY_POINTER_CONTROL_STYLE,
                        GestureConfig.DEFAULT_POINTER_CONTROL_STYLE,
                    ) ?: GestureConfig.DEFAULT_POINTER_CONTROL_STYLE,
                trackerBallDp = prefs.getInt(GestureConfig.KEY_TRACKER_BALL_DP, GestureConfig.DEFAULT_TRACKER_BALL_DP),
                trackerCursorDp = prefs.getInt(GestureConfig.KEY_TRACKER_CURSOR_DP, GestureConfig.DEFAULT_TRACKER_CURSOR_DP),
                trackerCancelRadiusDp =
                    prefs.getInt(
                        GestureConfig.KEY_TRACKER_CANCEL_RADIUS_DP,
                        GestureConfig.DEFAULT_TRACKER_CANCEL_RADIUS_DP,
                    ),
                trackerSensitivity = prefs.getInt(GestureConfig.KEY_TRACKER_SENSITIVITY, GestureConfig.DEFAULT_TRACKER_SENSITIVITY),
                trackerMaxSpeed = prefs.getInt(GestureConfig.KEY_TRACKER_MAX_SPEED, GestureConfig.DEFAULT_TRACKER_MAX_SPEED),
                trackerSmoothing = prefs.getInt(GestureConfig.KEY_TRACKER_SMOOTHING, GestureConfig.DEFAULT_TRACKER_SMOOTHING),
                pointerColorRed = prefs.getInt(GestureConfig.KEY_POINTER_COLOR_RED, GestureConfig.DEFAULT_POINTER_COLOR_RED),
                pointerColorGreen = prefs.getInt(GestureConfig.KEY_POINTER_COLOR_GREEN, GestureConfig.DEFAULT_POINTER_COLOR_GREEN),
                pointerColorBlue = prefs.getInt(GestureConfig.KEY_POINTER_COLOR_BLUE, GestureConfig.DEFAULT_POINTER_COLOR_BLUE),
                hapticFeedbackEnabled =
                    prefs.getBoolean(
                        GestureConfig.KEY_HAPTIC_FEEDBACK_ENABLED,
                        GestureConfig.DEFAULT_HAPTIC_FEEDBACK_ENABLED,
                    ),
                actionByKey = actionByKey,
            )
        }

    /**
     * Save settings to SharedPreferences
     */
    suspend fun saveSettings(state: SettingsState) =
        withContext(Dispatchers.IO) {
            val savedAt = System.currentTimeMillis()

            // Save to normal storage
            prefs.edit().apply {
                putCurrentConfig(this, state, savedAt)
                apply()
            }

            // Save to device protected storage
            devicePrefs.edit().apply {
                putCurrentConfig(this, state, savedAt)
                apply()
            }

            // Broadcast config change
            val intent =
                GestureConfig.putConfigExtras(
                    Intent(GestureConfig.ACTION_CONFIG_CHANGED),
                    state.enabled,
                    state.edgeWidthDp,
                    state.swipeDistanceDp,
                    state.triggerRegionStartPercent,
                    state.triggerRegionEndPercent,
                    state.swipeAngleDegrees,
                    state.doubleTapTimeoutMs,
                    state.notificationShadeMode,
                    state.pointerRadiusDp,
                    state.pointerControlAlpha,
                    state.pointerSensitivity,
                    state.pointerArrowDp,
                    state.pointerTouchAreaDp,
                    state.pointerLineDp,
                    state.pointerMarginDp,
                    state.pointerCancelDistanceDp,
                    state.pointerTimeoutMs,
                    state.pointerSmoothing,
                    state.pointerMaxSpeed,
                    state.pointerCurve,
                    GestureConfig.DEFAULT_POINTER_MAPPING_MODE,
                    state.pointerControlStyle,
                    state.trackerBallDp,
                    state.trackerCursorDp,
                    state.trackerCancelRadiusDp,
                    state.trackerSensitivity,
                    state.trackerMaxSpeed,
                    state.trackerSmoothing,
                    state.pointerColorRed,
                    state.pointerColorGreen,
                    state.pointerColorBlue,
                    state.hapticFeedbackEnabled,
                    state.actionByKey,
                )
            context.sendBroadcast(intent)
        }

    /**
     * Load hook status
     */
    suspend fun loadHookStatus(): HookStatus =
        withContext(Dispatchers.IO) {
            val moduleLoadedInApp = HookHealth.isModuleLoaded()

            val activeStatusPrefs =
                if (deviceStatusPrefs.contains(GestureConfig.KEY_STATUS_LOADED_AT) ||
                    deviceStatusPrefs.contains(GestureConfig.KEY_STATUS_STARTED_AT)
                ) {
                    deviceStatusPrefs
                } else {
                    statusPrefs
                }

            val loadedAt = activeStatusPrefs.getLong(GestureConfig.KEY_STATUS_LOADED_AT, 0L)
            val startedAt = activeStatusPrefs.getLong(GestureConfig.KEY_STATUS_STARTED_AT, 0L)
            val message = activeStatusPrefs.getString(GestureConfig.KEY_STATUS_LAST_MESSAGE, "") ?: ""

            val gesturesEnabled = prefs.getBoolean(GestureConfig.KEY_ENABLED, GestureConfig.DEFAULT_ENABLED)
            val now = SystemClock.elapsedRealtime()

            fun isFreshBootStatus(value: Long): Boolean {
                return value > 0L && value <= now
            }

            val inputFilterStarted = gesturesEnabled && isFreshBootStatus(startedAt)
            val systemServerLoaded = isFreshBootStatus(loadedAt)

            when {
                inputFilterStarted ->
                    HookStatus(
                        text = "LSPosed 输入过滤器已启动 / $message",
                        active = true,
                        enhancedActive = true,
                    )
                systemServerLoaded ->
                    HookStatus(
                        text = "system_server 已加载，等待输入过滤器",
                        active = true,
                        enhancedActive = false,
                    )
                moduleLoadedInApp ->
                    HookStatus(
                        text = "LSPosed 已加载模块",
                        active = true,
                        enhancedActive = false,
                    )
                else ->
                    HookStatus(
                        text = "未检测到加载；启用 LSPosed 后需重启",
                        active = false,
                        enhancedActive = false,
                    )
            }
        }

    private fun putCurrentConfig(
        editor: SharedPreferences.Editor,
        state: SettingsState,
        savedAt: Long,
    ) {
        editor
            .putLong(GestureConfig.KEY_CONFIG_UPDATED_AT, savedAt)
            .putBoolean(GestureConfig.KEY_ENABLED, state.enabled)
            .putInt(GestureConfig.KEY_EDGE_WIDTH_DP, state.edgeWidthDp)
            .putInt(GestureConfig.KEY_SWIPE_DISTANCE_DP, state.swipeDistanceDp)
            .putInt(GestureConfig.KEY_TRIGGER_REGION_START_PERCENT, state.triggerRegionStartPercent)
            .putInt(GestureConfig.KEY_TRIGGER_REGION_END_PERCENT, state.triggerRegionEndPercent)
            .putInt(GestureConfig.KEY_SWIPE_ANGLE_DEGREES, state.swipeAngleDegrees)
            .putInt(GestureConfig.KEY_DOUBLE_TAP_TIMEOUT_MS, state.doubleTapTimeoutMs)
            .putString(
                GestureConfig.KEY_NOTIFICATION_SHADE_MODE,
                GestureConfig.sanitizeNotificationShadeMode(state.notificationShadeMode),
            )
            .putInt(GestureConfig.KEY_POINTER_RADIUS_DP, state.pointerRadiusDp)
            .putInt(GestureConfig.KEY_POINTER_CONTROL_ALPHA, state.pointerControlAlpha)
            .putInt(GestureConfig.KEY_POINTER_SENSITIVITY, state.pointerSensitivity)
            .putInt(GestureConfig.KEY_POINTER_ARROW_DP, state.pointerArrowDp)
            .putInt(GestureConfig.KEY_POINTER_TOUCH_AREA_DP, state.pointerTouchAreaDp)
            .putInt(GestureConfig.KEY_POINTER_LINE_DP, state.pointerLineDp)
            .putInt(GestureConfig.KEY_POINTER_MARGIN_DP, state.pointerMarginDp)
            .putInt(GestureConfig.KEY_POINTER_CANCEL_DISTANCE_DP, state.pointerCancelDistanceDp)
            .putInt(GestureConfig.KEY_POINTER_TIMEOUT_MS, state.pointerTimeoutMs)
            .putInt(GestureConfig.KEY_POINTER_SMOOTHING, state.pointerSmoothing)
            .putInt(GestureConfig.KEY_POINTER_MAX_SPEED, state.pointerMaxSpeed)
            .putInt(GestureConfig.KEY_POINTER_CURVE, state.pointerCurve)
            .putString(GestureConfig.KEY_POINTER_MAPPING_MODE, GestureConfig.DEFAULT_POINTER_MAPPING_MODE)
            .putString(GestureConfig.KEY_POINTER_CONTROL_STYLE, state.pointerControlStyle)
            .putInt(GestureConfig.KEY_TRACKER_BALL_DP, state.trackerBallDp)
            .putInt(GestureConfig.KEY_TRACKER_CURSOR_DP, state.trackerCursorDp)
            .putInt(GestureConfig.KEY_TRACKER_CANCEL_RADIUS_DP, state.trackerCancelRadiusDp)
            .putInt(GestureConfig.KEY_TRACKER_SENSITIVITY, state.trackerSensitivity)
            .putInt(GestureConfig.KEY_TRACKER_MAX_SPEED, state.trackerMaxSpeed)
            .putInt(GestureConfig.KEY_TRACKER_SMOOTHING, state.trackerSmoothing)
            .putInt(GestureConfig.KEY_POINTER_COLOR_RED, state.pointerColorRed)
            .putInt(GestureConfig.KEY_POINTER_COLOR_GREEN, state.pointerColorGreen)
            .putInt(GestureConfig.KEY_POINTER_COLOR_BLUE, state.pointerColorBlue)
            .putBoolean(GestureConfig.KEY_HAPTIC_FEEDBACK_ENABLED, state.hapticFeedbackEnabled)

        state.actionByKey.forEach { (key, value) ->
            editor.putString(key, GestureConfig.sanitizeActionKey(key, value))
        }
    }
}
