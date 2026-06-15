package com.example.myedgegesture

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.ui.screens.NewUserGuideDialog
import com.example.myedgegesture.ui.screens.SettingsScreen
import com.example.myedgegesture.ui.theme.EdgeGestureTheme
import com.example.myedgegesture.ui.utils.t
import com.example.myedgegesture.ui.viewmodel.HookStatus

private const val KEY_NEW_USER_GUIDE_SHOWN = "new_user_guide_shown"

/**
 * TODO: 后续迁移到 SettingsViewModel + ConfigRepository 架构。
 * 当前 MainActivity 直接管理状态和 SharedPreferences，
 * ViewModel/Repository 已创建但尚未集成，避免改动影响 hook 端配置同步。
 */
class MainActivity : ComponentActivity() {
    private var latestState: SettingsState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CrashReporter.install(this)
        enableEdgeToEdge()
        requestHighRefreshRate()

        val initialState = loadState()
        latestState = initialState
        sendBroadcast(buildConfigIntent(initialState))

        setContent {
            var settings by remember { mutableStateOf(initialState) }
            val prefs = remember { getSharedPreferences(GestureConfig.PREFS_NAME, MODE_PRIVATE) }
            var showGuide by remember {
                mutableStateOf(!prefs.getBoolean(KEY_NEW_USER_GUIDE_SHOWN, false))
            }

            EdgeGestureTheme {
                SettingsScreen(
                    settings = settings,
                    onSettingsChange = { next ->
                        settings = next
                        latestState = next
                        saveConfig(next)
                    },
                    onReset = {
                        settings = settings.withRecommendedValues()
                        saveConfig(settings)
                        Toast.makeText(this, t("已恢复推荐值", "Recommended values restored"), Toast.LENGTH_SHORT).show()
                    },
                    onExport = { uri ->
                        exportConfig(uri, settings)
                    },
                    onImport = { uri ->
                        importConfig(uri)?.let { imported ->
                            settings = imported
                            saveConfig(imported)
                            Toast.makeText(this, t("配置已导入", "Config imported"), Toast.LENGTH_SHORT).show()
                        }
                    },
                    hookStatus = readHookStatus(),
                    onShowGuide = { showGuide = true },
                )

                if (showGuide) {
                    NewUserGuideDialog(
                        onDismiss = {
                            prefs.edit().putBoolean(KEY_NEW_USER_GUIDE_SHOWN, true).apply()
                            showGuide = false
                        },
                    )
                }
            }
        }
    }

    private fun requestHighRefreshRate() {
        runCatching {
            val attrs = window.attributes
            attrs.preferredRefreshRate = 120f
            window.attributes = attrs
        }
    }

    private fun loadState(): SettingsState {
        val prefs = getSharedPreferences(GestureConfig.PREFS_NAME, MODE_PRIVATE)

        // 配置版本迁移：检查是否需要更新默认值
        val configVersion = prefs.getInt("config_schema_version", 0)
        if (configVersion < 2) {
            // v2: 控制圆半径从120改为72，并记录旧版控制曲线默认值
            prefs.edit()
                .putInt("config_schema_version", 2)
                .apply()
            // 不强制覆盖用户值，只更新 schema 版本标记
        }

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

        return SettingsState(
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

    private fun saveConfig(state: SettingsState) {
        latestState = state

        val savedAt = System.currentTimeMillis()
        val editor = getSharedPreferences(GestureConfig.PREFS_NAME, MODE_PRIVATE).edit()
        putCurrentConfig(editor, state, savedAt)
        editor.apply()

        val deviceEditor =
            createDeviceProtectedStorageContext()
                .getSharedPreferences(GestureConfig.PREFS_NAME, MODE_PRIVATE)
                .edit()
        putCurrentConfig(deviceEditor, state, savedAt)
        deviceEditor.apply()

        sendBroadcast(buildConfigIntent(state))
    }

    private fun buildConfigIntent(state: SettingsState): Intent {
        return GestureConfig.putConfigExtras(
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
    }

    private fun exportConfig(
        uri: Uri,
        state: SettingsState,
    ) {
        try {
            contentResolver.openOutputStream(uri)?.use { output ->
                output.write(state.toJsonString().toByteArray(Charsets.UTF_8))
            } ?: error("openOutputStream returned null")
            Toast.makeText(this, t("配置已导出", "Config exported"), Toast.LENGTH_SHORT).show()
        } catch (e: Throwable) {
            Toast.makeText(this, t("导出失败: ${e.message}", "Export failed: ${e.message}"), Toast.LENGTH_LONG).show()
        }
    }

    private fun importConfig(uri: Uri): SettingsState? {
        return try {
            val text =
                contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                    ?: error("openInputStream returned null")
            SettingsState.fromJsonString(text)
        } catch (e: Throwable) {
            Toast.makeText(this, t("导入失败: ${e.message}", "Import failed: ${e.message}"), Toast.LENGTH_LONG).show()
            null
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

    private fun readHookStatus(): HookStatus {
        val moduleLoadedInApp = HookHealth.isModuleLoaded()
        val normalPrefs = getSharedPreferences(GestureConfig.STATUS_PREFS_NAME, MODE_PRIVATE)
        val devicePrefs =
            createDeviceProtectedStorageContext()
                .getSharedPreferences(GestureConfig.STATUS_PREFS_NAME, MODE_PRIVATE)
        val prefs =
            if (devicePrefs.contains(GestureConfig.KEY_STATUS_LOADED_AT) ||
                devicePrefs.contains(GestureConfig.KEY_STATUS_STARTED_AT)
            ) {
                devicePrefs
            } else {
                normalPrefs
            }
        val loadedAt = prefs.getLong(GestureConfig.KEY_STATUS_LOADED_AT, 0L)
        val startedAt = prefs.getLong(GestureConfig.KEY_STATUS_STARTED_AT, 0L)
        val message = prefs.getString(GestureConfig.KEY_STATUS_LAST_MESSAGE, "") ?: ""
        val gesturesEnabled =
            latestState?.enabled
                ?: getSharedPreferences(GestureConfig.PREFS_NAME, MODE_PRIVATE)
                    .getBoolean(GestureConfig.KEY_ENABLED, GestureConfig.DEFAULT_ENABLED)
        val now = SystemClock.elapsedRealtime()

        fun isFreshBootStatus(value: Long): Boolean {
            return value > 0L && value <= now
        }

        val inputFilterStarted = gesturesEnabled && isFreshBootStatus(startedAt)
        val systemServerLoaded = isFreshBootStatus(loadedAt)

        return when {
            inputFilterStarted ->
                HookStatus(
                    text = t("LSPosed 输入过滤器已启动 / $message", "LSPosed input filter started / $message"),
                    active = true,
                    enhancedActive = true,
                )
            systemServerLoaded ->
                HookStatus(
                    text = t("system_server 已加载，等待输入过滤器", "system_server loaded, waiting for input filter"),
                    active = true,
                    enhancedActive = false,
                )
            moduleLoadedInApp ->
                HookStatus(
                    text = t("LSPosed 已加载模块", "LSPosed module loaded"),
                    active = true,
                    enhancedActive = false,
                )
            else ->
                HookStatus(
                    text = t("未检测到加载；启用 LSPosed 后需重启", "Module not detected; reboot after enabling it in LSPosed"),
                    active = false,
                    enhancedActive = false,
                )
        }
    }
}
