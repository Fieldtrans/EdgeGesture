package com.example.myedgegesture.ui.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sqrt

/**
 * Draw an arrow from start to end with arrowhead
 */
fun DrawScope.drawArrow(
    start: Offset,
    end: Offset,
    arrowSize: Float,
    color: Color,
    stroke: Stroke
) {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val length = sqrt(dx * dx + dy * dy)
    if (length < 1f) return

    val ux = dx / length
    val uy = dy / length
    val wing = arrowSize * 0.62f
    val base = Offset(end.x - ux * arrowSize, end.y - uy * arrowSize)
    val left = Offset(base.x + -uy * wing, base.y + ux * wing)
    val right = Offset(base.x - -uy * wing, base.y - ux * wing)

    drawLine(color, start, end, strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawLine(color, end, left, strokeWidth = stroke.width, cap = StrokeCap.Round)
    drawLine(color, end, right, strokeWidth = stroke.width, cap = StrokeCap.Round)
}
