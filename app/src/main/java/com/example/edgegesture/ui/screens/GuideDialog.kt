package com.example.edgegesture.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.edgegesture.ui.utils.t

enum class GuideIllustrationType {
    EdgeSwipe,
    Control,
    ReleaseTap,
    Notification,
}

@Composable
fun NewUserGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t("操作教程", "Tutorial")) },
        text = {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.fillMaxWidth()) {
                    GuideTutorialRow(
                        type = GuideIllustrationType.EdgeSwipe,
                        title = t("斜上划调出", "Swipe Diagonally Up"),
                        body =
                            t(
                                "从左右触发区向屏幕内侧斜上划，出现直线箭头或摇杆光标。",
                                "Swipe diagonally up from a side trigger area to show the line pointer or cursor.",
                            ),
                    )
                    GuideDivider()
                    GuideTutorialRow(
                        type = GuideIllustrationType.Control,
                        title = t("小范围控制", "Small-Area Control"),
                        body =
                            t(
                                "拇指在控制区内移动，箭头尖或光标会映射到远处。",
                                "Move your thumb in the control area; the pointer maps to distant targets.",
                            ),
                    )
                    GuideDivider()
                    GuideTutorialRow(
                        type = GuideIllustrationType.ReleaseTap,
                        title = t("松手点击", "Release to Tap"),
                        body =
                            t(
                                "箭头尖或光标圆心对准目标，松手点击该位置。",
                                "Aim the arrow tip or cursor center, then release to tap there.",
                            ),
                    )
                    GuideDivider()
                    GuideTutorialRow(
                        type = GuideIllustrationType.Notification,
                        title = t("下拉通知栏", "Notifications"),
                        body =
                            t(
                                "移动到顶部会显示预动画，可设置碰到下拉或松手下拉。",
                                "Moving to the top shows a preview; choose pull on touch or release.",
                            ),
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(t("开始使用", "Start"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(t("稍后再看", "Later"))
            }
        },
    )
}

@Composable
private fun GuideTutorialRow(
    type: GuideIllustrationType,
    title: String,
    body: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        GuidePhoneIllustration(type)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = ">",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
        )
    }
}

@Composable
private fun GuideDivider() {
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 96.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
    )
}

@Composable
private fun GuidePhoneIllustration(type: GuideIllustrationType) {
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)
    val fill = MaterialTheme.colorScheme.surface
    val screen = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)

    Canvas(
        modifier = Modifier.size(width = 58.dp, height = 78.dp),
    ) {
        val phoneW = size.width * 0.72f
        val phoneH = size.height * 0.92f
        val left = (size.width - phoneW) / 2f
        val top = (size.height - phoneH) / 2f
        val radius = 12.dp.toPx()
        val innerPad = 8.dp.toPx()
        val innerLeft = left + innerPad
        val innerTop = top + innerPad
        val innerW = phoneW - innerPad * 2f
        val innerH = phoneH - innerPad * 2f

        drawRoundRect(
            color = fill,
            topLeft = Offset(left, top),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(radius, radius),
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(left, top),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = 2.dp.toPx()),
        )

        when (type) {
            GuideIllustrationType.EdgeSwipe -> {
                drawRoundRect(
                    color = screen,
                    topLeft = Offset(innerLeft, innerTop),
                    size = Size(innerW, innerH),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                )
                drawRoundRect(
                    color = primary.copy(alpha = 0.16f),
                    topLeft = Offset(left + phoneW - 5.dp.toPx(), top + phoneH * 0.22f),
                    size = Size(5.dp.toPx(), phoneH * 0.56f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                )
                val start = Offset(left + phoneW - 2.dp.toPx(), top + phoneH * 0.72f)
                val end = Offset(left + phoneW * 0.45f, top + phoneH * 0.36f)
                drawLine(primary.copy(alpha = 0.34f), start, end, strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                drawCircle(primary, radius = 4.dp.toPx(), center = end)
                drawCircle(primary.copy(alpha = 0.22f), radius = 10.dp.toPx(), center = start)
            }
            GuideIllustrationType.Control -> {
                drawRoundRect(
                    color = screen,
                    topLeft = Offset(innerLeft, innerTop),
                    size = Size(innerW, innerH),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                )
                val control = Offset(left + phoneW * 0.32f, top + phoneH * 0.72f)
                val end = Offset(left + phoneW * 0.7f, top + phoneH * 0.28f)
                drawCircle(primary.copy(alpha = 0.16f), radius = 13.dp.toPx(), center = control)
                drawLine(primary.copy(alpha = 0.35f), control, end, strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
                drawLine(primary, Offset(end.x - 5.dp.toPx(), end.y + 8.dp.toPx()), end, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                drawLine(primary, Offset(end.x - 9.dp.toPx(), end.y + 2.dp.toPx()), end, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                drawCircle(primary, radius = 3.5.dp.toPx(), center = control)
                drawCircle(primary, radius = 4.dp.toPx(), center = end)
            }
            GuideIllustrationType.ReleaseTap -> {
                drawRoundRect(
                    color = screen,
                    topLeft = Offset(innerLeft, innerTop),
                    size = Size(innerW, innerH),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                )
                val anchor = Offset(left + phoneW * 0.05f, top + phoneH * 0.62f)
                val tip = Offset(left + phoneW * 0.72f, top + phoneH * 0.38f)
                drawCircle(primary.copy(alpha = 0.14f), radius = 14.dp.toPx(), center = anchor)
                drawCircle(primary.copy(alpha = 0.18f), radius = 10.dp.toPx(), center = tip)
                drawLine(primary.copy(alpha = 0.38f), anchor, tip, strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
                drawLine(primary, Offset(tip.x - 7.dp.toPx(), tip.y + 5.dp.toPx()), tip, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                drawLine(primary, Offset(tip.x - 8.dp.toPx(), tip.y - 2.dp.toPx()), tip, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                drawCircle(primary, radius = 3.5.dp.toPx(), center = tip)
            }
            GuideIllustrationType.Notification -> {
                drawRoundRect(
                    color = screen,
                    topLeft = Offset(innerLeft, innerTop),
                    size = Size(innerW, innerH),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                )
                drawRoundRect(
                    color = primary.copy(alpha = 0.18f),
                    topLeft = Offset(innerLeft, innerTop),
                    size = Size(innerW, 7.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                )
                val anchor = Offset(left + phoneW * 0.74f, top + phoneH * 0.78f)
                val tip = Offset(left + phoneW * 0.5f, innerTop + 2.dp.toPx())
                drawLine(primary.copy(alpha = 0.34f), anchor, tip, strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
                drawLine(primary, Offset(tip.x - 7.dp.toPx(), tip.y + 8.dp.toPx()), tip, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                drawLine(primary, Offset(tip.x + 7.dp.toPx(), tip.y + 8.dp.toPx()), tip, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                drawCircle(primary.copy(alpha = 0.2f), radius = 11.dp.toPx(), center = tip)
                drawCircle(primary, radius = 4.dp.toPx(), center = tip)
            }
        }
    }
}
