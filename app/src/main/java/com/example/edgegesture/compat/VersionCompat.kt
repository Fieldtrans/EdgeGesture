package com.example.edgegesture.compat

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

/**
 * 版本兼容性工具类
 * 提供统一的 Android 版本检查方法
 */
object VersionCompat {
    /**
     * Android 8.0 Oreo (API 26)
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun isOreoOrHigher(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    /**
     * Android 9.0 Pie (API 28)
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    fun isPieOrHigher(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    /**
     * Android 10 (API 29)
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    fun isQOrHigher(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * Android 11 (API 30)
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    fun isROrHigher(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    /**
     * Android 12 (API 31)
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isSOrHigher(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    /**
     * Android 13 (API 33)
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    fun isTiramisuOrHigher(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    /**
     * Android 14 (API 34)
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun isUpsideDownCakeOrHigher(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    /**
     * 获取当前 SDK 版本
     */
    fun getCurrentSdkVersion(): Int = Build.VERSION.SDK_INT

    /**
     * 获取当前 Android 版本名称
     */
    fun getVersionName(): String = Build.VERSION.RELEASE

    /**
     * 检查是否支持特定功能
     */
    fun supportsInputFilter(): Boolean = isOreoOrHigher()

    /**
     * 检查是否支持 RECEIVER_EXPORTED 标志
     */
    fun supportsReceiverExported(): Boolean = isTiramisuOrHigher()
}
