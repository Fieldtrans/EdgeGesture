# Changelog

## 1.3.2 - 2026-06-24

- Refined the Material 3 settings layout, especially the overview and trigger pages.
- Added the new image-only homepage demo GIF and updated the README preview.
- Improved Tracker + Cursor startup positioning so the cursor starts near the control source instead of jumping to a screen edge.
- Added finer 1dp control for the top notification hotspot height and pointer tap contact area.
- Added configurable top notification hotspot start/end percentages and aligned the runtime preview with the actual trigger area.
- Improved reboot recovery by syncing saved configuration into device-protected storage.

## 1.3.1 - 2026-06-15

- Removed the support/donation entry and QR code from the app.
- Enabled Material You dynamic color on Android 12 and newer.
- Reduced the adaptive app icon foreground size.
- Refined the overview status card to use Material 3 color roles.
- Formatted the Kotlin project with ktlint and added Compose-aware ktlint naming configuration.

## 1.1.1 - 2026-05-31

- Added boot/unlock/user-present config rebroadcasting so the LSPosed enhanced engine can recover its saved configuration after reboot.
- Added an explicit config sync when opening the app, making the LSPosed mode recover reliably without toggling the master switch.
- Refreshed the LSPosed runtime when the screen turns on or the user unlocks the device.
- Added GitHub topics for Accessibility/no-root discoverability.

## 1.1.0 - 2026-05-31

- Added Accessibility mode for no-root users, including one-hand line pointer and Tracker + Cursor.
- Added coexistence handling between Accessibility mode and LSPosed enhanced mode to avoid duplicate gesture processing.
- Added edge double-tap Recents support with a configurable double-tap wait time.
- Improved edge tap pass-through behavior to reduce conflicts with app edge buttons and system back gestures.
- Improved normal edge tap pass-through in both LSPosed and Accessibility modes so single taps are not swallowed by double-tap detection.
- Added notification shade trigger mode switching: pull down when the pointer touches the status bar, or pull down only after releasing on the status bar.
- Added clearer Standard vs LSPosed trigger previews and LSPosed-style edge preview when the enhanced module is detected.
- Added automatic setting persistence; changes are saved and broadcast immediately without a separate save button.
- Added a first-run quick-start guide and a reusable guide entry on the overview page.
- Refined the settings UI with bottom navigation, bilingual text, GitHub link, and clearer trigger/action pages.
- Kept the LSPosed enhanced path on InputFilter/system_server monitoring for lower conflict with normal touches.

## 1.0.4 - 2026-05-29

- Removed the bottom-edge action option to avoid conflict with Android system Home/Recents gestures.
- Action settings now focus on left and right edges only.

## 1.0.3 - 2026-05-29

- Implemented actions currently available in this release:
  - Edge swipe up: One-hand tap, Back, Home, or Recents.
  - Edge double tap: Recents.
- Removed unimplemented action rows from the action settings page.
- Removed the top-edge action option; action settings now focus on left, right, and bottom edges.
- Kept the gesture detector on the stable InputFilter-based path.

## 1.0.2 - 2026-05-29

- Emergency hotfix release.
- Restored the stable v1.0.1 input handling path.
- Removed the experimental all-action gesture detector build from the release path because it could cause high `system_server` CPU usage on real devices.

## 1.0.1 - 2026-05-28

- Added GPL-3.0 license.
- Added GitHub Actions release APK build.
- Added install and troubleshooting documentation.
- Added real-device screenshots to the project page.
- Removed non-download demo GIF from GitHub Release assets.
- Added local release signing support through ignored `signing.properties`.

## 1.0 - 2026-05-28

- Renamed the project to EdgeGesture.
- Added line arrow pointer mode.
- Added Tracker + Cursor mode.
- Added adjustable trigger area, control circle, pointer speed, smoothing, color, cancel timeout, and cancel distance.
- Added top-edge notification shade trigger and lightweight pre-animation.
- Added configuration import and export.
- Optimized overlay drawing to reduce unnecessary refreshes.
- Added a simple app icon and bilingual UI/README.
