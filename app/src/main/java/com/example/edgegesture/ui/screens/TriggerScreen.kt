package com.example.edgegesture.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.edgegesture.GestureConfig
import com.example.edgegesture.data.model.SettingsState
import com.example.edgegesture.ui.components.SettingSlider
import com.example.edgegesture.ui.components.SettingsSection
import com.example.edgegesture.ui.utils.drawArrow
import com.example.edgegesture.ui.utils.notificationShadeModeLabel
import com.example.edgegesture.ui.utils.t
import com.example.edgegesture.ui.viewmodel.HookStatus

@Composable
fun TriggerPage(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
    hookStatus: HookStatus,
    bottomSpacing: Dp = 24.dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EdgeRangePreview(settings, hookStatus.active)

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentPadding = PaddingValues(bottom = bottomSpacing),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                SettingsSection(t("侧边触发", "Side Trigger"), Icons.Rounded.Tune) {
                    SettingSlider(
                        title = t("上划触发距离", "Swipe-Up Distance"),
                        valueText = "${settings.swipeDistanceDp}dp",
                        description = t("距离越大，越不容易误触。", "A longer distance reduces accidental triggers."),
                        value = settings.swipeDistanceDp,
                        range = 40..180,
                        onValueChange = { onSettingsChange(settings.copy(swipeDistanceDp = it)) },
                    )
                    SettingSlider(
                        title = t("方向允许偏角", "Allowed Angle"),
                        valueText = "±${settings.swipeAngleDegrees}°",
                        description = t("越小越严格，越不容易和横向侧滑混淆。", "Smaller values are stricter and reduce horizontal gesture conflicts."),
                        value = settings.swipeAngleDegrees,
                        range = 5..85,
                        onValueChange = { onSettingsChange(settings.copy(swipeAngleDegrees = it)) },
                    )
                }
            }

            item {
                SettingsSection(t("顶部热区", "Top Hotspot"), Icons.Rounded.TouchApp) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = t("通知栏触发方式", "Notification Trigger"),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text =
                                t(
                                    "热区贴着屏幕最上边，横向起点和终点可自定义，用来避开左上角、右上角或挖孔区域。",
                                    "The hotspot stays on the top edge; adjust start and end to avoid corners or cutouts.",
                                ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                        ) {
                            GestureConfig.notificationShadeModes.forEach { mode ->
                                FilterChip(
                                    selected = settings.notificationShadeMode == mode,
                                    onClick = { onSettingsChange(settings.copy(notificationShadeMode = mode)) },
                                    label = { Text(notificationShadeModeLabel(mode)) },
                                )
                            }
                        }
                    }
                    SettingSlider(
                        title = t("顶部热区起点", "Top Hotspot Start"),
                        valueText = "${settings.notificationHotspotStartPercent}%",
                        description =
                            t(
                                "从屏幕左侧开始计算，热区仍然贴着最上边。",
                                "Measured from the left edge; the hotspot remains attached to the top edge.",
                            ),
                        value = settings.notificationHotspotStartPercent,
                        range = GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT..GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT,
                        onValueChange = { onSettingsChange(settings.copy(notificationHotspotStartPercent = it)) },
                    )
                    SettingSlider(
                        title = t("顶部热区终点", "Top Hotspot End"),
                        valueText = "${settings.notificationHotspotEndPercent}%",
                        description = t("终点减去起点就是热区横向长度。", "The end minus the start is the hotspot width."),
                        value = settings.notificationHotspotEndPercent,
                        range = GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT..GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT,
                        onValueChange = { onSettingsChange(settings.copy(notificationHotspotEndPercent = it)) },
                    )
                    SettingSlider(
                        title = t("顶部热区高度", "Top Hotspot Height"),
                        valueText = "${settings.notificationTopEdgeDp}dp",
                        description =
                            t(
                                "从屏幕最上边向下计算，最低可调到 1dp，不再自动移动到摄像头下方。",
                                "Measured downward from the very top edge; it can be reduced to 1dp " +
                                    "and is no longer shifted below the camera area.",
                            ),
                        value = settings.notificationTopEdgeDp,
                        range = GestureConfig.NOTIFICATION_TOP_EDGE_MIN_DP..GestureConfig.NOTIFICATION_TOP_EDGE_MAX_DP,
                        onValueChange = { onSettingsChange(settings.copy(notificationTopEdgeDp = it)) },
                    )
                }
            }
        }
    }
}

@Composable
fun LiveTriggerPreview(
    settings: SettingsState,
    enhancedPreview: Boolean,
) {
    val color = settings.pointerColor
    Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val startPercent =
            minOf(settings.triggerRegionStartPercent, settings.triggerRegionEndPercent)
                .coerceIn(0, 100) / 100f
        val endPercent =
            maxOf(settings.triggerRegionStartPercent, settings.triggerRegionEndPercent)
                .coerceIn(0, 100) / 100f
        val top = size.height * startPercent
        val bottom = size.height * endPercent
        val activeHeight = (bottom - top).coerceAtLeast(1f)
        val edgeWidth =
            settings.edgeWidthDp.dp.toPx()
                .coerceAtLeast(GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP.dp.toPx())
                .coerceAtMost(GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_DP.dp.toPx())
                .coerceAtMost(size.width * GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_SCREEN_PERCENT)
        val systemGap =
            if (enhancedPreview) {
                0f
            } else {
                GestureConfig.ACCESSIBILITY_SYSTEM_GESTURE_GAP_DP.dp.toPx()
                    .coerceAtMost(size.width * GestureConfig.ACCESSIBILITY_MAX_SYSTEM_GESTURE_GAP_SCREEN_PERCENT)
            }
        val topEdgeHeight =
            settings.notificationTopEdgeDp.dp.toPx()
                .coerceIn(
                    GestureConfig.NOTIFICATION_TOP_EDGE_MIN_DP.dp.toPx(),
                    GestureConfig.NOTIFICATION_TOP_EDGE_MAX_DP.dp.toPx(),
                )
        val hotspotStartPercent =
            minOf(settings.notificationHotspotStartPercent, settings.notificationHotspotEndPercent)
                .coerceIn(GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT, GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT) / 100f
        val hotspotEndPercent =
            maxOf(settings.notificationHotspotStartPercent, settings.notificationHotspotEndPercent)
                .coerceIn(GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT, GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT) / 100f
        val topHotspotTop = 0f
        val topHotspotLeft = size.width * hotspotStartPercent
        val topHotspotWidth = (size.width * (hotspotEndPercent - hotspotStartPercent)).coerceAtLeast(1f)
        val previewColor = color.copy(alpha = 0.18f)
        val edgeColor = color.copy(alpha = 0.36f)

        drawRect(
            color = previewColor,
            topLeft = Offset(systemGap, top),
            size = Size(edgeWidth, activeHeight),
        )
        drawRect(
            color = previewColor,
            topLeft = Offset(size.width - systemGap - edgeWidth, top),
            size = Size(edgeWidth, activeHeight),
        )
        drawRect(
            color = previewColor.copy(alpha = 0.14f),
            topLeft = Offset(topHotspotLeft, topHotspotTop),
            size = Size(topHotspotWidth, topEdgeHeight),
        )
        drawLine(
            color = edgeColor,
            start = Offset(systemGap + edgeWidth, top),
            end = Offset(systemGap + edgeWidth, bottom),
            strokeWidth = 1.dp.toPx(),
        )
        drawLine(
            color = edgeColor,
            start = Offset(size.width - systemGap - edgeWidth, top),
            end = Offset(size.width - systemGap - edgeWidth, bottom),
            strokeWidth = 1.dp.toPx(),
        )
    }
}

@Composable
fun EdgeRangePreview(
    settings: SettingsState,
    enhancedMode: Boolean,
) {
    SettingsSection(t("手势预览", "Gesture Preview"), Icons.Rounded.TouchApp) {
        val systemColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.34f)
        val edgeColor = settings.pointerColor
        val phoneColor = MaterialTheme.colorScheme.surface
        val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)
        val previewProgress by rememberInfiniteTransition(label = "triggerPreviewLoop").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1800, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "triggerPreviewProgress",
        )

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
            shape = RoundedCornerShape(8.dp),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Canvas(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(8.dp),
                ) {
                    val panelGap = size.width * 0.08f
                    val phoneW = (size.width - panelGap) / 2f * 0.62f
                    val phoneH = size.height * 0.7f
                    val firstCenterX = size.width * 0.27f
                    val secondCenterX = size.width * 0.73f
                    val top = (size.height - phoneH) * 0.38f
                    val sideLeft = firstCenterX - phoneW / 2f
                    val topLeft = secondCenterX - phoneW / 2f
                    val sideProgress = ((previewProgress * 1.35f).coerceIn(0f, 1f))
                    val topProgress = (((previewProgress - 0.18f) * 1.35f).coerceIn(0f, 1f))

                    fun drawSidePreview(left: Float) {
                        val right = left + phoneW
                        val edgePx =
                            settings.edgeWidthDp.dp.toPx()
                                .coerceAtLeast(GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP.dp.toPx())
                                .coerceAtMost(GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_DP.dp.toPx())
                                .coerceAtMost(phoneW * GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_SCREEN_PERCENT)
                        val systemGapPx =
                            if (enhancedMode) {
                                0f
                            } else {
                                GestureConfig.ACCESSIBILITY_SYSTEM_GESTURE_GAP_DP.dp.toPx()
                                    .coerceAtMost(phoneW * GestureConfig.ACCESSIBILITY_MAX_SYSTEM_GESTURE_GAP_SCREEN_PERCENT)
                            }
                        val start =
                            minOf(settings.triggerRegionStartPercent, settings.triggerRegionEndPercent)
                                .coerceIn(0, 100) / 100f
                        val end =
                            maxOf(settings.triggerRegionStartPercent, settings.triggerRegionEndPercent)
                                .coerceIn(0, 100) / 100f
                        val activeTop = top + phoneH * start
                        val activeBottom = top + phoneH * end
                        val triggerY = activeTop + (activeBottom - activeTop).coerceAtLeast(1f) * 0.72f
                        val triggerX = right - systemGapPx - edgePx / 2f
                        val distancePx = settings.swipeDistanceDp.dp.toPx().coerceAtMost(phoneW * 0.6f)
                        val endPoint =
                            Offset(
                                (triggerX - distancePx * 0.36f).coerceAtLeast(left + systemGapPx + edgePx),
                                (triggerY - distancePx).coerceAtLeast(top + 10.dp.toPx()),
                            )
                        val animatedEnd =
                            Offset(
                                triggerX + (endPoint.x - triggerX) * sideProgress,
                                triggerY + (endPoint.y - triggerY) * sideProgress,
                            )

                        drawRoundRect(
                            color = phoneColor,
                            topLeft = Offset(left, top),
                            size = Size(phoneW, phoneH),
                            cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                        )
                        if (!enhancedMode) {
                            drawRect(systemColor, Offset(left, top), Size(systemGapPx, phoneH))
                            drawRect(systemColor, Offset(right - systemGapPx, top), Size(systemGapPx, phoneH))
                        }
                        drawRect(edgeColor.copy(alpha = 0.14f), Offset(left + systemGapPx, top), Size(edgePx, phoneH))
                        drawRect(edgeColor.copy(alpha = 0.14f), Offset(right - systemGapPx - edgePx, top), Size(edgePx, phoneH))
                        drawRect(
                            color = edgeColor.copy(alpha = 0.44f),
                            topLeft = Offset(right - systemGapPx - edgePx, activeTop),
                            size = Size(edgePx, activeBottom - activeTop),
                        )
                        drawRoundRect(
                            color = borderColor,
                            topLeft = Offset(left, top),
                            size = Size(phoneW, phoneH),
                            cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                            style = Stroke(width = 1.4.dp.toPx()),
                        )
                        drawArrow(
                            start = Offset(triggerX, triggerY),
                            end = animatedEnd,
                            arrowSize = 7.dp.toPx(),
                            color = edgeColor,
                            stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }

                    fun drawTopPreview(left: Float) {
                        val hotspotHeight =
                            settings.notificationTopEdgeDp.dp.toPx()
                                .coerceIn(
                                    GestureConfig.NOTIFICATION_TOP_EDGE_MIN_DP.dp.toPx(),
                                    GestureConfig.NOTIFICATION_TOP_EDGE_MAX_DP.dp.toPx(),
                                )
                                .coerceAtMost(phoneH * 0.38f)
                        val hotspotStartPercent =
                            minOf(settings.notificationHotspotStartPercent, settings.notificationHotspotEndPercent)
                                .coerceIn(
                                    GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT,
                                    GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT,
                                ) / 100f
                        val hotspotEndPercent =
                            maxOf(settings.notificationHotspotStartPercent, settings.notificationHotspotEndPercent)
                                .coerceIn(
                                    GestureConfig.NOTIFICATION_HOTSPOT_MIN_PERCENT,
                                    GestureConfig.NOTIFICATION_HOTSPOT_MAX_PERCENT,
                                ) / 100f
                        val hotspotLeft = left + phoneW * hotspotStartPercent
                        val hotspotWidth = (phoneW * (hotspotEndPercent - hotspotStartPercent)).coerceAtLeast(1f)
                        val pulseAlpha = 0.16f + 0.22f * topProgress

                        drawRoundRect(
                            color = phoneColor,
                            topLeft = Offset(left, top),
                            size = Size(phoneW, phoneH),
                            cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                        )
                        drawRect(
                            color = edgeColor.copy(alpha = pulseAlpha),
                            topLeft = Offset(hotspotLeft, top),
                            size = Size(hotspotWidth, hotspotHeight),
                        )
                        drawRoundRect(
                            color = borderColor,
                            topLeft = Offset(left, top),
                            size = Size(phoneW, phoneH),
                            cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                            style = Stroke(width = 1.4.dp.toPx()),
                        )
                    }

                    drawSidePreview(sideLeft)
                    drawTopPreview(topLeft)
                }

                Column(
                    modifier = Modifier.padding(start = 14.dp, top = 0.dp, end = 14.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Spacer(Modifier.weight(1f))
                        PreviewModeLabel(
                            color = edgeColor.copy(alpha = 0.44f),
                            title = t("侧边", "Side"),
                            subtitle = "±${settings.swipeAngleDegrees}° / ${settings.swipeDistanceDp}dp",
                            modifier = Modifier.weight(5f),
                        )
                        Spacer(Modifier.weight(1f))
                        PreviewModeLabel(
                            color = edgeColor.copy(alpha = 0.52f),
                            title = t("顶部", "Top"),
                            subtitle =
                                "${settings.notificationHotspotStartPercent}-${settings.notificationHotspotEndPercent}% / " +
                                    "${settings.notificationTopEdgeDp}dp",
                            modifier = Modifier.weight(5f),
                        )
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewModeLabel(
    color: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(10.dp)
                        .background(color, RoundedCornerShape(3.dp)),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
        )
    }
}
