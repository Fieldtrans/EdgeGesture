package com.example.myedgegesture.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myedgegesture.GestureConfig
import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.ui.components.SettingSlider
import com.example.myedgegesture.ui.components.SettingsSection
import com.example.myedgegesture.ui.utils.actionLabel
import com.example.myedgegesture.ui.utils.actionValuesForGesture
import com.example.myedgegesture.ui.utils.edgeLabel
import com.example.myedgegesture.ui.utils.gestureLabel
import com.example.myedgegesture.ui.utils.notificationShadeModeLabel
import com.example.myedgegesture.ui.utils.t

@Composable
fun ActionPage(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit
) {
    var selectedEdge by remember { mutableStateOf("right") }
    SettingsSection(t("边缘", "Edge"), Icons.Rounded.AccountTree) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            GestureConfig.edges.forEach { edge ->
                FilterChip(
                    selected = selectedEdge == edge,
                    onClick = { selectedEdge = edge },
                    label = { Text(edgeLabel(edge)) }
                )
            }
        }
    }

    SettingsSection(t("动作参数", "Action Timing"), Icons.Rounded.Tune) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(t("通知栏触发方式", "Notification Trigger"), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = t(
                    "预动画保持不变。选择碰到顶部立刻下拉，或松手点击顶部后再下拉。",
                    "The preview animation stays unchanged. Choose instant pull-down on top touch, or pull down after releasing on the status bar."
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                GestureConfig.notificationShadeModes.forEach { mode ->
                    FilterChip(
                        selected = settings.notificationShadeMode == mode,
                        onClick = { onSettingsChange(settings.copy(notificationShadeMode = mode)) },
                        label = { Text(notificationShadeModeLabel(mode)) }
                    )
                }
            }
        }
        SettingSlider(
            title = t("双击等待时间", "Double-Tap Wait"),
            valueText = "${settings.doubleTapTimeoutMs}ms",
            description = t(
                "用于判断两次点击是否属于双击；普通边缘点击会优先穿透。",
                "Used to decide whether two taps count as a double-tap; normal edge taps pass through first."
            ),
            value = settings.doubleTapTimeoutMs,
            range = 120..320,
            onValueChange = { onSettingsChange(settings.copy(doubleTapTimeoutMs = it)) }
        )
    }

    SettingsSection(t("${edgeLabel(selectedEdge)}动作", "${edgeLabel(selectedEdge)} Actions"), Icons.Rounded.TouchApp) {
        GestureConfig.gestures.forEach { gesture ->
            val key = GestureConfig.actionKey(selectedEdge, gesture)
            ActionDropdownRow(
                title = gestureLabel(gesture),
                gesture = gesture,
                selectedAction = settings.actionByKey[key] ?: GestureConfig.defaultAction(selectedEdge, gesture),
                onActionSelected = { action ->
                    onSettingsChange(settings.copy(actionByKey = settings.actionByKey + (key to action)))
                }
            )
        }
    }
}

@Composable
private fun ActionDropdownRow(
    title: String,
    gesture: String,
    selectedAction: String,
    onActionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val actions = actionValuesForGesture(gesture)
    val displayAction = selectedAction.takeIf { it in actions } ?: GestureConfig.ACTION_NONE
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(actionLabel(displayAction))
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Rounded.ExpandMore, contentDescription = null)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                actions.forEach { action ->
                    DropdownMenuItem(
                        text = { Text(actionLabel(action)) },
                        onClick = {
                            expanded = false
                            onActionSelected(action)
                        }
                    )
                }
            }
        }
    }
}
