package com.example.edgegesture.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val M3_SWITCH_MOTION_MS = 150
private val M3StandardEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

/**
 * 设置区块组件 - 简洁分组样式
 */
@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        SettingsGroupHeader(title = title)
        content()
    }
}

/**
 * 分组标题组件 - primary 颜色小字体
 */
@Composable
fun SettingsGroupHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

/**
 * 设置开关组件 - 支持点击整行切换
 */
@Composable
fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .toggleable(
                    value = checked,
                    role = Role.Switch,
                    onValueChange = onCheckedChange,
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        MotionSettingSwitch(
            checked = checked,
        )
    }
}

@Composable
private fun MotionSettingSwitch(checked: Boolean) {
    val switchColorSpec = tween<Color>(M3_SWITCH_MOTION_MS, easing = M3StandardEasing)
    val switchOffsetSpec = tween<Dp>(M3_SWITCH_MOTION_MS, easing = M3StandardEasing)
    val switchSizeSpec = tween<Dp>(M3_SWITCH_MOTION_MS, easing = M3StandardEasing)
    val trackColor by animateColorAsState(
        targetValue =
            if (checked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
        animationSpec = switchColorSpec,
        label = "settingSwitchTrackColor",
    )
    val borderColor by animateColorAsState(
        targetValue =
            if (checked) {
                Color.Transparent
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.72f)
            },
        animationSpec = switchColorSpec,
        label = "settingSwitchBorderColor",
    )
    val borderWidth by animateDpAsState(
        targetValue = if (checked) 0.dp else 2.dp,
        animationSpec = switchSizeSpec,
        label = "settingSwitchBorderWidth",
    )
    val thumbColor by animateColorAsState(
        targetValue =
            if (checked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        animationSpec = switchColorSpec,
        label = "settingSwitchThumbColor",
    )
    val thumbIconColor by animateColorAsState(
        targetValue =
            if (checked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
            },
        animationSpec = switchColorSpec,
        label = "settingSwitchThumbIconColor",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 28.dp else 4.dp,
        animationSpec = switchOffsetSpec,
        label = "settingSwitchThumbOffset",
    )
    val thumbSize by animateDpAsState(
        targetValue = if (checked) 30.dp else 28.dp,
        animationSpec = switchSizeSpec,
        label = "settingSwitchThumbSize",
    )

    Box(
        modifier =
            Modifier
                .width(64.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(19.dp))
                .background(trackColor)
                .border(borderWidth, borderColor, RoundedCornerShape(19.dp))
                .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier =
                Modifier
                    .offset(x = thumbOffset)
                    .size(thumbSize)
                    .clip(RoundedCornerShape(15.dp))
                    .background(thumbColor),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = checked,
                transitionSpec =
                    {
                        (
                            fadeIn(animationSpec = tween(120, easing = M3StandardEasing)) +
                                scaleIn(
                                    animationSpec = tween(120, easing = M3StandardEasing),
                                    initialScale = 0.68f,
                                )
                        ) togetherWith
                            (
                                fadeOut(animationSpec = tween(90, easing = M3StandardEasing)) +
                                    scaleOut(
                                        animationSpec = tween(90, easing = M3StandardEasing),
                                        targetScale = 0.68f,
                                    )
                            )
                    },
                label = "settingSwitchThumbIcon",
            ) { enabled ->
                if (enabled) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = thumbIconColor,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Box(
                        modifier =
                            Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(thumbIconColor),
                    )
                }
            }
        }
    }
}

/**
 * 分隔线 - 用于设置项之间
 */
@Composable
fun SettingsDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    )
}
