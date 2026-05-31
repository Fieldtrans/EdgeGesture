package com.example.myedgegesture

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import kotlin.math.abs

class EdgeAccessibilityService : AccessibilityService() {
    private val edgeViews = mutableMapOf<EdgeGestureDetector.Edge, View>()
    private val touchControllers = mutableMapOf<EdgeGestureDetector.Edge, EdgeTouchController>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var receiverRegistered = false
    private var overlaySignature: OverlaySignature? = null

    private val configReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != GestureConfig.ACTION_CONFIG_CHANGED) return
            RuntimeGestureConfig.updateFromIntent(intent)
            refreshEdgeOverlays()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        loadRuntimeConfig()
        registerConfigReceiver()
        refreshEdgeOverlays()
        DebugLog.always("accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        refreshEdgeOverlays()
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        removeEdgeOverlays()
        unregisterConfigReceiver()
        super.onDestroy()
    }

    private fun loadRuntimeConfig() {
        RuntimeGestureConfig.updateFromPreferences(
            getSharedPreferences(GestureConfig.PREFS_NAME, MODE_PRIVATE)
        )
    }

    private fun registerConfigReceiver() {
        if (receiverRegistered) return
        registerReceiver(
            configReceiver,
            IntentFilter(GestureConfig.ACTION_CONFIG_CHANGED),
            RECEIVER_NOT_EXPORTED
        )
        receiverRegistered = true
    }

    private fun unregisterConfigReceiver() {
        if (!receiverRegistered) return
        runCatching { unregisterReceiver(configReceiver) }
        receiverRegistered = false
    }

    private fun refreshEdgeOverlays() {
        if (!RuntimeGestureConfig.enabled) {
            removeEdgeOverlays()
            overlaySignature = OverlaySignature.disabled()
            DebugLog.info("accessibility overlay disabled by config")
            return
        }

        if (isEnhancedEngineActive()) {
            removeEdgeOverlays()
            overlaySignature = OverlaySignature.disabled()
            DebugLog.info("accessibility overlay paused because LSPosed engine is active")
            return
        }

        val metrics = windowManager.currentWindowMetrics
        val bounds = metrics.bounds
        val screenWidth = bounds.width()
        val screenHeight = bounds.height()
        val edgeWidthPx = accessibilityEdgeWidthPx(screenWidth)
        val systemGestureGapPx = systemGestureGapPx(screenWidth)
        val top = activeRegionTop(screenHeight)
        val bottom = activeRegionBottom(screenHeight)
        val height = (bottom - top).coerceAtLeast(1)
        val leftSupported = hasSupportedAccessibilityAction(EdgeGestureDetector.Edge.LEFT)
        val rightSupported = hasSupportedAccessibilityAction(EdgeGestureDetector.Edge.RIGHT)
        val nextSignature = OverlaySignature(
            enabled = true,
            edgeWidthPx = edgeWidthPx,
            systemGestureGapPx = systemGestureGapPx,
            top = top,
            height = height,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            leftSupported = leftSupported,
            rightSupported = rightSupported
        )
        if (overlaySignature == nextSignature) {
            return
        }

        removeEdgeOverlays()

        if (leftSupported) {
            addEdgeOverlay(
                EdgeGestureDetector.Edge.LEFT,
                edgeWidthPx,
                height,
                top,
                screenWidth,
                screenHeight,
                systemGestureGapPx
            )
        }
        if (rightSupported) {
            addEdgeOverlay(
                EdgeGestureDetector.Edge.RIGHT,
                edgeWidthPx,
                height,
                top,
                screenWidth,
                screenHeight,
                systemGestureGapPx
            )
        }
        overlaySignature = nextSignature

        DebugLog.info(
            "accessibility overlays refreshed width=${edgeWidthPx}px height=${height}px top=${top}px"
        )
    }

    private fun addEdgeOverlay(
        edge: EdgeGestureDetector.Edge,
        width: Int,
        height: Int,
        top: Int,
        screenWidth: Int,
        screenHeight: Int,
        systemGestureGapPx: Int
    ) {
        val controller = EdgeTouchController(this, edge, screenWidth, screenHeight)
        val view = View(this).apply {
            setBackgroundColor(0x00000000)
            isHapticFeedbackEnabled = false
            setOnTouchListener { _, event -> controller.onTouch(event) }
        }

        val params = WindowManager.LayoutParams(
            width,
            height,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = if (edge == EdgeGestureDetector.Edge.LEFT) {
                systemGestureGapPx
            } else {
                screenWidth - width - systemGestureGapPx
            }
            y = top
            preferredRefreshRate = PREFERRED_REFRESH_RATE
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }

        runCatching {
            windowManager.addView(view, params)
            edgeViews[edge] = view
            touchControllers[edge] = controller
        }.onFailure {
            DebugLog.always("accessibility add overlay failed edge=$edge message=${it.message}")
        }
    }

    private fun removeEdgeOverlays() {
        edgeViews.values.forEach { view ->
            runCatching { windowManager.removeView(view) }
        }
        edgeViews.clear()
        touchControllers.clear()
    }

    private fun schedulePassThroughTap(x: Float, y: Float, delayMs: Long): Runnable {
        val runnable = Runnable {
            DebugLog.info("accessibility pass-through tap x=$x y=$y")
            removeEdgeOverlays()
            overlaySignature = null
            mainHandler.postDelayed({
                AccessibilityActionDispatcher.tap(this, x, y) {
                    mainHandler.postDelayed({ refreshEdgeOverlays() }, PASS_THROUGH_RESTORE_DELAY_MS)
                }
            }, PASS_THROUGH_TAP_DELAY_MS)
        }
        mainHandler.postDelayed(runnable, delayMs.coerceAtLeast(0L))
        return runnable
    }

    private fun cancelPassThroughTap(runnable: Runnable?) {
        if (runnable != null) {
            mainHandler.removeCallbacks(runnable)
        }
    }

    private data class OverlaySignature(
        val enabled: Boolean,
        val edgeWidthPx: Int,
        val systemGestureGapPx: Int,
        val top: Int,
        val height: Int,
        val screenWidth: Int,
        val screenHeight: Int,
        val leftSupported: Boolean,
        val rightSupported: Boolean
    ) {
        companion object {
            fun disabled() = OverlaySignature(
                enabled = false,
                edgeWidthPx = 0,
                systemGestureGapPx = 0,
                top = 0,
                height = 0,
                screenWidth = 0,
                screenHeight = 0,
                leftSupported = false,
                rightSupported = false
            )
        }
    }

    private fun hasSupportedAccessibilityAction(edge: EdgeGestureDetector.Edge): Boolean {
        val swipeAction = RuntimeGestureConfig.actionFor(edge, "swipe_up")
        val doubleAction = RuntimeGestureConfig.actionFor(edge, "double_click")
        return swipeAction in ACCESSIBILITY_SUPPORTED_SWIPE_ACTIONS ||
                doubleAction == GestureConfig.ACTION_RECENTS
    }

    private fun isEnhancedEngineActive(): Boolean {
        return runCatching {
            val prefs = createDeviceProtectedStorageContext()
                .getSharedPreferences(GestureConfig.STATUS_PREFS_NAME, MODE_PRIVATE)
            val startedAt = prefs.getLong(GestureConfig.KEY_STATUS_STARTED_AT, 0L)
            val now = SystemClock.elapsedRealtime()
            startedAt > 0L && startedAt <= now
        }.getOrDefault(false)
    }

    private fun accessibilityEdgeWidthPx(screenWidth: Int): Int {
        val density = resources.displayMetrics.density
        val requestedPx = RuntimeGestureConfig.edgeWidthDp
            .coerceAtLeast(GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP) * density
        val hardLimitPx = GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_DP * density
        val percentLimitPx = screenWidth * GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_SCREEN_PERCENT
        return requestedPx
            .coerceAtMost(hardLimitPx)
            .coerceAtMost(percentLimitPx)
            .toInt()
            .coerceAtLeast((GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP * density).toInt())
    }

    private fun systemGestureGapPx(screenWidth: Int): Int {
        val density = resources.displayMetrics.density
        return (GestureConfig.ACCESSIBILITY_SYSTEM_GESTURE_GAP_DP * density)
            .coerceAtMost(screenWidth * GestureConfig.ACCESSIBILITY_MAX_SYSTEM_GESTURE_GAP_SCREEN_PERCENT)
            .toInt()
            .coerceAtLeast(0)
    }

    private fun activeRegionTop(screenHeight: Int): Int {
        val start = minOf(
            RuntimeGestureConfig.triggerRegionStartPercent,
            RuntimeGestureConfig.triggerRegionEndPercent
        ).coerceIn(0, 100)
        return (screenHeight * start / 100f).toInt()
    }

    private fun activeRegionBottom(screenHeight: Int): Int {
        val end = maxOf(
            RuntimeGestureConfig.triggerRegionStartPercent,
            RuntimeGestureConfig.triggerRegionEndPercent
        ).coerceIn(0, 100)
        return (screenHeight * end / 100f).toInt()
    }

    private val windowManager: WindowManager
        get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private class EdgeTouchController(
        private val service: EdgeAccessibilityService,
        private val edge: EdgeGestureDetector.Edge,
        private val screenWidth: Int,
        private val screenHeight: Int
    ) {
        private var session: TouchSession? = null
        private var pendingTap: PendingTap? = null

        fun onTouch(event: MotionEvent): Boolean {
            if (!RuntimeGestureConfig.enabled || !DeviceState.canRunGestures(service)) {
                session = null
                return false
            }

            return when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    session = TouchSession(
                        downX = event.rawX,
                        downY = event.rawY,
                        createdAt = event.eventTime,
                        zone = verticalZone(event.rawY)
                    )
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    handleMove(event)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    handleUp(event)
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    session?.pointerController?.cancel()
                    session = null
                    true
                }

                else -> true
            }
        }

        private fun handleMove(event: MotionEvent) {
            val current = session ?: return
            current.pointerController?.let {
                it.moveTo(event.rawX, event.rawY)
                return
            }
            if (current.swipeUpConfirmed) return

            val dx = event.rawX - current.downX
            val dy = event.rawY - current.downY
            val thresholdPx = RuntimeGestureConfig.swipeDistanceDp *
                    service.resources.displayMetrics.density
            if (isConfirmedSwipeUp(dx, dy, thresholdPx)) {
                current.swipeUpConfirmed = true
                pendingTap?.let { service.cancelPassThroughTap(it.passThroughTap) }
                pendingTap = null
                val action = RuntimeGestureConfig.actionFor(edge, "swipe_up")
                if (action == GestureConfig.ACTION_ONE_HAND_TAP ||
                    action == GestureConfig.ACTION_NOTIFICATIONS
                ) {
                    current.pointerController = AccessibilityPointerController(
                        service = service,
                        edge = edge,
                        screenWidth = screenWidth,
                        screenHeight = screenHeight,
                        downX = current.downX,
                        downY = current.downY,
                        notificationOnly = action == GestureConfig.ACTION_NOTIFICATIONS
                    ).also {
                        it.start(event.rawX, event.rawY)
                    }
                    DebugLog.info("accessibility pointer started edge=$edge")
                } else {
                    DebugLog.info("accessibility swipe confirmed edge=$edge")
                }
            }
        }

        private fun handleUp(event: MotionEvent) {
            val current = session ?: return
            session = null

            current.pointerController?.let {
                it.moveTo(event.rawX, event.rawY)
                it.finish()
                return
            }

            if (current.swipeUpConfirmed) {
                AccessibilityActionDispatcher.perform(
                    service = service,
                    edge = edge,
                    zone = current.zone,
                    gesture = "swipe_up",
                    startX = current.downX,
                    startY = current.downY,
                    x = event.rawX,
                    y = event.rawY
                )
                return
            }

            if (isTap(current, event)) {
                handleTap(current, event)
            } else {
                pendingTap = null
            }
        }

        private fun handleTap(current: TouchSession, event: MotionEvent) {
            val pending = pendingTap
            if (pending != null && isMatchingDoubleTap(pending, event)) {
                service.cancelPassThroughTap(pending.passThroughTap)
                pendingTap = null
                AccessibilityActionDispatcher.perform(
                    service = service,
                    edge = edge,
                    zone = current.zone,
                    gesture = "double_click",
                    startX = pending.x,
                    startY = pending.y,
                    x = event.rawX,
                    y = event.rawY
                )
            } else {
                val doubleTapEnabled = RuntimeGestureConfig.actionFor(edge, "double_click") ==
                        GestureConfig.ACTION_RECENTS
                val passThroughTap = service.schedulePassThroughTap(
                    event.rawX,
                    event.rawY,
                    0L
                )
                pendingTap = if (doubleTapEnabled) PendingTap(
                    x = event.rawX,
                    y = event.rawY,
                    eventTime = event.eventTime,
                    passThroughTap = passThroughTap
                ) else null
            }
        }

        private fun isTap(current: TouchSession, event: MotionEvent): Boolean {
            val density = service.resources.displayMetrics.density
            val tapSlop = TAP_SLOP_DP * density
            return event.eventTime - current.createdAt <= TAP_MAX_HOLD_MS &&
                    abs(event.rawX - current.downX) <= tapSlop &&
                    abs(event.rawY - current.downY) <= tapSlop
        }

        private fun isMatchingDoubleTap(pending: PendingTap, event: MotionEvent): Boolean {
            val density = service.resources.displayMetrics.density
            val slop = DOUBLE_TAP_SLOP_DP * density
            return event.eventTime - pending.eventTime <= doubleTapTimeoutMs() &&
                    abs(event.rawX - pending.x) <= slop &&
                    abs(event.rawY - pending.y) <= slop &&
                    RuntimeGestureConfig.actionFor(edge, "double_click") == GestureConfig.ACTION_RECENTS
        }

        private fun doubleTapTimeoutMs(): Long {
            return RuntimeGestureConfig.doubleTapTimeoutMs.coerceIn(
                MIN_DOUBLE_TAP_TIMEOUT_MS,
                MAX_DOUBLE_TAP_TIMEOUT_MS
            ).toLong()
        }

        private fun isConfirmedSwipeUp(dx: Float, dy: Float, thresholdPx: Float): Boolean {
            if (dy >= 0f) return false
            val vertical = abs(dy)
            val horizontal = abs(dx)
            val tolerance = RuntimeGestureConfig.swipeAngleDegrees.coerceIn(5, 85)
            val verticalRatio = (1.65f - (tolerance - 5f) / 80f * 0.75f).coerceIn(0.9f, 1.65f)
            return vertical >= thresholdPx && vertical > horizontal * verticalRatio
        }

        private fun verticalZone(rawY: Float): String {
            return when {
                rawY < screenHeight * 0.33f -> "top"
                rawY < screenHeight * 0.66f -> "mid"
                else -> "bottom"
            }
        }

        private data class TouchSession(
            val downX: Float,
            val downY: Float,
            val createdAt: Long,
            val zone: String,
            var swipeUpConfirmed: Boolean = false,
            var pointerController: AccessibilityPointerController? = null
        )

        private data class PendingTap(
            val x: Float,
            val y: Float,
            val eventTime: Long,
            val passThroughTap: Runnable?
        )
    }

    private companion object {
        const val PREFERRED_REFRESH_RATE = 120f
        const val TAP_MAX_HOLD_MS = 260L
        const val MIN_DOUBLE_TAP_TIMEOUT_MS = 120
        const val MAX_DOUBLE_TAP_TIMEOUT_MS = 320
        const val TAP_SLOP_DP = 14f
        const val DOUBLE_TAP_SLOP_DP = 64f
        const val PASS_THROUGH_TAP_DELAY_MS = 8L
        const val PASS_THROUGH_RESTORE_DELAY_MS = 12L
        val ACCESSIBILITY_SUPPORTED_SWIPE_ACTIONS = setOf(
            GestureConfig.ACTION_BACK,
            GestureConfig.ACTION_HOME,
            GestureConfig.ACTION_RECENTS,
            GestureConfig.ACTION_NOTIFICATIONS,
            GestureConfig.ACTION_ONE_HAND_TAP
        )
    }
}
