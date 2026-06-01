package com.example.myedgegesture

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object AccessibilityActionDispatcher {

    fun tap(
        service: AccessibilityService,
        x: Float,
        y: Float,
        durationMs: Long = TAP_DURATION_MS,
        onFinished: (() -> Unit)? = null
    ) {
        if (!DeviceState.canRunGestures(service)) {
            DebugLog.info("accessibility tap ignored because device is locked or screen is off")
            onFinished?.invoke()
            return
        }

        val tapX = x.coerceAtLeast(1f)
        val tapY = y.coerceAtLeast(1f)
        val path = Path().apply {
            moveTo(tapX, tapY)
            lineTo(tapX + TAP_MOVE_EPSILON_PX, tapY + TAP_MOVE_EPSILON_PX)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0L, durationMs))
            .build()
        val ok = service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    DebugLog.info("accessibility tap completed x=$tapX y=$tapY")
                    onFinished?.invoke()
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    DebugLog.info("accessibility tap cancelled x=$tapX y=$tapY")
                    onFinished?.invoke()
                }
            },
            null
        )
        DebugLog.info("accessibility tap dispatched x=$tapX y=$tapY result=$ok")
        if (!ok) {
            onFinished?.invoke()
        }
    }

    fun perform(
        service: AccessibilityService,
        edge: EdgeGestureDetector.Edge,
        zone: String,
        gesture: String,
        startX: Float,
        startY: Float,
        x: Float,
        y: Float
    ) {
        val action = RuntimeGestureConfig.actionFor(edge, gesture)
        DebugLog.info("accessibility dispatch edge=$edge zone=$zone gesture=$gesture action=$action")

        if (!DeviceState.canRunGestures(service)) {
            DebugLog.info("accessibility gesture ignored because device is locked or screen is off")
            return
        }

        when (action) {
            GestureConfig.ACTION_RECENTS -> runGlobalAction(
                service,
                AccessibilityService.GLOBAL_ACTION_RECENTS,
                "recents"
            )

            GestureConfig.ACTION_ONE_HAND_TAP -> DebugLog.info(
                "accessibility one-hand tap deferred to pointer layer"
            )

            else -> DebugLog.info("accessibility no action mapped")
        }

        if (action != GestureConfig.ACTION_NONE && RuntimeGestureConfig.hapticFeedbackEnabled) {
            performHapticFeedback(service)
        }
    }

    private fun performHapticFeedback(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= 31) {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Throwable) {}
    }

    fun expandNotifications(service: AccessibilityService) {
        if (!DeviceState.canRunGestures(service)) {
            DebugLog.info("accessibility notifications ignored because device is locked or screen is off")
            return
        }
        runGlobalAction(
            service,
            AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS,
            "notifications"
        )
    }

    private fun runGlobalAction(
        service: AccessibilityService,
        action: Int,
        actionName: String
    ) {
        val ok = service.performGlobalAction(action)
        DebugLog.info("accessibility action -> $actionName result=$ok")
    }

    private const val TAP_DURATION_MS = 45L
    private const val TAP_MOVE_EPSILON_PX = 0.1f
}
