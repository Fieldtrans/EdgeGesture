# EdgeGesture

[![Release](https://img.shields.io/github/v/release/Fieldtrans/EdgeGesture?label=release)](https://github.com/Fieldtrans/EdgeGesture/releases/latest)
[![Android CI](https://github.com/Fieldtrans/EdgeGesture/actions/workflows/android.yml/badge.svg)](https://github.com/Fieldtrans/EdgeGesture/actions/workflows/android.yml)
[![Downloads](https://img.shields.io/github/downloads/Fieldtrans/EdgeGesture/total?label=downloads)](https://github.com/Fieldtrans/EdgeGesture/releases)
[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![LSPosed](https://img.shields.io/badge/LSPosed-module-6f42c1)](https://github.com/LSPosed/LSPosed)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

**语言 / Language:** 中文 | [English](#english)

[下载 APK](https://github.com/Fieldtrans/EdgeGesture/releases/latest) | [文档目录](docs/README.md) | [安装指南](docs/INSTALL.zh-CN.md) | [排错指南](docs/TROUBLESHOOTING.zh-CN.md)

EdgeGesture 是一个单手边缘手势工具，用来解决大屏手机单手点不到屏幕上方或远处区域的问题。它支持免 Root 的无障碍模式，也支持 LSPosed/Xposed 增强模式。

![EdgeGesture 演示](docs/demo.gif)

<p align="center">
  <img src="docs/screenshots/line-pointer.jpg" width="260" alt="直线箭头模式" />
  <img src="docs/screenshots/tracker-cursor.jpg" width="260" alt="摇杆光标模式" />
</p>

## 功能亮点

- 从边缘上划，触发单手点击、返回、主页或最近任务。
- 边缘双击可触发最近任务。
- 支持免 Root 无障碍模式，也支持 Root + LSPosed 增强模式。
- 直线箭头模式：松手后点击箭头尖的位置。
- Tracker + Cursor 摇杆光标模式。
- 双击等待时间可调，方便在点击穿透速度和双击识别稳定性之间取舍。
- 可调触发区域、控制圆、指针速度、平滑度、颜色、取消时间和取消距离。
- 支持顶部触发通知栏下拉，并带轻量预动画。
- 支持配置导入和导出。

## 使用要求

- 普通模式：无需 Root，需要开启 EdgeGesture 无障碍服务。
- 增强模式：需要 Root，并安装启用 LSPosed。
- 支持 Android 8.0+ (API 26+)，目标 Android 14 (API 35)。
- 使用 LSPosed 增强模式时，作用域需要包含 Android 系统/框架。

## 安装方法

1. 在 [Releases](https://github.com/Fieldtrans/EdgeGesture/releases/latest) 下载最新版 APK。
2. 安装 APK。
3. 免 Root 使用：打开 EdgeGesture，进入系统无障碍设置并启用 EdgeGesture。
4. LSPosed 增强使用：在 LSPosed 中启用 EdgeGesture，并确认作用域包含 Android 系统/框架。
5. 使用 LSPosed 增强模式时，重启手机。
6. 打开 EdgeGesture，启用你需要的手势模式并保存配置。

如果手势没有反应，请在 LSPosed 日志中搜索 `EdgeGesture`。

更详细的步骤见 [安装指南](docs/INSTALL.zh-CN.md) 和 [排错指南](docs/TROUBLESHOOTING.zh-CN.md)。

### 直线箭头

从右侧边缘上划后出现绿色箭头。拇指只需要在小范围里移动，箭头会映射到更大的屏幕范围。松手后点击箭头尖所在位置。

### Tracker + Cursor

从右侧边缘上划后出现摇杆球和光标。通过摇杆区域控制光标移动，松手时如果不在取消圆内，就会执行点击。

## 编译

```bash
# 构建 Release 版本
./gradlew :app:assembleRelease

# 运行测试
./gradlew test

# 代码质量检查
./gradlew detekt ktlintCheck
```

Release APK 输出位置：

```text
app/build/outputs/apk/release/app-release.apk
```

开发者请参考 [快速开始指南](QUICKSTART.md)。

## 当前状态

当前版本：`1.3.0`。

详见 [改进报告](IMPROVEMENTS.md) 和 [开发指南](QUICKSTART.md)。

该模块会 hook `system_server` 的输入处理逻辑，请谨慎使用，并在测试自定义版本前保留可恢复手段。

开源协议：[GPL-3.0](LICENSE)。

## English

**Language / 语言:** [中文](#edgegesture) | English

[Download APK](https://github.com/Fieldtrans/EdgeGesture/releases/latest) | [Docs](docs/README.md) | [Install Guide](docs/INSTALL.zh-CN.md) | [Troubleshooting](docs/TROUBLESHOOTING.zh-CN.md)

One-handed edge gestures for large phones. EdgeGesture helps you tap hard-to-reach areas of the screen and supports both a no-root Accessibility mode and an LSPosed/Xposed enhanced mode.

## Highlights

- Edge swipe up can trigger one-hand tap, Back, Home, or Recents.
- Edge double tap can trigger Recents.
- Supports no-root Accessibility mode and Root + LSPosed enhanced mode.
- Line arrow pointer mode: release to tap at the arrow tip.
- Tracker + Cursor mode inspired by joystick-style cursor control.
- Configurable double-tap wait time for balancing tap pass-through latency and double-tap recognition.
- Adjustable trigger area, control circle, pointer speed, smoothing, color, cancel timeout, and cancel distance.
- Top notification shade trigger with a lightweight pre-animation.
- Configuration import and export.
- No transparent full-screen touch layer in the enhanced path; the visual overlay does not consume touches.

## Requirements

- Standard mode: no root required, but the EdgeGesture Accessibility service must be enabled.
- Enhanced mode: rooted Android device with LSPosed installed and enabled.
- Android 16 / API 36 target build.
- For enhanced mode, the LSPosed scope should include Android system/framework.

## Installation

1. Download the latest APK from [Releases](https://github.com/Fieldtrans/EdgeGesture/releases/latest).
2. Install the APK.
3. For no-root use, open EdgeGesture, go to Android Accessibility settings, and enable EdgeGesture.
4. For LSPosed enhanced use, enable EdgeGesture in LSPosed and make sure the scope includes Android system/framework.
5. Reboot the phone when using LSPosed enhanced mode.
6. Open EdgeGesture, enable the gesture mode you want, and save the config.

If gestures do not respond, open LSPosed logs and search for `EdgeGesture`.

For detailed setup and troubleshooting, see [安装指南](docs/INSTALL.zh-CN.md) and [排错指南](docs/TROUBLESHOOTING.zh-CN.md).

### Line Pointer

Swipe up from the right edge to show a green arrow. Move your thumb inside a small control area; the arrow maps that movement to a larger screen range. Releasing taps at the arrow tip.

### Tracker + Cursor

Swipe up to show a tracker ball and cursor. Move the tracker in a joystick-like area to control the cursor. Releasing outside the cancel circle performs a tap.

## Build

```bash
./gradlew :app:assembleRelease
```

Release APK output:

```text
app/build/outputs/apk/release/app-release.apk
```

## Status

Current version: `1.3.0`.

This module hooks input handling inside `system_server`. Use it carefully and keep a working recovery path before testing custom builds.

License: [GPL-3.0](LICENSE).

## Contributors

See [CONTRIBUTORS.md](CONTRIBUTORS.md).
