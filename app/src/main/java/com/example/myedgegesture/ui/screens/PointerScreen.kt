package com.example.myedgegesture.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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

private enum class PointerSubPage {
    Line,
    Tracker,
    Appearance
}

@Composable
fun PointerPage(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
    modifier: Modifier = Modifier
) {
    var subPage by remember {
        mutableStateOf(
            if (settings.pointerControlStyle == GestureConfig.POINTER_STYLE_TRACKER_CURSOR) {
                PointerSubPage.Tracker
            } else {
                PointerSubPage.Line
            }
        )
    }

    LaunchedEffect(settings.pointerControlStyle) {
        if (subPage != PointerSubPage.Appearance) {
            subPage = if (settings.pointerControlStyle == GestureConfig.POINTER_STYLE_TRACKER_CURSOR) {
                PointerSubPage.Tracker
            } else {
                PointerSubPage.Line
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PointerPreview(settings)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SettingsSection(t("指针设置", "Pointer Settings"), Icons.Rounded.RadioButtonChecked) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        FilterChip(
                            selected = subPage == PointerSubPage.Line,
                            onClick = {
                                subPage = PointerSubPage.Line
                                onSettingsChange(settings.copy(pointerControlStyle = GestureConfig.POINTER_STYLE_LINE_ARROW))
                            },
                            label = { Text(t("直线", "Line")) },
                            leadingIcon = {
                                Icon(Icons.Rounded.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        )
                        FilterChip(
                            selected = subPage == PointerSubPage.Tracker,
                            onClick = {
                                subPage = PointerSubPage.Tracker
                                onSettingsChange(settings.copy(pointerControlStyle = GestureConfig.POINTER_STYLE_TRACKER_CURSOR))
                            },
                            label = { Text(t("摇杆", "Tracker")) },
                            leadingIcon = {
                                Icon(Icons.Rounded.RadioButtonChecked, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        )
                        FilterChip(
                            selected = subPage == PointerSubPage.Appearance,
                            onClick = { subPage = PointerSubPage.Appearance },
                            label = { Text(t("外观", "Appearance")) },
                            leadingIcon = {
                                Icon(Icons.Rounded.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        )
                    }
                }
            }

            item {
                when (subPage) {
                    PointerSubPage.Line -> LinePointerPage(settings, onSettingsChange)
                    PointerSubPage.Tracker -> TrackerPointerPage(settings, onSettingsChange)
                    PointerSubPage.Appearance -> AppearancePage(settings, onSettingsChange)
                }
            }
        }
    }
}

@Composable
private fun LinePointerPage(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit
) {
    SettingsSection(t("直线箭头", "Line Arrow"), Icons.Rounded.Tune) {
        SettingSlider(
            title = t("速度 / 灵敏度", "Speed / Sensitivity"),
            valueText = "${settings.pointerSensitivity}%",
            description = t("基础移动倍率，慢速时精确、快速时会加速放大。", "Base multiplier; slow moves are precise, fast moves are amplified."),
            value = settings.pointerSensitivity,
            range = 40..180,
            onValueChange = { onSettingsChange(settings.copy(pointerSensitivity = it)) }
        )
        SettingSlider(
            title = t("最大速度", "Max Speed"),
            valueText = t("${settings.pointerMaxSpeed}%屏高/秒", "${settings.pointerMaxSpeed}% screen/s"),
            description = t("限制指针每秒最多移动多少屏幕高度。", "Limits how much screen height the pointer can move per second."),
            value = settings.pointerMaxSpeed,
            range = 40..500,
            onValueChange = { onSettingsChange(settings.copy(pointerMaxSpeed = it)) }
        )
        SettingSlider(
            title = t("点击区域", "Touch Area"),
            valueText = "${settings.pointerTouchAreaDp}dp",
            description = t("注入点击时模拟手指接触面积。", "Simulated contact area used when injecting a tap."),
            value = settings.pointerTouchAreaDp,
            range = 6..48,
            onValueChange = { onSettingsChange(settings.copy(pointerTouchAreaDp = it)) }
        )
    }

    SettingsSection(t("直线高级", "Line Advanced"), Icons.Rounded.Settings) {
        SettingSlider(
            title = t("取消圆半径", "Cancel Circle Radius"),
            valueText = "${settings.pointerRadiusDp}dp",
            description = t("指针在这个圆内松手会取消点击。", "Release inside this circle cancels the tap."),
            value = settings.pointerRadiusDp,
            range = 48..320,
            onValueChange = { onSettingsChange(settings.copy(pointerRadiusDp = it)) }
        )
        SettingSlider(
            title = t("自动取消时间", "Auto Cancel Time"),
            valueText = t("${settings.timeoutSeconds}秒", "${settings.timeoutSeconds}s"),
            description = t("定位太久会自动取消，避免指针一直停留。", "Cancels after a long hold so the pointer does not stay on screen."),
            value = settings.timeoutSeconds,
            range = 2..10,
            onValueChange = { onSettingsChange(settings.copy(pointerTimeoutMs = it * 1000)) }
        )
        SettingSlider(
            title = t("平滑度", "Smoothing"),
            valueText = "${settings.pointerSmoothing}%",
            description = t("越高越稳，越低越跟手。", "Higher is steadier; lower follows faster."),
            value = settings.pointerSmoothing,
            range = 5..90,
            onValueChange = { onSettingsChange(settings.copy(pointerSmoothing = it)) }
        )
        SettingSlider(
            title = t("控制曲线", "Control Curve"),
            valueText = "${settings.pointerCurve}%",
            description = t("控制加速强度，越高快速移动时指针跳得越远。", "Controls acceleration intensity; higher values make the pointer jump further on fast movement."),
            value = settings.pointerCurve,
            range = 60..220,
            onValueChange = { onSettingsChange(settings.copy(pointerCurve = it)) }
        )
        SettingSlider(
            title = t("边界留白", "Edge Margin"),
            valueText = "${settings.pointerMarginDp}dp",
            description = t("指针离屏幕边缘的最小距离。", "Minimum distance between the pointer and screen edges."),
            value = settings.pointerMarginDp,
            range = 0..48,
            onValueChange = { onSettingsChange(settings.copy(pointerMarginDp = it)) }
        )
    }
}

@Composable
private fun TrackerPointerPage(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit
) {
    SettingsSection(t("摇杆光标", "Tracker Cursor"), Icons.Rounded.RadioButtonChecked) {
        SettingSlider(
            title = t("取消圆半径", "Cancel Circle Radius"),
            valueText = "${settings.pointerRadiusDp}dp",
            description = t("光标在这个圆内松手会取消点击。", "Release inside this circle cancels the tap."),
            value = settings.pointerRadiusDp,
            range = 48..320,
            onValueChange = { onSettingsChange(settings.copy(pointerRadiusDp = it)) }
        )
        SettingSlider(
            title = t("摇杆灵敏度", "Tracker Sensitivity"),
            valueText = "${settings.trackerSensitivity}%",
            description = t("摇杆移动到光标移动的比例。", "Ratio between tracker movement and cursor movement."),
            value = settings.trackerSensitivity,
            range = 40..220,
            onValueChange = { onSettingsChange(settings.copy(trackerSensitivity = it)) }
        )
        SettingSlider(
            title = t("摇杆最大速度", "Tracker Max Speed"),
            valueText = t("${settings.trackerMaxSpeed}%屏高/秒", "${settings.trackerMaxSpeed}% screen/s"),
            description = t("限制摇杆模式的光标速度。", "Limits cursor speed in tracker mode."),
            value = settings.trackerMaxSpeed,
            range = 40..500,
            onValueChange = { onSettingsChange(settings.copy(trackerMaxSpeed = it)) }
        )
        SettingSlider(
            title = t("光标圆大小", "Cursor Size"),
            valueText = "${settings.trackerCursorDp}dp",
            description = t("松手点击光标圆中心。", "Release taps the center of the cursor circle."),
            value = settings.trackerCursorDp,
            range = 8..56,
            onValueChange = { onSettingsChange(settings.copy(trackerCursorDp = it)) }
        )
        SettingSlider(
            title = t("摇杆圆球大小", "Tracker Ball Size"),
            valueText = "${settings.trackerBallDp}dp",
            description = t("手指附近显示的摇杆中心点。", "Size of the tracker center shown near your finger."),
            value = settings.trackerBallDp,
            range = 4..32,
            onValueChange = { onSettingsChange(settings.copy(trackerBallDp = it)) }
        )
        SettingSlider(
            title = t("摇杆平滑度", "Tracker Smoothing"),
            valueText = "${settings.trackerSmoothing}%",
            description = t("越高越稳，越低越跟手。", "Higher is steadier; lower follows faster."),
            value = settings.trackerSmoothing,
            range = 5..90,
            onValueChange = { onSettingsChange(settings.copy(trackerSmoothing = it)) }
        )
    }
}

@Composable
private fun AppearancePage(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit
) {
    SettingsSection(t("取消圆", "Cancel Circle"), Icons.Rounded.Palette) {
        SettingSlider(
            title = t("取消圆透明度", "Cancel Circle Opacity"),
            valueText = settings.pointerControlAlpha.toString(),
            description = t("调到 0 就隐藏取消圆。", "Set to 0 to hide the cancel circle."),
            value = settings.pointerControlAlpha,
            range = 0..255,
            onValueChange = { onSettingsChange(settings.copy(pointerControlAlpha = it)) }
        )
    }

    SettingsSection(t("外观", "Appearance"), Icons.Rounded.Palette) {
        SettingSlider(
            title = t("箭头大小", "Arrow Size"),
            valueText = "${settings.pointerArrowDp}dp",
            description = t("只影响直线箭头模式。", "Only affects line arrow mode."),
            value = settings.pointerArrowDp,
            range = 8..36,
            onValueChange = { onSettingsChange(settings.copy(pointerArrowDp = it)) }
        )
        SettingSlider(
            title = t("线条粗细", "Line Width"),
            valueText = "${settings.pointerLineDp}dp",
            description = t("只影响直线箭头和摇杆中心点描边。", "Only affects the line arrow and tracker center outline."),
            value = settings.pointerLineDp,
            range = 1..8,
            onValueChange = { onSettingsChange(settings.copy(pointerLineDp = it)) }
        )
    }

    SettingsSection(t("颜色", "Color"), Icons.Rounded.Palette) {
        ColorSwatch(settings.pointerColor)
        SettingSlider(
            title = t("红色", "Red"),
            valueText = settings.pointerColorRed.toString(),
            description = null,
            value = settings.pointerColorRed,
            range = 0..255,
            onValueChange = { onSettingsChange(settings.copy(pointerColorRed = it)) }
        )
        SettingSlider(
            title = t("绿色", "Green"),
            valueText = settings.pointerColorGreen.toString(),
            description = null,
            value = settings.pointerColorGreen,
            range = 0..255,
            onValueChange = { onSettingsChange(settings.copy(pointerColorGreen = it)) }
        )
        SettingSlider(
            title = t("蓝色", "Blue"),
            valueText = settings.pointerColorBlue.toString(),
            description = null,
            value = settings.pointerColorBlue,
            range = 0..255,
            onValueChange = { onSettingsChange(settings.copy(pointerColorBlue = it)) }
        )
    }
}

@Composable
fun PointerPreview(settings: SettingsState) {
    SettingsSection(t("动态预览", "Live Preview"), Icons.Rounded.Palette) {
        val color = settings.pointerColor
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(184.dp)
                    .padding(10.dp)
            ) {
                val stroke = Stroke(width = settings.pointerLineDp.dp.toPx(), cap = StrokeCap.Round)
                val anchor = Offset(size.width * 0.72f, size.height * 0.62f)
                val cancelRadius = settings.pointerRadiusDp.dp.toPx()
                    .coerceAtMost(size.height * 0.32f)
                    .coerceAtMost(size.width * 0.22f)
                val cancelAlpha = settings.pointerControlAlpha.coerceIn(0, 255) / 255f

                // Draw cancel circle
                if (cancelAlpha > 0f) {
                    drawCircle(
                        color = color.copy(alpha = cancelAlpha),
                        radius = cancelRadius,
                        center = anchor,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = color.copy(alpha = cancelAlpha * 0.24f),
                        radius = cancelRadius,
                        center = anchor
                    )
                }

                if (settings.pointerControlStyle == GestureConfig.POINTER_STYLE_TRACKER_CURSOR) {
                    val cursor = Offset(size.width * 0.32f, size.height * 0.30f)
                    // Tracker ball at anchor
                    drawCircle(
                        color = color,
                        radius = (settings.trackerBallDp.dp.toPx() / 2f).coerceAtLeast(3.dp.toPx()),
                        center = anchor
                    )
                    // Line from anchor to cursor
                    drawLine(
                        color = color.copy(alpha = 0.45f),
                        start = anchor,
                        end = cursor,
                        strokeWidth = 1.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Cursor circle (fill + stroke)
                    drawCircle(
                        color = color.copy(alpha = 0.20f),
                        radius = settings.trackerCursorDp.dp.toPx() / 2f,
                        center = cursor
                    )
                    drawCircle(
                        color = color,
                        radius = settings.trackerCursorDp.dp.toPx() / 2f,
                        center = cursor,
                        style = Stroke(width = 2.dp.toPx())
                    )
                } else {
                    val start = Offset(size.width * 0.92f, anchor.y)
                    val end = Offset(size.width * 0.28f, size.height * 0.24f)
                    drawArrow(start, end, settings.pointerArrowDp.dp.toPx(), color, stroke)
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(color, CircleShape)
        )
        Text(
            t("当前指针颜色", "Current Pointer Color"),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
