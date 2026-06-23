# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# 保留 Xposed 相关
-keep class com.example.edgegesture.MainHook { *; }
-keep class com.example.edgegesture.MainHook$* { *; }
-keep class com.example.edgegesture.GestureConfig { *; }
-keep class com.example.edgegesture.RuntimeGestureConfig { *; }
-keep class com.example.edgegesture.OneHandPointer { *; }
-keep class com.example.edgegesture.OneHandPointer$* { *; }
-keep class com.example.edgegesture.GestureActionDispatcher { *; }
-keep class com.example.edgegesture.EdgeGestureDetector { *; }
-keep class com.example.edgegesture.EdgeGestureDetector$* { *; }
-keep class com.example.edgegesture.DeviceState { *; }
-keep class com.example.edgegesture.DebugLog { *; }
-keep class com.example.edgegesture.HookHealth { *; }
-keep class com.example.edgegesture.InputInjectionGuard { *; }
-keep class com.example.edgegesture.SavedConfigBroadcaster { *; }
-keep class com.example.edgegesture.ConfigBootReceiver { *; }
-keep class com.example.edgegesture.AccessibilityPointerController { *; }
-keep class com.example.edgegesture.EdgeAccessibilityService { *; }
-keep class com.example.edgegesture.AccessibilityActionDispatcher { *; }

# 保留枚举
-keepclassmembers enum * { *; }

# 保留 JSON 序列化字段
-keepclassmembers class com.example.edgegesture.data.model.SettingsState { *; }

# 保留反射调用的方法
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# 保留行号信息用于崩溃报告
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 混淆优化
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
-allowaccessmodification
-repackageclasses ''
