package com.example.myedgegesture.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * 设置滑块组件 - 紧凑布局 + 步进按钮
 */
@Composable
fun SettingSlider(
    title: String,
    valueText: String,
    description: String?,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            var lastValue by remember { mutableIntStateOf(value) }
            val scaleAnim = remember { Animatable(1f) }

            LaunchedEffect(value) {
                if (value != lastValue) {
                    lastValue = value
                    scaleAnim.animateTo(1.15f, animationSpec = tween(50))
                    scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.4f))
                }
            }

            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.scale(scaleAnim.value),
            )
        }
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            FilledTonalIconButton(
                onClick = {
                    val newValue = (value - 1).coerceIn(range.first, range.last)
                    if (newValue != value) onValueChange(newValue)
                },
                modifier = Modifier.size(32.dp),
                colors =
                    IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
            ) {
                Icon(
                    Icons.Rounded.Remove,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
            Slider(
                value = value.coerceIn(range.first, range.last).toFloat(),
                onValueChange = { next ->
                    val rounded = next.roundToInt().coerceIn(range.first, range.last)
                    if (rounded != value) {
                        onValueChange(rounded)
                    }
                },
                valueRange = range.first.toFloat()..range.last.toFloat(),
                modifier = Modifier.weight(1f),
                colors =
                    SliderDefaults.colors(
                        inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.38f),
                    ),
            )
            FilledTonalIconButton(
                onClick = {
                    val newValue = (value + 1).coerceIn(range.first, range.last)
                    if (newValue != value) onValueChange(newValue)
                },
                modifier = Modifier.size(32.dp),
                colors =
                    IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
