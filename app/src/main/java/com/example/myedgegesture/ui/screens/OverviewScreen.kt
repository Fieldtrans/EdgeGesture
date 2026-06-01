package com.example.myedgegesture.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myedgegesture.GestureConfig
import com.example.myedgegesture.R
import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.ui.components.SettingSlider
import com.example.myedgegesture.ui.components.SettingsSection
import com.example.myedgegesture.ui.utils.t
import com.example.myedgegesture.ui.viewmodel.HookStatus

/**
 * Overview page showing module status, guide, mode selection and about info
 */
@Composable
fun OverviewPage(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
    hookStatus: HookStatus,
    onShowGuide: () -> Unit
) {
    val context = LocalContext.current
    var showSupportDialog by remember { mutableStateOf(false) }

    StatusCard(settings, hookStatus, onSettingsChange)

    SettingsSection(t("新手指南", "Quick Start"), Icons.Rounded.TouchApp) {
        Text(
            text = t(
                "第一次使用建议先看 30 秒指南：选择普通模式或增强模式，理解上划指针、摇杆光标、取消圆和保存配置。",
                "New users should start with a short guide covering Standard/Enhanced mode, swipe-up pointer, Tracker + Cursor, cancel circles, and saving settings."
            ),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(
            onClick = onShowGuide,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(t("打开新手指南", "Open Guide"))
        }
    }

    SettingsSection(
        title = t("普通模式（无障碍）", "Standard Mode (Accessibility)"),
        icon = Icons.Rounded.TouchApp
    ) {
        Text(
            text = t(
                "免 Root 的基础模式，依赖系统无障碍服务。适合普通用户使用；如果 LSPosed 增强模式已启动，无障碍触摸层会自动暂停，避免冲突。",
                "A no-root basic mode powered by Android Accessibility. It is suitable for normal users; if the LSPosed enhanced engine is active, the accessibility touch layer pauses automatically to avoid conflicts."
            ),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(
            onClick = {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(t("打开无障碍设置", "Open Accessibility Settings"))
        }
    }

    SettingsSection(
        title = t("当前模式", "Current Mode"),
        icon = Icons.Rounded.PowerSettingsNew
    ) {
        ModeSelector(settings, onSettingsChange)
        SettingSlider(
            title = t("触发边缘宽度", "Trigger Edge Width"),
            valueText = "${settings.edgeWidthDp}dp",
            description = t("越宽越容易触发，也越容易靠近系统侧滑区域。", "Wider is easier to trigger, but closer to the system back gesture area."),
            value = settings.edgeWidthDp,
            range = GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP..GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_DP,
            onValueChange = { onSettingsChange(settings.copy(edgeWidthDp = it)) }
        )
        SettingSlider(
            title = t("直线箭头灵敏度", "Line Arrow Sensitivity"),
            valueText = "${settings.pointerSensitivity}%",
            description = t("影响直线箭头模式下手指移动到指针移动的比例。", "Controls the finger-to-pointer movement ratio in line arrow mode."),
            value = settings.pointerSensitivity,
            range = 40..180,
            onValueChange = { onSettingsChange(settings.copy(pointerSensitivity = it)) }
        )
    }

    SettingsSection(t("说明", "Notes"), Icons.Rounded.Palette) {
        Text(
            text = t(
                "启用 LSPosed 模块后建议重启。杀掉 App 不影响手势；App 只负责保存参数。需要排查时，在 LSPosed 日志里搜索 EdgeGesture。若调出时卡顿，优先降低控制圆透明度。",
                "Reboot after enabling the LSPosed module. Killing the app does not stop gestures; the app only saves settings. Search EdgeGesture in LSPosed logs for troubleshooting. If the overlay stutters, lower the control circle opacity first."
            ),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    SettingsSection(t("关于", "About"), Icons.Rounded.Settings) {
        Text(
            text = t(
                "EdgeGesture 是一个单手边缘手势工具，支持普通无障碍模式和 LSPosed 增强模式。",
                "EdgeGesture is a one-handed edge gesture tool with Accessibility mode and LSPosed enhanced mode."
            ),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Fieldtrans/EdgeGesture"))
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(t("打开 GitHub 项目主页", "Open GitHub"))
        }
    }

    SettingsSection(t("支持开发", "Support"), Icons.Rounded.Palette) {
        Text(
            text = t(
                "如果 EdgeGesture 对你有帮助，可以自愿支持后续维护。打赏不会解锁额外功能。",
                "If EdgeGesture helps you, you can voluntarily support ongoing development. Donations do not unlock extra features."
            ),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(
            onClick = { showSupportDialog = true },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(t("打开打赏码", "Show QR Code"))
        }
    }

    if (showSupportDialog) {
        SupportDevelopmentDialog(
            onDismiss = { showSupportDialog = false }
        )
    }
}

@Composable
private fun SupportDevelopmentDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t("支持开发", "Support Development")) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = t(
                        "感谢支持。这个入口完全自愿，不影响任何功能使用。",
                        "Thank you for the support. This is completely voluntary and does not affect any feature."
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.donate_qr),
                        contentDescription = t("打赏二维码", "Donation QR code"),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(t("关闭", "Close"))
            }
        }
    )
}
