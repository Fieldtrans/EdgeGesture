# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# 保留 Xposed 相关
-keep class com.example.myedgegesture.MainHook { *; }
-keep class com.example.myedgegesture.MainHook$* { *; }
-keep class com.example.myedgegesture.GestureConfig { *; }
-keep class com.example.myedgegesture.RuntimeGestureConfig { *; }
-keep class com.example.myedgegesture.OneHandPointer { *; }
-keep class com.example.myedgegesture.OneHandPointer$* { *; }
-keep class com.example.myedgegesture.GestureActionDispatcher { *; }
-keep class com.example.myedgegesture.EdgeGestureDetector { *; }
-keep class com.example.myedgegesture.EdgeGestureDetector$* { *; }
-keep class com.example.myedgegesture.DeviceState { *; }
-keep class com.example.myedgegesture.DebugLog { *; }
-keep class com.example.myedgegesture.HookHealth { *; }
-keep class com.example.myedgegesture.InputInjectionGuard { *; }
-keep class com.example.myedgegesture.SavedConfigBroadcaster { *; }
-keep class com.example.myedgegesture.ConfigBootReceiver { *; }
-keep class com.example.myedgegesture.AccessibilityPointerController { *; }
-keep class com.example.myedgegesture.EdgeAccessibilityService { *; }
-keep class com.example.myedgegesture.AccessibilityActionDispatcher { *; }

# 保留枚举
-keepclassmembers enum * { *; }

# 保留 JSON 序列化字段
-keepclassmembers class com.example.myedgegesture.data.model.SettingsState { *; }

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
