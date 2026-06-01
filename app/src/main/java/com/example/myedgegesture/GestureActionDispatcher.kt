package com.example.myedgegesture

import android.content.Context
import android.hardware.input.InputManager
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyCharacterMap
import android.view.KeyEvent
import de.robv.android.xposed.XposedBridge

object GestureActionDispatcher {

    fun perform(
        context: Context,
        edge: EdgeGestureDetector.Edge,
        zone: String,
        gesture: String,
        startX: Float,
        startY: Float,
        x: Float,
        y: Float
    ) {
        val action = RuntimeGestureConfig.actionFor(edge, gesture)
        DebugLog.info("dispatch edge=$edge zone=$zone gesture=$gesture action=$action")

        if (!DeviceState.canRunGestures(context)) {
            DebugLog.info("gesture ignored because device is locked or screen is off")
            return
        }

        when (action) {
            GestureConfig.ACTION_BACK -> injectSystemKey(context, KeyEvent.KEYCODE_BACK, "back")
            GestureConfig.ACTION_HOME -> injectSystemKey(context, KeyEvent.KEYCODE_HOME, "home")
            GestureConfig.ACTION_RECENTS -> toggleRecents(context)
            GestureConfig.ACTION_NOTIFICATIONS -> OneHandPointer.start(
                context,
                x,
                y,
                x,
                y,
                notificationOnly = true
            )
            GestureConfig.ACTION_ONE_HAND_TAP -> OneHandPointer.start(context, x, y, x, y)
            GestureConfig.ACTION_SCREENSHOT -> performScreenshot(context)
            GestureConfig.ACTION_SPLIT_SCREEN -> performSplitScreen(context)
            GestureConfig.ACTION_POWER_MENU -> performPowerMenu(context)
            else -> DebugLog.info("no action mapped")
        }

        if (action != GestureConfig.ACTION_NONE && RuntimeGestureConfig.hapticFeedbackEnabled) {
            performHapticFeedback(context)
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

    private fun performScreenshot(context: Context) {
        // Inject KEYCODE_SYSRQ for screenshot
        try {
            injectSystemKey(context, KeyEvent.KEYCODE_SYSRQ, "screenshot")
        } catch (_: Throwable) {
            // Fallback: simulate POWER + VOLUME_DOWN combo
            try {
                val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
                val injectMethod = inputManager.javaClass.getMethod(
                    "injectInputEvent",
                    InputEvent::class.java,
                    Int::class.javaPrimitiveType
                )
                val now = SystemClock.uptimeMillis()

                val powerDown = KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER, 0, 0,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_FROM_SYSTEM, InputDevice.SOURCE_KEYBOARD)
                val volDown = KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_DOWN, 0, 0,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_FROM_SYSTEM, InputDevice.SOURCE_KEYBOARD)
                val volUp = KeyEvent(now, now + 100, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_DOWN, 0, 0,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_FROM_SYSTEM, InputDevice.SOURCE_KEYBOARD)
                val powerUp = KeyEvent(now, now + 100, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_POWER, 0, 0,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_FROM_SYSTEM, InputDevice.SOURCE_KEYBOARD)

                InputInjectionGuard.runIgnoring {
                    injectMethod.invoke(inputManager, powerDown, 0)
                    injectMethod.invoke(inputManager, volDown, 0)
                    injectMethod.invoke(inputManager, volUp, 0)
                    injectMethod.invoke(inputManager, powerUp, 0)
                }
                XposedBridge.log("EdgeGesture: action -> screenshot (power+vol fallback)")
            } catch (t: Throwable) {
                XposedBridge.log("EdgeGesture: screenshot fallback failed: ${t.message}")
            }
        }
    }

    private fun performSplitScreen(context: Context) {
        if (callStatusBarMethod("toggleSplitScreen", "splitScreen")) {
            XposedBridge.log("EdgeGesture: action -> split_screen")
            return
        }
        // Fallback: try toggleSplitScreenMode
        if (callStatusBarMethod("toggleSplitScreenMode", "splitScreen")) {
            XposedBridge.log("EdgeGesture: action -> split_screen (fallback)")
            return
        }
        XposedBridge.log("EdgeGesture: split_screen not available via StatusBar")
    }

    private fun performPowerMenu(context: Context) {
        // Long press power key to show power menu
        try {
            val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
            val injectMethod = inputManager.javaClass.getMethod(
                "injectInputEvent",
                InputEvent::class.java,
                Int::class.javaPrimitiveType
            )
            val now = SystemClock.uptimeMillis()
            val downEvent = KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                KeyEvent.FLAG_FROM_SYSTEM or KeyEvent.FLAG_LONG_PRESS,
                InputDevice.SOURCE_KEYBOARD)
            val upEvent = KeyEvent(now, now + 500, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_POWER, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_FROM_SYSTEM, InputDevice.SOURCE_KEYBOARD)

            InputInjectionGuard.runIgnoring {
                injectMethod.invoke(inputManager, downEvent, 0)
            }
            // Delay before key up to simulate long press
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                InputInjectionGuard.runIgnoring {
                    injectMethod.invoke(inputManager, upEvent, 0)
                }
            }, 500)
            XposedBridge.log("EdgeGesture: action -> power_menu")
        } catch (t: Throwable) {
            XposedBridge.log("EdgeGesture: power_menu failed: ${t.message}")
        }
    }

    private fun toggleRecents(context: Context) {
        if (toggleRecentsByStatusBar()) {
            XposedBridge.log("EdgeGesture: action -> recents")
            return
        }

        injectSystemKey(context, KeyEvent.KEYCODE_APP_SWITCH, "recents")
    }

    private fun toggleRecentsByStatusBar(): Boolean {
        return callStatusBarMethod("toggleRecentApps", "toggleRecents")
    }

    private fun callStatusBarMethod(methodName: String, logName: String): Boolean {
        return try {
            val service = Class.forName("android.os.ServiceManager")
                .getMethod("getService", String::class.java)
                .invoke(null, "statusbar")
                ?: return false
            val stubClass = Class.forName("com.android.internal.statusbar.IStatusBarService\$Stub")
            val serviceInterface = stubClass.getMethod("asInterface", android.os.IBinder::class.java)
                .invoke(null, service)
            val method = serviceInterface.javaClass.methods.firstOrNull { it.name == methodName }
                ?: return false

            InputInjectionGuard.runIgnoring {
                method.invoke(serviceInterface)
            }
            true
        } catch (t: Throwable) {
            XposedBridge.log("EdgeGesture: $logName failed: ${t.message}")
            false
        }
    }

    private fun injectSystemKey(context: Context, keyCode: Int, actionName: String) {
        try {
            val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
            val injectMethod = inputManager.javaClass.getMethod(
                "injectInputEvent",
                InputEvent::class.java,
                Int::class.javaPrimitiveType
            )
            val now = SystemClock.uptimeMillis()

            listOf(KeyEvent.ACTION_DOWN, KeyEvent.ACTION_UP).forEach { action ->
                val event = KeyEvent(
                    now,
                    now,
                    action,
                    keyCode,
                    0,
                    0,
                    KeyCharacterMap.VIRTUAL_KEYBOARD,
                    0,
                    KeyEvent.FLAG_FROM_SYSTEM,
                    InputDevice.SOURCE_KEYBOARD
                )
                InputInjectionGuard.runIgnoring {
                    injectMethod.invoke(inputManager, event, 0)
                }
            }
            XposedBridge.log("EdgeGesture: action -> $actionName")
        } catch (t: Throwable) {
            XposedBridge.log("EdgeGesture: injectSystemKey failed: ${t.message}")
        }
    }
}
