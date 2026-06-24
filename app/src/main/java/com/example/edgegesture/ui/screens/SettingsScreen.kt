package com.example.edgegesture.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.edgegesture.data.model.SettingsState
import com.example.edgegesture.ui.utils.t
import com.example.edgegesture.ui.viewmodel.HookStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private val PAGE_BOTTOM_SPACING = 24.dp
private const val M3_PAGE_TRANSITION_MS = 300
private const val M3_NAV_INDICATOR_MS = 260
private const val M3_ACTION_MORPH_MS = 160
private const val M3_ACTION_CONTENT_IN_MS = 120
private const val M3_ACTION_CONTENT_OUT_MS = 90
private val M3StandardEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
private val M3EmphasizedDecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
private val M3EmphasizedAccelerateEasing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

private data class SettingsPage(
    val title: String,
    val icon: ImageVector,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: SettingsState,
    onSettingsChange: (SettingsState) -> Unit,
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
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {},
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                    actions = {
                        MorphTopActionButton(
                            icon = Icons.AutoMirrored.Rounded.HelpOutline,
                            contentDescription = t("打开新手指南", "Open guide"),
                            onClick = onShowGuide,
                        )
                        MorphTopActionButton(
                            icon = Icons.Rounded.Upload,
                            contentDescription = t("导入配置", "Import config"),
                            onClick = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) },
                        )
                        MorphTopActionButton(
                            icon = Icons.Rounded.Download,
                            contentDescription = t("导出配置", "Export config"),
                            onClick = { exportLauncher.launch("EdgeGesture-config.json") },
                        )
                    },
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 0.dp,
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 6.dp, end = 8.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        pages.forEachIndexed { index, page ->
                            MorphNavigationItem(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        if (abs(pagerState.currentPage - index) <= 1) {
                                            pagerState.animateScrollToPage(
                                                page = index,
                                                animationSpec =
                                                    tween(
                                                        durationMillis = M3_PAGE_TRANSITION_MS,
                                                        easing = M3EmphasizedDecelerateEasing,
                                                    ),
                                            )
                                        } else {
                                            pagerState.scrollToPage(index)
                                        }
                                    }
                                },
                                icon = page.icon,
                                label = page.title,
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { pageIndex ->
                    val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                    val settledOffset = kotlin.math.abs(pageOffset).coerceIn(0f, 1f)
                    val alpha = 1f - settledOffset * 0.16f
                    val scale = 1f - settledOffset * 0.015f

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
                                    bottomSpacing = PAGE_BOTTOM_SPACING,
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                )
                            2 ->
                                PointerPage(
                                    settings = settings,
                                    onSettingsChange = onSettingsChange,
                                    bottomSpacing = PAGE_BOTTOM_SPACING,
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
                                    contentPadding = PaddingValues(top = 12.dp, bottom = PAGE_BOTTOM_SPACING),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    item {
                                        when (pageIndex) {
                                            0 -> OverviewPage(settings, onSettingsChange, hookStatus)
                                            3 -> ActionPage(settings, onSettingsChange)
                                        }
                                    }
                                }
                            }
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

private enum class MorphFeedbackPhase {
    Idle,
    Loading,
}

@Composable
private fun MorphTopActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    var phase by remember { mutableStateOf(MorphFeedbackPhase.Idle) }
    LaunchedEffect(phase) {
        if (phase != MorphFeedbackPhase.Idle) {
            delay(520)
            phase = MorphFeedbackPhase.Idle
        }
    }

    val active = phase != MorphFeedbackPhase.Idle
    val actionMorphSpec = tween<Dp>(M3_ACTION_MORPH_MS, easing = M3StandardEasing)
    val actionColorSpec = tween<Color>(M3_ACTION_MORPH_MS, easing = M3StandardEasing)
    val width by animateDpAsState(
        if (active) 44.dp else 40.dp,
        animationSpec = actionMorphSpec,
        label = "topActionWidth",
    )
    val height by animateDpAsState(
        if (active) 36.dp else 40.dp,
        animationSpec = actionMorphSpec,
        label = "topActionHeight",
    )
    val containerColor by animateColorAsState(
        if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = actionColorSpec,
        label = "topActionColor",
    )
    val contentColor by animateColorAsState(
        if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = actionColorSpec,
        label = "topActionContentColor",
    )
    val cornerRadius by animateDpAsState(
        if (active) 16.dp else 14.dp,
        animationSpec = actionMorphSpec,
        label = "topActionRadius",
    )

    Box(
        modifier =
            Modifier
                .padding(horizontal = 2.dp)
                .size(width, height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(containerColor)
                .clickable {
                    phase = MorphFeedbackPhase.Loading
                    onClick()
                },
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = phase,
            transitionSpec =
                {
                    (
                        fadeIn(animationSpec = tween(M3_ACTION_CONTENT_IN_MS, easing = M3StandardEasing)) +
                            scaleIn(
                                animationSpec = tween(M3_ACTION_CONTENT_IN_MS, easing = M3StandardEasing),
                                initialScale = 0.92f,
                            )
                    ) togetherWith
                        (
                            fadeOut(animationSpec = tween(M3_ACTION_CONTENT_OUT_MS, easing = M3StandardEasing)) +
                                scaleOut(
                                    animationSpec = tween(M3_ACTION_CONTENT_OUT_MS, easing = M3StandardEasing),
                                    targetScale = 0.92f,
                                )
                        )
                },
            label = "topActionContent",
        ) { current ->
            when (current) {
                MorphFeedbackPhase.Idle -> Icon(icon, contentDescription = contentDescription, tint = contentColor)
                MorphFeedbackPhase.Loading ->
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = contentColor,
                    )
            }
        }
    }
}

@Composable
private fun RowScope.MorphNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
) {
    val bgColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        animationSpec = tween(M3_NAV_INDICATOR_MS, easing = M3StandardEasing),
        label = "navBgColor",
    )
    val iconColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(M3_NAV_INDICATOR_MS, easing = M3StandardEasing),
        label = "navIconColor",
    )
    val textColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(M3_NAV_INDICATOR_MS, easing = M3StandardEasing),
        label = "navTextColor",
    )
    val navIndicatorSpec = tween<Dp>(M3_NAV_INDICATOR_MS, easing = M3StandardEasing)
    val indicatorWidth by animateDpAsState(
        if (selected) 62.dp else 44.dp,
        animationSpec = navIndicatorSpec,
        label = "navIndicatorWidth",
    )
    val indicatorRadius by animateDpAsState(
        if (selected) 18.dp else 16.dp,
        animationSpec = navIndicatorSpec,
        label = "navIndicatorRadius",
    )

    Box(
        modifier =
            Modifier
                .weight(1f)
                .height(72.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(width = indicatorWidth, height = 32.dp)
                        .clip(RoundedCornerShape(indicatorRadius))
                        .background(bgColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
