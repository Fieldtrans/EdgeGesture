package com.example.myedgegesture.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myedgegesture.GestureConfig
import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.ui.components.SettingSwitch
import com.example.myedgegesture.ui.utils.t
import com.example.myedgegesture.ui.viewmodel.HookStatus

/**
 * Status card showing module load state and enable switch
 */
@Composable
fun StatusCard(
    settings: SettingsState,
    hookStatus: HookStatus,
    onSettingsChange: (SettingsState) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            if (hookStatus.active) Color(0xFF10A85A) else Color(0xFFE05A47),
                            CircleShape
                        )
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = t("模块状态", "Module Status"),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = hookStatus.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            SettingSwitch(
                title = t("启用模块手势", "Enable Module Gestures"),
                description = t("关闭后不处理边缘上划。", "When disabled, edge swipe-up will not be handled."),
                checked = settings.enabled,
                onCheckedChange = { onSettingsChange(settings.copy(enabled = it)) }
            )
        }
    }
}

/**
 * Mode selector: Line Arrow or Tracker Cursor
 */
@Composable
fun ModeSelector(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        FilterChip(
            selected = settings.pointerControlStyle == GestureConfig.POINTER_STYLE_LINE_ARROW,
            onClick = {
                onSettingsChange(
                    settings.copy(pointerControlStyle = GestureConfig.POINTER_STYLE_LINE_ARROW)
                )
            },
            label = { Text(t("直线箭头", "Line Arrow")) },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        )
        FilterChip(
            selected = settings.pointerControlStyle == GestureConfig.POINTER_STYLE_TRACKER_CURSOR,
            onClick = {
                onSettingsChange(
                    settings.copy(pointerControlStyle = GestureConfig.POINTER_STYLE_TRACKER_CURSOR)
                )
            },
            label = { Text(t("摇杆光标", "Tracker Cursor")) },
            leadingIcon = {
                Icon(
                    Icons.Rounded.RadioButtonChecked,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}
