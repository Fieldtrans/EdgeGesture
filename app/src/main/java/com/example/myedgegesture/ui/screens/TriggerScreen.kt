package com.example.myedgegesture.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.myedgegesture.GestureConfig
import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.ui.components.SettingSlider
import com.example.myedgegesture.ui.components.SettingsSection
import com.example.myedgegesture.ui.utils.drawArrow
import com.example.myedgegesture.ui.utils.t
import com.example.myedgegesture.ui.viewmodel.HookStatus
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TriggerPage(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
    hookStatus: HookStatus,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EdgeRangePreview(settings, hookStatus.active)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SettingsSection(t("触发区域", "Trigger Area"), Icons.Rounded.TouchApp) {
                    SettingSlider(
                        title = t("区域起点", "Area Start"),
                        valueText = "${settings.triggerRegionStartPercent}%",
                        description = t("从屏幕上方开始计算。", "Measured from the top of the screen."),
                        value = settings.triggerRegionStartPercent,
                        range = 0..100,
                        onValueChange = { onSettingsChange(settings.copy(triggerRegionStartPercent = it)) }
                    )
                    SettingSlider(
                        title = t("区域终点", "Area End"),
                        valueText = "${settings.triggerRegionEndPercent}%",
                        description = t("区域终点可以低于起点，内部会自动取有效范围。", "The end can be lower than the start; the valid range is normalized automatically."),
                        value = settings.triggerRegionEndPercent,
                        range = 0..100,
                        onValueChange = { onSettingsChange(settings.copy(triggerRegionEndPercent = it)) }
                    )
                    SettingSlider(
                        title = t("边缘宽度", "Edge Width"),
                        valueText = "${settings.edgeWidthDp}dp",
                        description = t("推荐先保持 18dp 左右，避免系统返回冲突。", "Start around 18dp to avoid conflicts with the system back gesture."),
                        value = settings.edgeWidthDp,
                        range = GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP..GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_DP,
                        onValueChange = { onSettingsChange(settings.copy(edgeWidthDp = it)) }
                    )
                }
            }

            item {
                SettingsSection(t("高级触发", "Advanced Trigger"), Icons.Rounded.Tune) {
                    SettingSlider(
                        title = t("上划触发距离", "Swipe-Up Distance"),
                        valueText = "${settings.swipeDistanceDp}dp",
                        description = t("距离越大，越不容易误触。", "A longer distance reduces accidental triggers."),
                        value = settings.swipeDistanceDp,
                        range = 40..180,
                        onValueChange = { onSettingsChange(settings.copy(swipeDistanceDp = it)) }
                    )
                    SettingSlider(
                        title = t("方向允许偏角", "Allowed Angle"),
                        valueText = "±${settings.swipeAngleDegrees}°",
                        description = t("越小越严格，越不容易和横向侧滑混淆。", "Smaller values are stricter and reduce horizontal gesture conflicts."),
                        value = settings.swipeAngleDegrees,
                        range = 5..85,
                        onValueChange = { onSettingsChange(settings.copy(swipeAngleDegrees = it)) }
                    )
                }
            }
        }
    }
}

@Composable
fun LiveTriggerPreview(settings: SettingsState, enhancedPreview: Boolean) {
    val color = settings.pointerColor
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val startPercent = minOf(settings.triggerRegionStartPercent, settings.triggerRegionEndPercent)
            .coerceIn(0, 100) / 100f
        val endPercent = maxOf(settings.triggerRegionStartPercent, settings.triggerRegionEndPercent)
            .coerceIn(0, 100) / 100f
        val top = size.height * startPercent
        val bottom = size.height * endPercent
        val activeHeight = (bottom - top).coerceAtLeast(1f)
        val edgeWidth = settings.edgeWidthDp.dp.toPx()
            .coerceAtLeast(GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP.dp.toPx())
            .coerceAtMost(GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_DP.dp.toPx())
            .coerceAtMost(size.width * GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_SCREEN_PERCENT)
        val systemGap = if (enhancedPreview) {
            0f
        } else {
            GestureConfig.ACCESSIBILITY_SYSTEM_GESTURE_GAP_DP.dp.toPx()
                .coerceAtMost(size.width * GestureConfig.ACCESSIBILITY_MAX_SYSTEM_GESTURE_GAP_SCREEN_PERCENT)
        }
        val previewColor = color.copy(alpha = 0.18f)
        val edgeColor = color.copy(alpha = 0.36f)

        drawRect(
            color = previewColor,
            topLeft = Offset(systemGap, top),
            size = Size(edgeWidth, activeHeight)
        )
        drawRect(
            color = previewColor,
            topLeft = Offset(size.width - systemGap - edgeWidth, top),
            size = Size(edgeWidth, activeHeight)
        )
        drawLine(
            color = edgeColor,
            start = Offset(systemGap + edgeWidth, top),
            end = Offset(systemGap + edgeWidth, bottom),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = edgeColor,
            start = Offset(size.width - systemGap - edgeWidth, top),
            end = Offset(size.width - systemGap - edgeWidth, bottom),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
fun EdgeRangePreview(settings: SettingsState, enhancedMode: Boolean) {
    SettingsSection(t("手势预览", "Gesture Preview"), Icons.Rounded.TouchApp) {
        val systemColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.34f)
        val edgeColor = settings.pointerColor
        val phoneColor = MaterialTheme.colorScheme.surface
        val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .padding(10.dp)
                ) {
                    val phoneW = size.width * 0.34f
                    val phoneH = size.height * 0.82f
                    val gap = size.width * 0.08f
                    val firstLeft = (size.width - phoneW * 2f - gap) / 2f
                    val top = (size.height - phoneH) / 2f

                    fun drawEnginePreview(left: Float, lsposed: Boolean) {
                        val right = left + phoneW
                        val edgePx = settings.edgeWidthDp.dp.toPx()
                            .coerceAtLeast(GestureConfig.ACCESSIBILITY_MIN_EDGE_WIDTH_DP.dp.toPx())
                            .coerceAtMost(GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_DP.dp.toPx())
                            .coerceAtMost(phoneW * GestureConfig.ACCESSIBILITY_MAX_EDGE_WIDTH_SCREEN_PERCENT)
                        val systemGapPx = if (lsposed) {
                            0f
                        } else {
                            GestureConfig.ACCESSIBILITY_SYSTEM_GESTURE_GAP_DP.dp.toPx()
                                .coerceAtMost(phoneW * GestureConfig.ACCESSIBILITY_MAX_SYSTEM_GESTURE_GAP_SCREEN_PERCENT)
                        }
                        val distancePx = settings.swipeDistanceDp.dp.toPx().coerceAtMost(phoneW * 0.52f)
                        val start = minOf(settings.triggerRegionStartPercent, settings.triggerRegionEndPercent)
                            .coerceIn(0, 100) / 100f
                        val end = maxOf(settings.triggerRegionStartPercent, settings.triggerRegionEndPercent)
                            .coerceIn(0, 100) / 100f
                        val activeTop = top + phoneH * start
                        val activeBottom = top + phoneH * end

                        drawRoundRect(
                            color = phoneColor,
                            topLeft = Offset(left, top),
                            size = Size(phoneW, phoneH),
                            cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
                        )

                        if (!lsposed) {
                            drawRect(systemColor, Offset(left, top), Size(systemGapPx, phoneH))
                            drawRect(systemColor, Offset(right - systemGapPx, top), Size(systemGapPx, phoneH))
                        }

                        drawRect(edgeColor.copy(alpha = 0.14f), Offset(left + systemGapPx, top), Size(edgePx, phoneH))
                        drawRect(edgeColor.copy(alpha = 0.14f), Offset(right - systemGapPx - edgePx, top), Size(edgePx, phoneH))
                        drawRect(
                            color = edgeColor.copy(alpha = if (lsposed) 0.52f else 0.38f),
                            topLeft = Offset(left + systemGapPx, activeTop),
                            size = Size(edgePx, activeBottom - activeTop)
                        )
                        drawRect(
                            color = edgeColor.copy(alpha = if (lsposed) 0.52f else 0.38f),
                            topLeft = Offset(right - systemGapPx - edgePx, activeTop),
                            size = Size(edgePx, activeBottom - activeTop)
                        )

                        val activeBorder = if (enhancedMode == lsposed) edgeColor.copy(alpha = 0.72f) else borderColor
                        drawRoundRect(
                            color = activeBorder,
                            topLeft = Offset(left, top),
                            size = Size(phoneW, phoneH),
                            cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                            style = Stroke(width = if (enhancedMode == lsposed) 2.dp.toPx() else 1.4.dp.toPx())
                        )

                        val activeSpan = (activeBottom - activeTop).coerceAtLeast(1f)
                        val triggerY = activeTop + activeSpan * 0.72f
                        val triggerX = right - systemGapPx - edgePx / 2f
                        val guideColor = edgeColor
                        val centerEnd = Offset(
                            (triggerX - distancePx * 0.38f).coerceAtLeast(left + systemGapPx + edgePx),
                            (triggerY - distancePx).coerceAtLeast(top + 12.dp.toPx())
                        )
                        drawArrow(
                            start = Offset(triggerX, triggerY),
                            end = centerEnd,
                            arrowSize = 7.dp.toPx(),
                            color = guideColor,
                            stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                        val radians = Math.toRadians(settings.swipeAngleDegrees.toDouble()).toFloat()
                        listOf((-Math.PI / 2).toFloat() - radians, (-Math.PI / 2).toFloat() + radians).forEach { angle ->
                            drawLine(
                                color = guideColor.copy(alpha = 0.66f),
                                start = Offset(triggerX, triggerY),
                                end = Offset(
                                    triggerX + cos(angle) * distancePx * 0.78f,
                                    triggerY + sin(angle) * distancePx * 0.78f
                                ),
                                strokeWidth = 1.3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                        drawCircle(guideColor, radius = 3.6.dp.toPx(), center = Offset(triggerX, triggerY))
                    }

                    drawEnginePreview(firstLeft, lsposed = false)
                    drawEnginePreview(firstLeft + phoneW + gap, lsposed = true)
                }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Spacer(Modifier.weight(12f))
                        PreviewModeLabel(
                            color = edgeColor.copy(alpha = 0.38f),
                            title = t("普通", "Standard"),
                            subtitle = t("留侧滑", "Gap"),
                            modifier = Modifier.weight(34f)
                        )
                        Spacer(Modifier.weight(8f))
                        PreviewModeLabel(
                            color = edgeColor.copy(alpha = 0.52f),
                            title = "LSPosed",
                            subtitle = t("贴边", "Edge"),
                            modifier = Modifier.weight(34f)
                        )
                        Spacer(Modifier.weight(12f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "±${settings.swipeAngleDegrees}° / ${settings.swipeDistanceDp}dp",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, RoundedCornerShape(3.dp))
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
