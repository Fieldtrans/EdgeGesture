package com.example.edgegesture.ui.screens

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
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.edgegesture.data.model.SettingsState
import com.example.edgegesture.ui.utils.t
import com.example.edgegesture.ui.viewmodel.HookStatus
import kotlinx.coroutines.launch
import kotlin.math.abs

private data class SettingsPage(
    val title: String,
    val icon: ImageVector,
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
    onShowGuide: () -> Unit,
) {
    val pages =
        remember {
            listOf(
                SettingsPage(t("总览", "Overview"), Icons.Rounded.Settings),
                SettingsPage(t("触发", "Trigger"), Icons.Rounded.TouchApp),
                SettingsPage(t("指针", "Pointer"), Icons.Rounded.RadioButtonChecked),
                SettingsPage(t("动作", "Actions"), Icons.Rounded.AccountTree),
            )
        }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/json"),
        ) { uri ->
            uri?.let(onExport)
        }
    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri?.let(onImport)
        }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("EdgeGesture") },
                scrollBehavior = scrollBehavior,
                colors =
                    TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                actions = {
                    IconButton(onClick = onShowGuide) {
                        Icon(Icons.AutoMirrored.Rounded.HelpOutline, contentDescription = t("打开新手指南", "Open guide"))
                    }
                    IconButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) }) {
                        Icon(Icons.Rounded.Upload, contentDescription = t("导入配置", "Import config"))
                    }
                    IconButton(onClick = { exportLauncher.launch("EdgeGesture-config.json") }) {
                        Icon(Icons.Rounded.Download, contentDescription = t("导出配置", "Export config"))
                    }
                    IconButton(onClick = onReset) {
                        Icon(Icons.Rounded.Restore, contentDescription = t("恢复推荐值", "Restore recommended values"))
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 0.dp,
            ) {
                pages.forEachIndexed { index, page ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                if (abs(pagerState.currentPage - index) <= 1) {
                                    pagerState.animateScrollToPage(
                                        page = index,
                                        animationSpec =
                                            tween(
                                                durationMillis = 300,
                                                easing = FastOutSlowInEasing,
                                            ),
                                    )
                                } else {
                                    pagerState.scrollToPage(index)
                                }
                            }
                        },
                        icon = { Icon(page.icon, contentDescription = null) },
                        label = { Text(page.title) },
                        alwaysShowLabel = true,
                        colors =
                            NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            Box(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { pageIndex ->
                    val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                    val alpha = 1f - kotlin.math.abs(pageOffset).coerceIn(0f, 1f) * 0.3f
                    val scale = 1f - kotlin.math.abs(pageOffset).coerceIn(0f, 1f) * 0.05f

                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .alpha(alpha)
                                .scale(scale),
                    ) {
                        when (pageIndex) {
                            1 ->
                                TriggerPage(
                                    settings = settings,
                                    onSettingsChange = onSettingsChange,
                                    hookStatus = hookStatus,
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                )
                            2 ->
                                PointerPage(
                                    settings = settings,
                                    onSettingsChange = onSettingsChange,
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                )
                            else -> {
                                LazyColumn(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    item {
                                        when (pageIndex) {
                                            0 -> OverviewPage(settings, onSettingsChange, hookStatus)
                                            3 -> ActionPage(settings, onSettingsChange)
                                        }
                                    }
                                    item { Spacer(Modifier.height(16.dp)) }
                                }
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
