package com.example.edgegesture

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class AccessibilityPointerController(
    private val service: EdgeAccessibilityService,
    private val edge: EdgeGestureDetector.Edge,
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val downX: Float,
    private val downY: Float,
    private val notificationOnly: Boolean = false,
) {
    private val windowManager = service.getSystemService(android.content.Context.WINDOW_SERVICE) as WindowManager
    private val view = PointerOverlayView(service)
    private var shown = false
    private var lastFingerX = downX
    private var lastFingerY = downY
    private var baseX = if (edge == EdgeGestureDetector.Edge.LEFT) 0f else screenWidth.toFloat()
    private var baseY = downY
    private var tipX = baseX
    private var tipY = baseY
    private var cursorX = screenWidth * 0.5f
    private var cursorY = screenHeight * 0.5f
    private var trackerX = downX
    private var trackerY = downY
    private var trackerOriginX = downX
    private var trackerOriginY = downY
    private var cancelled = true
    private var notificationTriggered = false

    fun start(
        fingerX: Float,
        fingerY: Float,
    ) {
        if (shown) return
        if (RuntimeGestureConfig.pointerControlStyle == GestureConfig.POINTER_STYLE_TRACKER_CURSOR) {
            trackerOriginX = fingerX
            trackerOriginY = fingerY
            lastFingerX = fingerX
            lastFingerY = fingerY
            trackerX = fingerX
            trackerY = fingerY
            val start = trackerCursorStart(fingerX, fingerY)
            cursorX = start.first
            cursorY = start.second
        }
        updatePointer(fingerX, fingerY)
        val params =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                preferredRefreshRate = PREFERRED_REFRESH_RATE
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }

        runCatching {
            windowManager.addView(view, params)
            shown = true
            view.invalidate()
        }.onFailure {
            DebugLog.always("accessibility pointer show failed: ${it.message}")
        }
    }

    fun moveTo(
        fingerX: Float,
        fingerY: Float,
    ) {
        if (!shown) return
        updatePointer(fingerX, fingerY)
        maybeTriggerNotificationOnTouch()
        view.invalidate()
    }

    fun finish() {
        val tapX: Float
        val tapY: Float
        when (RuntimeGestureConfig.pointerControlStyle) {
            GestureConfig.POINTER_STYLE_TRACKER_CURSOR -> {
                tapX = cursorX
                tapY = cursorY
            }

            else -> {
                tapX = tipX
                tapY = tipY
            }
        }

        val shouldTap = !cancelled
        dismiss()

        if (shouldTap) {
            if (RuntimeGestureConfig.notificationShadeMode == GestureConfig.NOTIFICATION_SHADE_RELEASE &&
                isInsideNotificationHotspot(tapX, tapY)
            ) {
                AccessibilityActionDispatcher.expandNotifications(service)
                DebugLog.info("accessibility notifications clicked by pointer x=$tapX y=$tapY")
            } else if (!notificationOnly || notificationTriggered) {
                AccessibilityActionDispatcher.tap(service, tapX, tapY)
            } else {
                DebugLog.info("accessibility notification action canceled outside status bar x=$tapX y=$tapY")
            }
        } else {
            DebugLog.info("accessibility pointer cancelled")
        }
    }

    fun cancel() {
        dismiss()
    }

    private fun dismiss() {
        if (!shown) return
        runCatching { windowManager.removeView(view) }
        shown = false
    }

    private fun updatePointer(
        fingerX: Float,
        fingerY: Float,
    ) {
        when (RuntimeGestureConfig.pointerControlStyle) {
            GestureConfig.POINTER_STYLE_TRACKER_CURSOR -> updateTrackerCursor(fingerX, fingerY)
            else -> updateLinePointer(fingerX, fingerY)
        }
        lastFingerX = fingerX
        lastFingerY = fingerY
        view.snapshot =
            Snapshot(
                style = RuntimeGestureConfig.pointerControlStyle,
                baseX = baseX,
                baseY = baseY,
                tipX = tipX,
                tipY = tipY,
                controlX =
                    if (RuntimeGestureConfig.pointerControlStyle == GestureConfig.POINTER_STYLE_TRACKER_CURSOR) {
                        trackerOriginX
                    } else {
                        downX
                    },
                controlY =
                    if (RuntimeGestureConfig.pointerControlStyle == GestureConfig.POINTER_STYLE_TRACKER_CURSOR) {
                        trackerOriginY
                    } else {
                        downY
                    },
                trackerX = trackerX,
                trackerY = trackerY,
                cursorX = cursorX,
                cursorY = cursorY,
                cancelled = cancelled,
            )
    }

    private fun updateLinePointer(
        fingerX: Float,
        fingerY: Float,
    ) {
        val density = service.resources.displayMetrics.density
        val scale =
            (RuntimeGestureConfig.pointerSensitivity.coerceIn(40, 220) / 100f) *
                LINE_POINTER_SCALE
        val margin = RuntimeGestureConfig.pointerMarginDp.coerceAtLeast(0) * density

        tipX = (baseX + (fingerX - downX) * scale).coerceIn(margin, screenWidth - margin)
        tipY = (baseY + (fingerY - downY) * scale).coerceIn(margin, screenHeight - margin)

        val cancelRadius = RuntimeGestureConfig.pointerRadiusDp.coerceAtLeast(32) * density
        cancelled = hypot(tipX - downX, tipY - downY) <= cancelRadius
    }

    private fun updateTrackerCursor(
        fingerX: Float,
        fingerY: Float,
    ) {
        val density = service.resources.displayMetrics.density
        val scale =
            (RuntimeGestureConfig.trackerSensitivity.coerceIn(40, 220) / 100f) *
                TRACKER_CURSOR_SCALE
        val cursorMargin = RuntimeGestureConfig.trackerCursorDp.coerceAtLeast(8) * density
        val trackerRadius = RuntimeGestureConfig.pointerRadiusDp.coerceAtLeast(32) * density
        val cancelRadius = RuntimeGestureConfig.trackerCancelRadiusDp.coerceAtLeast(24) * density

        val dx = (fingerX - lastFingerX) * scale
        val dy = (fingerY - lastFingerY) * scale
        cursorX = (cursorX + dx).coerceIn(cursorMargin, screenWidth - cursorMargin)
        cursorY = (cursorY + dy).coerceIn(cursorMargin, screenHeight - cursorMargin)

        val rawTrackerDx = fingerX - trackerOriginX
        val rawTrackerDy = fingerY - trackerOriginY
        val rawDistance = hypot(rawTrackerDx, rawTrackerDy)
        val trackerScale =
            if (rawDistance > trackerRadius && rawDistance > 0f) {
                trackerRadius / rawDistance
            } else {
                1f
            }
        trackerX = trackerOriginX + rawTrackerDx * trackerScale
        trackerY = trackerOriginY + rawTrackerDy * trackerScale

        cancelled = hypot(trackerX - trackerOriginX, trackerY - trackerOriginY) <= cancelRadius
    }

    private fun trackerCursorStart(
        fingerX: Float,
        fingerY: Float,
    ): Pair<Float, Float> {
        val density = service.resources.displayMetrics.density
        val cursorMargin = RuntimeGestureConfig.trackerCursorDp.coerceAtLeast(8) * density
        val controlRadius = RuntimeGestureConfig.pointerRadiusDp.coerceAtLeast(32) * density
        val cursorRadius = RuntimeGestureConfig.trackerCursorDp.coerceAtLeast(8) * density
        val startOffset = maxOf(controlRadius * 1.35f, cursorRadius * 2.6f, minOf(screenWidth, screenHeight) * 0.1f)
        val x =
            if (edge == EdgeGestureDetector.Edge.RIGHT) {
                fingerX - startOffset
            } else {
                fingerX + startOffset
            }
        val y = fingerY - startOffset * 0.35f
        return x.coerceIn(cursorMargin, screenWidth - cursorMargin) to
            y.coerceIn(cursorMargin, screenHeight - cursorMargin)
    }

    private fun maybeTriggerNotificationOnTouch() {
        if (notificationTriggered) return
        if (cancelled) return
        if (RuntimeGestureConfig.notificationShadeMode != GestureConfig.NOTIFICATION_SHADE_TOUCH) return

        val x: Float
        val y: Float
        when (RuntimeGestureConfig.pointerControlStyle) {
            GestureConfig.POINTER_STYLE_TRACKER_CURSOR -> {
                x = cursorX
                y = cursorY
            }

            else -> {
                x = tipX
                y = tipY
            }
        }
        if (!isInsideNotificationHotspot(x, y)) return

        notificationTriggered = true
        AccessibilityActionDispatcher.expandNotifications(service)
        DebugLog.info("accessibility notifications touched by pointer x=$x y=$y")
    }

    private fun isInsideNotificationHotspot(
        x: Float,
        y: Float,
    ): Boolean {
        val density = service.resources.displayMetrics.density
        val hotspotHeight =
            RuntimeGestureConfig.notificationTopEdgeDp
                .coerceIn(GestureConfig.NOTIFICATION_TOP_EDGE_MIN_DP, GestureConfig.NOTIFICATION_TOP_EDGE_MAX_DP) * density
        val hotspotTop = 0f
        val hotspotBottom = hotspotTop + hotspotHeight
        val startPercent =
            minOf(
                RuntimeGestureConfig.notificationHotspotStartPercent,
                RuntimeGestureConfig.notificationHotspotEndPercent,
            ).coerceIn(GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT, GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT) / 100f
        val endPercent =
            maxOf(
                RuntimeGestureConfig.notificationHotspotStartPercent,
                RuntimeGestureConfig.notificationHotspotEndPercent,
            ).coerceIn(GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT, GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT) / 100f
        return y >= hotspotTop && y <= hotspotBottom && x >= screenWidth * startPercent && x <= screenWidth * endPercent
    }

    private data class Snapshot(
        val style: String,
        val baseX: Float,
        val baseY: Float,
        val tipX: Float,
        val tipY: Float,
        val controlX: Float,
        val controlY: Float,
        val trackerX: Float,
        val trackerY: Float,
        val cursorX: Float,
        val cursorY: Float,
        val cancelled: Boolean,
    )

    private class PointerOverlayView(context: android.content.Context) : View(context) {
        var snapshot: Snapshot? = null

        private val linePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }
        private val fillPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
            }
        private val circlePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
            }

        override fun onDraw(canvas: Canvas) {
            if (RuntimeGestureConfig.pointerControlAlpha <= 0) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            }
            val data = snapshot ?: return
            val color = pointerColor()
            if (data.style == GestureConfig.POINTER_STYLE_TRACKER_CURSOR) {
                drawTrackerCursor(canvas, data, color)
            } else {
                drawLinePointer(canvas, data, color)
            }
        }

        private fun drawLinePointer(
            canvas: Canvas,
            data: Snapshot,
            color: Int,
        ) {
            val density = resources.displayMetrics.density
            val controlRadius = RuntimeGestureConfig.pointerRadiusDp.coerceAtLeast(32) * density
            val alpha = RuntimeGestureConfig.pointerControlAlpha.coerceIn(0, 255)
            if (alpha > 0) {
                circlePaint.color = Color.argb(alpha, 255, 255, 255)
                circlePaint.strokeWidth = 1.5f * density
                canvas.drawCircle(data.controlX, data.controlY, controlRadius, circlePaint)
            }

            linePaint.color = color
            linePaint.strokeWidth = RuntimeGestureConfig.pointerLineDp.coerceAtLeast(1) * density
            canvas.drawLine(data.baseX, data.baseY, data.tipX, data.tipY, linePaint)
            drawArrowHead(canvas, data.baseX, data.baseY, data.tipX, data.tipY, color)
        }

        private fun drawTrackerCursor(
            canvas: Canvas,
            data: Snapshot,
            color: Int,
        ) {
            val density = resources.displayMetrics.density
            val controlRadius = RuntimeGestureConfig.pointerRadiusDp.coerceAtLeast(32) * density
            val cancelRadius = RuntimeGestureConfig.trackerCancelRadiusDp.coerceAtLeast(24) * density
            val ballRadius = RuntimeGestureConfig.trackerBallDp.coerceAtLeast(6) * density
            val cursorRadius = RuntimeGestureConfig.trackerCursorDp.coerceAtLeast(8) * density
            val alpha = RuntimeGestureConfig.pointerControlAlpha.coerceIn(0, 255)

            if (alpha > 0) {
                circlePaint.color = Color.argb(alpha, 255, 255, 255)
                circlePaint.strokeWidth = 1.5f * density
                canvas.drawCircle(data.controlX, data.controlY, controlRadius, circlePaint)
                circlePaint.color = Color.argb((alpha * 0.8f).toInt(), 255, 80, 80)
                canvas.drawCircle(data.controlX, data.controlY, cancelRadius, circlePaint)
            }

            fillPaint.color = if (data.cancelled) Color.argb(210, 255, 80, 80) else color
            canvas.drawCircle(data.trackerX, data.trackerY, ballRadius, fillPaint)

            circlePaint.color = color
            circlePaint.strokeWidth = 2.5f * density
            canvas.drawCircle(data.cursorX, data.cursorY, cursorRadius, circlePaint)
            canvas.drawLine(data.cursorX - cursorRadius, data.cursorY, data.cursorX + cursorRadius, data.cursorY, circlePaint)
            canvas.drawLine(data.cursorX, data.cursorY - cursorRadius, data.cursorX, data.cursorY + cursorRadius, circlePaint)
        }

        private fun drawArrowHead(
            canvas: Canvas,
            baseX: Float,
            baseY: Float,
            tipX: Float,
            tipY: Float,
            color: Int,
        ) {
            val density = resources.displayMetrics.density
            val arrowSize = RuntimeGestureConfig.pointerArrowDp.coerceAtLeast(8) * density
            val angle = atan2(tipY - baseY, tipX - baseX)
            val wingAngleA = angle + ARROW_WING_ANGLE_RADIANS
            val wingAngleB = angle - ARROW_WING_ANGLE_RADIANS
            linePaint.color = color
            linePaint.strokeWidth = RuntimeGestureConfig.pointerLineDp.coerceAtLeast(1) * density
            canvas.drawLine(
                tipX,
                tipY,
                tipX - cos(wingAngleA) * arrowSize,
                tipY - sin(wingAngleA) * arrowSize,
                linePaint,
            )
            canvas.drawLine(
                tipX,
                tipY,
                tipX - cos(wingAngleB) * arrowSize,
                tipY - sin(wingAngleB) * arrowSize,
                linePaint,
            )
        }

        private fun pointerColor(): Int {
            return Color.rgb(
                RuntimeGestureConfig.pointerColorRed.coerceIn(0, 255),
                RuntimeGestureConfig.pointerColorGreen.coerceIn(0, 255),
                RuntimeGestureConfig.pointerColorBlue.coerceIn(0, 255),
            )
        }
    }

    private companion object {
        const val LINE_POINTER_SCALE = 3.0f
        const val TRACKER_CURSOR_SCALE = 3.0f
        const val ARROW_WING_ANGLE_RADIANS = 0.68f
        const val PREFERRED_REFRESH_RATE = 120f
    }
}
