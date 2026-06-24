package com.example.edgegesture.data.model

import androidx.compose.ui.graphics.Color
import com.example.edgegesture.GestureConfig
import org.json.JSONObject

/**
 * Data class representing the complete gesture settings state
 *
 * This is an immutable data class that holds all configuration values
 * for the gesture system. Use copy() to create modified versions.
 */
data class SettingsState(
    val enabled: Boolean,
    val edgeWidthDp: Int,
    val swipeDistanceDp: Int,
    val triggerRegionStartPercent: Int,
    val triggerRegionEndPercent: Int,
    val swipeAngleDegrees: Int,
    val doubleTapTimeoutMs: Int,
    val notificationShadeMode: String,
    val notificationTopEdgeDp: Int,
    val notificationHotspotStartPercent: Int,
    val notificationHotspotEndPercent: Int,
    val pointerRadiusDp: Int,
    val pointerControlAlpha: Int,
    val pointerSensitivity: Int,
    val pointerArrowDp: Int,
    val pointerTouchAreaDp: Int,
    val pointerLineDp: Int,
    val pointerMarginDp: Int,
    val pointerCancelDistanceDp: Int,
    val pointerTimeoutMs: Int,
    val pointerSmoothing: Int,
    val pointerMaxSpeed: Int,
    val pointerCurve: Int,
    val pointerControlStyle: String,
    val trackerBallDp: Int,
    val trackerCursorDp: Int,
    val trackerCancelRadiusDp: Int,
    val trackerSensitivity: Int,
    val trackerMaxSpeed: Int,
    val trackerSmoothing: Int,
    val pointerColorRed: Int,
    val pointerColorGreen: Int,
    val pointerColorBlue: Int,
    val hapticFeedbackEnabled: Boolean,
    val actionByKey: Map<String, String>,
) {
    /**
     * Get pointer color as Compose Color
     */
    val pointerColor: Color
        get() =
            Color(
                red = pointerColorRed.coerceIn(0, 255) / 255f,
                green = pointerColorGreen.coerceIn(0, 255) / 255f,
                blue = pointerColorBlue.coerceIn(0, 255) / 255f,
            )

    /**
     * Get timeout in seconds
     */
    val timeoutSeconds: Int
        get() = pointerTimeoutMs / 1000

    /**
     * Export settings to JSON string
     */
    fun toJsonString(): String {
        val actions = JSONObject()
        actionByKey.toSortedMap().forEach { (key, value) ->
            actions.put(key, value)
        }

        return JSONObject()
            .put("schemaVersion", 1)
            .put("app", "EdgeGesture")
            .put("enabled", enabled)
            .put("edgeWidthDp", edgeWidthDp)
            .put("swipeDistanceDp", swipeDistanceDp)
            .put("triggerRegionStartPercent", triggerRegionStartPercent)
            .put("triggerRegionEndPercent", triggerRegionEndPercent)
            .put("swipeAngleDegrees", swipeAngleDegrees)
            .put("doubleTapTimeoutMs", doubleTapTimeoutMs)
            .put("notificationShadeMode", notificationShadeMode)
            .put("notificationTopEdgeDp", notificationTopEdgeDp)
            .put("notificationHotspotStartPercent", notificationHotspotStartPercent)
            .put("notificationHotspotEndPercent", notificationHotspotEndPercent)
            .put("pointerRadiusDp", pointerRadiusDp)
            .put("pointerControlAlpha", pointerControlAlpha)
            .put("pointerSensitivity", pointerSensitivity)
            .put("pointerArrowDp", pointerArrowDp)
            .put("pointerTouchAreaDp", pointerTouchAreaDp)
            .put("pointerLineDp", pointerLineDp)
            .put("pointerMarginDp", pointerMarginDp)
            .put("pointerCancelDistanceDp", pointerCancelDistanceDp)
            .put("pointerTimeoutMs", pointerTimeoutMs)
            .put("pointerSmoothing", pointerSmoothing)
            .put("pointerMaxSpeed", pointerMaxSpeed)
            .put("pointerCurve", pointerCurve)
            .put("pointerControlStyle", pointerControlStyle)
            .put("trackerBallDp", trackerBallDp)
            .put("trackerCursorDp", trackerCursorDp)
            .put("trackerCancelRadiusDp", trackerCancelRadiusDp)
            .put("trackerSensitivity", trackerSensitivity)
            .put("trackerMaxSpeed", trackerMaxSpeed)
            .put("trackerSmoothing", trackerSmoothing)
            .put("pointerColorRed", pointerColorRed)
            .put("pointerColorGreen", pointerColorGreen)
            .put("pointerColorBlue", pointerColorBlue)
            .put("hapticFeedbackEnabled", hapticFeedbackEnabled)
            .put("actionByKey", actions)
            .toString(2)
    }

    companion object {
        /**
         * Create default settings state
         */
        fun default(): SettingsState {
            val actionByKey =
                buildMap {
                    GestureConfig.edges.forEach { edge ->
                        GestureConfig.gestures.forEach { gesture ->
                            val key = GestureConfig.actionKey(edge, gesture)
                            put(key, GestureConfig.defaultAction(edge, gesture))
                        }
                    }
                }

            return SettingsState(
                enabled = GestureConfig.DEFAULT_ENABLED,
                edgeWidthDp = GestureConfig.DEFAULT_EDGE_WIDTH_DP,
                swipeDistanceDp = GestureConfig.DEFAULT_SWIPE_DISTANCE_DP,
                triggerRegionStartPercent = GestureConfig.DEFAULT_TRIGGER_REGION_START_PERCENT,
                triggerRegionEndPercent = GestureConfig.DEFAULT_TRIGGER_REGION_END_PERCENT,
                swipeAngleDegrees = GestureConfig.DEFAULT_SWIPE_ANGLE_DEGREES,
                doubleTapTimeoutMs = GestureConfig.DEFAULT_DOUBLE_TAP_TIMEOUT_MS,
                notificationShadeMode = GestureConfig.DEFAULT_NOTIFICATION_SHADE_MODE,
                notificationTopEdgeDp = GestureConfig.DEFAULT_NOTIFICATION_TOP_EDGE_DP,
                notificationHotspotStartPercent = GestureConfig.DEFAULT_NOTIFICATION_HOTSPOT_START_PERCENT,
                notificationHotspotEndPercent = GestureConfig.DEFAULT_NOTIFICATION_HOTSPOT_END_PERCENT,
                pointerRadiusDp = GestureConfig.DEFAULT_POINTER_RADIUS_DP,
                pointerControlAlpha = GestureConfig.DEFAULT_POINTER_CONTROL_ALPHA,
                pointerSensitivity = GestureConfig.DEFAULT_POINTER_SENSITIVITY,
                pointerArrowDp = GestureConfig.DEFAULT_POINTER_ARROW_DP,
                pointerTouchAreaDp = GestureConfig.DEFAULT_POINTER_TOUCH_AREA_DP,
                pointerLineDp = GestureConfig.DEFAULT_POINTER_LINE_DP,
                pointerMarginDp = GestureConfig.DEFAULT_POINTER_MARGIN_DP,
                pointerCancelDistanceDp = GestureConfig.DEFAULT_POINTER_CANCEL_DISTANCE_DP,
                pointerTimeoutMs = GestureConfig.DEFAULT_POINTER_TIMEOUT_MS,
                pointerSmoothing = GestureConfig.DEFAULT_POINTER_SMOOTHING,
                pointerMaxSpeed = GestureConfig.DEFAULT_POINTER_MAX_SPEED,
                pointerCurve = GestureConfig.DEFAULT_POINTER_CURVE,
                pointerControlStyle = GestureConfig.DEFAULT_POINTER_CONTROL_STYLE,
                trackerBallDp = GestureConfig.DEFAULT_TRACKER_BALL_DP,
                trackerCursorDp = GestureConfig.DEFAULT_TRACKER_CURSOR_DP,
                trackerCancelRadiusDp = GestureConfig.DEFAULT_TRACKER_CANCEL_RADIUS_DP,
                trackerSensitivity = GestureConfig.DEFAULT_TRACKER_SENSITIVITY,
                trackerMaxSpeed = GestureConfig.DEFAULT_TRACKER_MAX_SPEED,
                trackerSmoothing = GestureConfig.DEFAULT_TRACKER_SMOOTHING,
                pointerColorRed = GestureConfig.DEFAULT_POINTER_COLOR_RED,
                pointerColorGreen = GestureConfig.DEFAULT_POINTER_COLOR_GREEN,
                pointerColorBlue = GestureConfig.DEFAULT_POINTER_COLOR_BLUE,
                hapticFeedbackEnabled = GestureConfig.DEFAULT_HAPTIC_FEEDBACK_ENABLED,
                actionByKey = actionByKey,
            )
        }

        /**
         * Import settings from JSON string with strict validation
         */
        fun fromJsonString(text: String): SettingsState {
            val json =
                try {
                    JSONObject(text)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Invalid JSON format: ${e.message}")
                }

            // Validate schema version
            val schemaVersion = json.optInt("schemaVersion", -1)
            if (schemaVersion < 1 || schemaVersion > 1) {
                throw IllegalArgumentException(
                    "Unsupported schema version: $schemaVersion (supported: 1)",
                )
            }

            // Validate app identifier
            val app = json.optString("app", "")
            if (app.isNotEmpty() && app != "EdgeGesture") {
                throw IllegalArgumentException("Invalid app identifier: $app")
            }

            val actionsJson = json.optJSONObject("actionByKey") ?: json.optJSONObject("actions")
            val actionByKey =
                buildMap {
                    GestureConfig.edges.forEach { edge ->
                        GestureConfig.gestures.forEach { gesture ->
                            val key = GestureConfig.actionKey(edge, gesture)
                            val imported = actionsJson?.optString(key).orEmpty()
                            put(
                                key,
                                GestureConfig.sanitizeAction(
                                    gesture,
                                    imported.takeIf { it in GestureConfig.actionValues }
                                        ?: GestureConfig.defaultAction(edge, gesture),
                                ),
                            )
                        }
                    }
                }
            val pointerStyle =
                json.optString(
                    "pointerControlStyle",
                    GestureConfig.DEFAULT_POINTER_CONTROL_STYLE,
                ).takeIf { it in GestureConfig.pointerStyleValues } ?: GestureConfig.DEFAULT_POINTER_CONTROL_STYLE

            return SettingsState(
                enabled = json.optBoolean("enabled", GestureConfig.DEFAULT_ENABLED),
                edgeWidthDp =
                    json.optInt("edgeWidthDp", GestureConfig.DEFAULT_EDGE_WIDTH_DP)
                        .coerceIn(GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP, GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_DP),
                swipeDistanceDp =
                    json.optInt("swipeDistanceDp", GestureConfig.DEFAULT_SWIPE_DISTANCE_DP)
                        .coerceIn(20, 200),
                triggerRegionStartPercent =
                    json.optInt(
                        "triggerRegionStartPercent",
                        GestureConfig.DEFAULT_TRIGGER_REGION_START_PERCENT,
                    ).coerceIn(0, 100),
                triggerRegionEndPercent =
                    json.optInt(
                        "triggerRegionEndPercent",
                        GestureConfig.DEFAULT_TRIGGER_REGION_END_PERCENT,
                    ).coerceIn(0, 100),
                swipeAngleDegrees =
                    json.optInt("swipeAngleDegrees", GestureConfig.DEFAULT_SWIPE_ANGLE_DEGREES)
                        .coerceIn(5, 85),
                doubleTapTimeoutMs =
                    json.optInt(
                        "doubleTapTimeoutMs",
                        GestureConfig.DEFAULT_DOUBLE_TAP_TIMEOUT_MS,
                    ).coerceIn(100, 500),
                notificationShadeMode =
                    GestureConfig.sanitizeNotificationShadeMode(
                        json.optString(
                            "notificationShadeMode",
                            GestureConfig.DEFAULT_NOTIFICATION_SHADE_MODE,
                        ),
                    ),
                notificationTopEdgeDp =
                    json.optInt("notificationTopEdgeDp", GestureConfig.DEFAULT_NOTIFICATION_TOP_EDGE_DP)
                        .coerceIn(GestureConfig.NOTIFICATION_TOP_EDGE_MIN_DP, GestureConfig.NOTIFICATION_TOP_EDGE_MAX_DP),
                notificationHotspotStartPercent =
                    json.optInt(
                        "notificationHotspotStartPercent",
                        GestureConfig.DEFAULT_NOTIFICATION_HOTSPOT_START_PERCENT,
                    ).coerceIn(GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT, GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT),
                notificationHotspotEndPercent =
                    json.optInt(
                        "notificationHotspotEndPercent",
                        GestureConfig.DEFAULT_NOTIFICATION_HOTSPOT_END_PERCENT,
                    ).coerceIn(GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT, GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT),
                pointerRadiusDp =
                    json.optInt("pointerRadiusDp", GestureConfig.DEFAULT_POINTER_RADIUS_DP)
                        .coerceIn(40, 300),
                pointerControlAlpha =
                    json.optInt(
                        "pointerControlAlpha",
                        GestureConfig.DEFAULT_POINTER_CONTROL_ALPHA,
                    ).coerceIn(0, 255),
                pointerSensitivity =
                    json.optInt("pointerSensitivity", GestureConfig.DEFAULT_POINTER_SENSITIVITY)
                        .coerceIn(10, 500),
                pointerArrowDp =
                    json.optInt("pointerArrowDp", GestureConfig.DEFAULT_POINTER_ARROW_DP)
                        .coerceIn(8, 48),
                pointerTouchAreaDp =
                    json.optInt(
                        "pointerTouchAreaDp",
                        GestureConfig.DEFAULT_POINTER_TOUCH_AREA_DP,
                    )
                        .coerceIn(GestureConfig.POINTER_TOUCH_AREA_MIN_DP, GestureConfig.POINTER_TOUCH_AREA_MAX_DP),
                pointerLineDp =
                    json.optInt("pointerLineDp", GestureConfig.DEFAULT_POINTER_LINE_DP)
                        .coerceIn(1, 10),
                pointerMarginDp =
                    json.optInt(
                        "pointerMarginDp",
                        GestureConfig.DEFAULT_POINTER_MARGIN_DP,
                    )
                        .coerceIn(0, 32),
                pointerCancelDistanceDp =
                    json.optInt(
                        "pointerCancelDistanceDp",
                        GestureConfig.DEFAULT_POINTER_CANCEL_DISTANCE_DP,
                    ).coerceIn(50, 500),
                pointerTimeoutMs =
                    json.optInt(
                        "pointerTimeoutMs",
                        GestureConfig.DEFAULT_POINTER_TIMEOUT_MS,
                    )
                        .coerceIn(1000, 30000),
                pointerSmoothing =
                    json.optInt(
                        "pointerSmoothing",
                        GestureConfig.DEFAULT_POINTER_SMOOTHING,
                    )
                        .coerceIn(0, 100),
                pointerMaxSpeed =
                    json.optInt(
                        "pointerMaxSpeed",
                        GestureConfig.DEFAULT_POINTER_MAX_SPEED,
                    )
                        .coerceIn(50, 1000),
                pointerCurve =
                    json.optInt(
                        "pointerCurve",
                        GestureConfig.DEFAULT_POINTER_CURVE,
                    )
                        .coerceIn(50, 300),
                pointerControlStyle = pointerStyle,
                trackerBallDp =
                    json.optInt("trackerBallDp", GestureConfig.DEFAULT_TRACKER_BALL_DP)
                        .coerceIn(4, 40),
                trackerCursorDp =
                    json.optInt("trackerCursorDp", GestureConfig.DEFAULT_TRACKER_CURSOR_DP)
                        .coerceIn(8, 60),
                trackerCancelRadiusDp =
                    json.optInt(
                        "trackerCancelRadiusDp",
                        GestureConfig.DEFAULT_TRACKER_CANCEL_RADIUS_DP,
                    ).coerceIn(30, 300),
                trackerSensitivity =
                    json.optInt("trackerSensitivity", GestureConfig.DEFAULT_TRACKER_SENSITIVITY)
                        .coerceIn(10, 500),
                trackerMaxSpeed =
                    json.optInt("trackerMaxSpeed", GestureConfig.DEFAULT_TRACKER_MAX_SPEED)
                        .coerceIn(50, 1000),
                trackerSmoothing =
                    json.optInt("trackerSmoothing", GestureConfig.DEFAULT_TRACKER_SMOOTHING)
                        .coerceIn(0, 100),
                pointerColorRed =
                    json.optInt("pointerColorRed", GestureConfig.DEFAULT_POINTER_COLOR_RED)
                        .coerceIn(0, 255),
                pointerColorGreen =
                    json.optInt("pointerColorGreen", GestureConfig.DEFAULT_POINTER_COLOR_GREEN)
                        .coerceIn(0, 255),
                pointerColorBlue =
                    json.optInt("pointerColorBlue", GestureConfig.DEFAULT_POINTER_COLOR_BLUE)
                        .coerceIn(0, 255),
                hapticFeedbackEnabled =
                    json.optBoolean(
                        "hapticFeedbackEnabled",
                        GestureConfig.DEFAULT_HAPTIC_FEEDBACK_ENABLED,
                    ),
                actionByKey = actionByKey,
            )
        }
    }
}
