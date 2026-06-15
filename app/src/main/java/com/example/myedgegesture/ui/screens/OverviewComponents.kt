package com.example.myedgegesture.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myedgegesture.GestureConfig
import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.ui.components.SettingSwitch
import com.example.myedgegesture.ui.utils.t
import com.example.myedgegesture.ui.viewmodel.HookStatus

/**
 * Status card showing module load state and enable switch.
 */
@Composable
fun StatusCard(
    settings: SettingsState,
    hookStatus: HookStatus,
    onSettingsChange: (SettingsState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor by animateColorAsState(
        targetValue =
            if (hookStatus.active) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.72f)
            },
        animationSpec = tween(300),
        label = "statusCardColor",
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier =
            modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = spring(dampingRatio = 0.8f)),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color =
                            if (hookStatus.active) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            },
                        contentColor =
                            if (hookStatus.active) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            },
                    ) {
                        Icon(
                            imageVector =
                                if (hookStatus.active) {
                                    Icons.Rounded.CheckCircle
                                } else {
                                    Icons.Rounded.Error
                                },
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .padding(8.dp)
                                    .size(22.dp),
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            text =
                                if (hookStatus.active) {
                                    t("模块已激活", "Module Active")
                                } else {
                                    t("模块未激活", "Module Inactive")
                                },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = hookStatus.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                SettingSwitch(
                    title = t("启用模块手势", "Enable Module Gestures"),
                    description = t("关闭后不处理边缘上划。", "When disabled, edge swipe-up will not be handled."),
                    checked = settings.enabled,
                    onCheckedChange = { onSettingsChange(settings.copy(enabled = it)) },
                )
                SettingSwitch(
                    title = t("触觉反馈", "Haptic Feedback"),
                    description = t("执行动作时触发短震动。", "Short vibration when an action is triggered."),
                    checked = settings.hapticFeedbackEnabled,
                    onCheckedChange = { onSettingsChange(settings.copy(hapticFeedbackEnabled = it)) },
                )
            }
        }
    }
}

/**
 * Mode selector: Pill-shaped segmented button style
 */
@Composable
fun ModeSelector(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLineArrow = settings.pointerControlStyle == GestureConfig.POINTER_STYLE_LINE_ARROW
    val selectedColor = MaterialTheme.colorScheme.primaryContainer
    val unselectedColor = MaterialTheme.colorScheme.surfaceContainerHigh

    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(unselectedColor),
    ) {
        // Line Arrow option
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isLineArrow) selectedColor else Color.Transparent)
                    .clickable {
                        onSettingsChange(
                            settings.copy(pointerControlStyle = GestureConfig.POINTER_STYLE_LINE_ARROW),
                        )
                    }
                    .padding(vertical = 12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint =
                        if (isLineArrow) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
                Text(
                    text = t("直线箭头", "Line Arrow"),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isLineArrow) FontWeight.SemiBold else FontWeight.Normal,
                    color =
                        if (isLineArrow) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }
        // Tracker Cursor option
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (!isLineArrow) selectedColor else Color.Transparent)
                    .clickable {
                        onSettingsChange(
                            settings.copy(pointerControlStyle = GestureConfig.POINTER_STYLE_TRACKER_CURSOR),
                        )
                    }
                    .padding(vertical = 12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.RadioButtonChecked,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint =
                        if (!isLineArrow) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
                Text(
                    text = t("摇杆光标", "Tracker Cursor"),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (!isLineArrow) FontWeight.SemiBold else FontWeight.Normal,
                    color =
                        if (!isLineArrow) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }
    }
}
