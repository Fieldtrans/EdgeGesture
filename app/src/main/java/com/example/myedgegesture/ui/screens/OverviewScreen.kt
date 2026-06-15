package com.example.myedgegesture.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.myedgegesture.CrashReporter
import com.example.myedgegesture.GestureConfig
import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.ui.components.SettingSlider
import com.example.myedgegesture.ui.components.SettingsSection
import com.example.myedgegesture.ui.utils.t
import com.example.myedgegesture.ui.viewmodel.HookStatus

@Composable
fun OverviewPage(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
    hookStatus: HookStatus,
) {
    val context = LocalContext.current
    var showCrashLogDialog by remember { mutableStateOf(false) }
    var crashLog by remember { mutableStateOf<String?>(null) }

    StatusCard(settings, hookStatus, onSettingsChange)

    Spacer(Modifier.height(8.dp))

    SettingsSection(
        title = t("标准模式（无障碍）", "Standard Mode (Accessibility)"),
        icon = Icons.Rounded.TouchApp,
    ) {
        Text(
            text =
                t(
                    "无需 Root 的基础模式，依赖系统无障碍服务。适合普通用户使用；如果 LSPosed 增强模式已启动，无障碍触摸层会自动暂停以避免冲突。",
                    "A no-root basic mode powered by Android Accessibility. It is suitable for normal users; " +
                        "if the LSPosed enhanced engine is active, the accessibility touch layer pauses " +
                        "automatically to avoid conflicts.",
                ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(
            onClick = {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(t("打开无障碍设置", "Open Accessibility Settings"))
        }
    }

    Spacer(Modifier.height(8.dp))

    SettingsSection(
        title = t("当前模式", "Current Mode"),
        icon = Icons.Rounded.PowerSettingsNew,
    ) {
        ModeSelector(settings, onSettingsChange)
        SettingSlider(
            title = t("触发边缘宽度", "Trigger Edge Width"),
            valueText = "${settings.edgeWidthDp}dp",
            description =
                t(
                    "越宽越容易触发，也越容易靠近系统侧滑区域。",
                    "Wider is easier to trigger, but closer to the system back gesture area.",
                ),
            value = settings.edgeWidthDp,
            range = GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP..GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_DP,
            onValueChange = { onSettingsChange(settings.copy(edgeWidthDp = it)) },
        )
        SettingSlider(
            title = t("直线箭头灵敏度", "Line Arrow Sensitivity"),
            valueText = "${settings.pointerSensitivity}%",
            description =
                t(
                    "线性移动倍率，不随快速滑动额外放大。",
                    "Linear movement multiplier without fast-swipe acceleration.",
                ),
            value = settings.pointerSensitivity,
            range = 40..180,
            onValueChange = { onSettingsChange(settings.copy(pointerSensitivity = it)) },
        )
    }

    Spacer(Modifier.height(8.dp))

    SettingsSection(t("说明", "Notes"), Icons.Rounded.Palette) {
        Text(
            text =
                t(
                    "启用 LSPosed 模块后建议重启。杀掉 App 不影响手势；App 只负责保存参数。需要排查时，在 LSPosed 日志里搜索 EdgeGesture。若调出时卡顿，优先降低控制圆透明度。",
                    "Reboot after enabling the LSPosed module. Killing the app does not stop gestures; " +
                        "the app only saves settings. Search EdgeGesture in LSPosed logs for troubleshooting. " +
                        "If the overlay stutters, lower the control circle opacity first.",
                ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    Spacer(Modifier.height(8.dp))

    Column(modifier = Modifier.fillMaxWidth()) {
        OverviewListItem(
            icon = Icons.Rounded.Info,
            title = t("关于", "About"),
            subtitle =
                t(
                    "EdgeGesture - 单手边缘手势工具",
                    "EdgeGesture - One-handed edge gesture tool",
                ),
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Fieldtrans/EdgeGesture")),
                )
            },
            trailingIcon = Icons.Rounded.ArrowOutward,
        )
        OverviewListItem(
            icon = Icons.Rounded.Info,
            title = t("查看崩溃日志", "View Crash Log"),
            subtitle =
                t(
                    "查看上一次未捕获异常记录",
                    "View the latest saved crash report",
                ),
            onClick = {
                crashLog = CrashReporter.getLastCrashLog(context)
                showCrashLogDialog = true
            },
        )
    }

    if (showCrashLogDialog) {
        CrashLogDialog(
            crashLog = crashLog,
            onClear = {
                CrashReporter.clearCrashLog(context)
                crashLog = null
            },
            onDismiss = { showCrashLogDialog = false },
        )
    }
}

@Composable
private fun OverviewListItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailingIcon: ImageVector? = null,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CrashLogDialog(
    crashLog: String?,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    val content = crashLog?.takeIf { it.isNotBlank() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t("崩溃日志", "Crash Log")) },
        text = {
            if (content == null) {
                Text(
                    text = t("暂无崩溃日志。", "No crash log saved yet."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = content,
                        modifier =
                            Modifier
                                .heightIn(max = 420.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(t("关闭", "Close"))
            }
        },
        dismissButton = {
            if (content != null) {
                TextButton(onClick = onClear) {
                    Text(t("清除", "Clear"))
                }
            }
        },
    )
}
