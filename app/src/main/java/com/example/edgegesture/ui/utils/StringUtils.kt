package com.example.edgegesture.ui.utils

import com.example.edgegesture.GestureConfig
import java.util.Locale

fun t(
    zh: String,
    en: String,
): String {
    return if (Locale.getDefault().language.lowercase(Locale.ROOT) == "zh") zh else en
}

fun edgeLabel(edge: String): String {
    return when (edge) {
        "left" -> t("左边缘", "Left Edge")
        "right" -> t("右边缘", "Right Edge")
        "top" -> t("上边缘", "Top Edge")
        "bottom" -> t("下边缘", "Bottom Edge")
        else -> edge
    }
}

fun gestureLabel(gesture: String): String {
    return when (gesture) {
        "click" -> t("单击", "Tap")
        "double_click" -> t("双击", "Double Tap")
        "long_press" -> t("长按", "Long Press")
        "swipe_up" -> t("上划", "Swipe Up")
        "swipe_down" -> t("下划", "Swipe Down")
        "swipe_left" -> t("左划", "Swipe Left")
        "swipe_right" -> t("右划", "Swipe Right")
        else -> gesture
    }
}

fun actionLabel(action: String): String {
    return when (action) {
        GestureConfig.ACTION_NONE -> t("无动作", "No Action")
        GestureConfig.ACTION_ONE_HAND_TAP -> t("单手点击屏幕", "One-Hand Tap")
        GestureConfig.ACTION_RECENTS -> t("最近任务", "Recents")
        else -> action
    }
}

fun notificationShadeModeLabel(mode: String): String {
    return when (mode) {
        GestureConfig.NOTIFICATION_SHADE_TOUCH -> t("碰到就下拉", "Pull on Touch")
        GestureConfig.NOTIFICATION_SHADE_RELEASE -> t("松手点击下拉", "Pull on Release")
        else -> mode
    }
}

fun actionValuesForGesture(gesture: String): List<String> {
    return GestureConfig.actionValuesForGesture(gesture)
}
