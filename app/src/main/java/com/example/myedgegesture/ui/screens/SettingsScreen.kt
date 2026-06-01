package com.example.myedgegesture.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.ui.utils.t
import com.example.myedgegesture.ui.viewmodel.HookStatus
import kotlinx.coroutines.launch
import kotlin.math.abs

private data class SettingsPage(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
    onReset: () -> Unit,
    onExport: (Uri) -> Unit,
    onImport: (Uri) -> Unit,
    hookStatus: HookStatus,
    onShowGuide: () -> Unit
) {
    val pages = remember {
        listOf(
            SettingsPage(t("总览", "Overview"), Icons.Rounded.Settings),
            SettingsPage(t("触发", "Trigger"), Icons.Rounded.TouchApp),
            SettingsPage(t("指针", "Pointer"), Icons.Rounded.RadioButtonChecked),
            SettingsPage(t("动作", "Actions"), Icons.Rounded.AccountTree)
        )
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let(onExport)
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let(onImport)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("EdgeGesture", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (settings.enabled) t("手势已启用", "Gestures enabled") else t("手势未启用", "Gestures disabled"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) }) {
                        Icon(Icons.Rounded.Upload, contentDescription = t("导入配置", "Import config"))
                    }
                    IconButton(onClick = { exportLauncher.launch("EdgeGesture-config.json") }) {
                        Icon(Icons.Rounded.Download, contentDescription = t("导出配置", "Export config"))
                    }
                    IconButton(onClick = onReset) {
                        Icon(Icons.Rounded.Restore, contentDescription = t("恢复推荐值", "Restore recommended values"))
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                pages.forEachIndexed { index, page ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                if (abs(pagerState.currentPage - index) <= 1) {
                                    pagerState.animateScrollToPage(
                                        page = index,
                                        animationSpec = tween(
                                            durationMillis = 260,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                } else {
                                    pagerState.scrollToPage(index)
                                }
                            }
                        },
                        icon = { Icon(page.icon, contentDescription = null) },
                        label = { Text(page.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                ) { pageIndex ->
                    when (pageIndex) {
                        1 -> TriggerPage(
                            settings = settings,
                            onSettingsChange = onSettingsChange,
                            hookStatus = hookStatus,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp, vertical = 16.dp)
                        )
                        2 -> PointerPage(
                            settings = settings,
                            onSettingsChange = onSettingsChange,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp, vertical = 16.dp)
                        )
                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 18.dp),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                item {
                                    when (pageIndex) {
                                        0 -> OverviewPage(settings, onSettingsChange, hookStatus, onShowGuide)
                                        3 -> ActionPage(settings, onSettingsChange)
                                    }
                                }
                                item { Spacer(Modifier.height(24.dp)) }
                            }
                        }
                    }
                }
                if (pagerState.currentPage == 1) {
                    LiveTriggerPreview(settings, hookStatus.active)
                }
            }
        }
    }
}
