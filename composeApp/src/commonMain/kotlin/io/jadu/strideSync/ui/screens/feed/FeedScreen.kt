package io.jadu.strideSync.ui.screens.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.jadu.strideSync.domain.model.FeedItem
import io.jadu.strideSync.domain.model.StatusItem
import io.jadu.strideSync.domain.model.User
import io.jadu.strideSync.ui.components.ActivityCard
import io.jadu.strideSync.ui.components.StrideBottomNavigation
import io.jadu.strideSync.ui.theme.CardSurface
import io.jadu.strideSync.ui.theme.SurfaceAlt
import io.jadu.strideSync.ui.theme.TextSecondary
import io.jadu.strideSync.ui.viewmodel.FeedViewModel
import io.jadu.strideSync.utils.Formatters
import org.koin.compose.viewmodel.koinViewModel

private val StatusBackgroundOptions = listOf(
    "#FF571B",
    "#3D5AFE",
    "#0F9D58",
    "#8E24AA",
    "#F9A825"
)
private val StatusFallbackColor = Color(0xFFFF571B)

@Composable
fun FeedScreen(
    onNavigateToRecord: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToActivity: (String) -> Unit = {}
) {
    val viewModel: FeedViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val storyUiState by viewModel.storyUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState !is FeedViewModel.FeedUiState.Loading) {
            viewModel.refresh()
        }
    }

    FeedScreenContent(
        uiState = uiState,
        storyUiState = storyUiState,
        onRefresh = { viewModel.refresh() },
        onLoadMore = { viewModel.loadMore() },
        onKudosToggle = { activityId -> viewModel.toggleKudos(activityId) },
        onNavigateToActivity = onNavigateToActivity,
        onNavigateToRecord = onNavigateToRecord,
        onNavigateToExplore = onNavigateToExplore,
        onNavigateToProfile = onNavigateToProfile,
        onOpenMyStatus = viewModel::openMyStatus,
        onOpenStatus = viewModel::openStatus,
        onDismissStatusViewer = viewModel::dismissStatusViewer,
        onOpenComposerForUpdate = viewModel::openComposerForUpdate,
        onDismissComposer = viewModel::dismissComposer,
        onComposerTextChange = viewModel::onComposerTextChange,
        onComposerBackgroundChange = viewModel::onComposerBackgroundChange,
        onSubmitStatus = viewModel::submitStatus
    )
}

@Composable
private fun FeedScreenContent(
    uiState: FeedViewModel.FeedUiState,
    storyUiState: FeedViewModel.StoryUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onKudosToggle: (String) -> Unit,
    onNavigateToActivity: (String) -> Unit,
    onNavigateToRecord: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onOpenMyStatus: () -> Unit,
    onOpenStatus: (StatusItem) -> Unit,
    onDismissStatusViewer: () -> Unit,
    onOpenComposerForUpdate: () -> Unit,
    onDismissComposer: () -> Unit,
    onComposerTextChange: (String) -> Unit,
    onComposerBackgroundChange: (String) -> Unit,
    onSubmitStatus: () -> Unit
) {
    val selectedTab = "home"

    Scaffold(
        topBar = { FeedTopBar() },
        bottomBar = {
            StrideBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    when (tab) {
                        "explore" -> onNavigateToExplore()
                        "record" -> onNavigateToRecord()
                        "profile" -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (uiState) {
            is FeedViewModel.FeedUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is FeedViewModel.FeedUiState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    StoryStatusRow(
                        currentUser = storyUiState.currentUser,
                        statuses = storyUiState.statuses,
                        onOpenMyStatus = onOpenMyStatus,
                        onOpenStatus = onOpenStatus,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No activities yet.\nFollow athletes to see their activities!",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is FeedViewModel.FeedUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp
                        )
                        TextButton(onClick = onRefresh) {
                            Text("Retry", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            is FeedViewModel.FeedUiState.Success -> {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            StoryStatusRow(
                                currentUser = storyUiState.currentUser,
                                statuses = storyUiState.statuses,
                                onOpenMyStatus = onOpenMyStatus,
                                onOpenStatus = onOpenStatus,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        items(
                            items = uiState.items,
                            key = { it.activity.id }
                        ) { feedItem ->
                            ActivityCard(
                                feedItem = feedItem,
                                onKudosToggle = { onKudosToggle(feedItem.activity.id) },
                                onCardClick = { onNavigateToActivity(feedItem.activity.id) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        if (uiState.hasMore || uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.isLoadingMore) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(8.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                LaunchedEffect(uiState.items.size, uiState.hasMore) {
                                    onLoadMore()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    storyUiState.selectedStatus?.let { status ->
        StatusViewerDialog(
            status = status,
            onDismiss = onDismissStatusViewer,
            onUpdate = if (status.isOwn) onOpenComposerForUpdate else null
        )
    }

    if (storyUiState.isComposerOpen) {
        StatusComposerDialog(
            currentUser = storyUiState.currentUser,
            text = storyUiState.composerText,
            backgroundHex = storyUiState.composerBackgroundHex,
            isSubmitting = storyUiState.isSubmitting,
            errorMessage = storyUiState.errorMessage,
            onDismiss = onDismissComposer,
            onTextChange = onComposerTextChange,
            onBackgroundChange = onComposerBackgroundChange,
            onSubmit = onSubmitStatus
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "StrideSync",
                color = MaterialTheme.colorScheme.primaryContainer,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
private fun StoryStatusRow(
    currentUser: User?,
    statuses: List<StatusItem>,
    onOpenMyStatus: () -> Unit,
    onOpenStatus: (StatusItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val ownStatus = statuses.firstOrNull { it.isOwn }
    val otherStatuses = statuses.filterNot { it.isOwn }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StoryBubble(
            label = "You",
            initials = currentUser?.displayName?.take(2)?.uppercase(),
            avatarUrl = currentUser?.avatarUrl,
            isAdd = ownStatus == null,
            hasStatus = ownStatus != null,
            onClick = onOpenMyStatus
        )

        otherStatuses.forEach { status ->
            StoryBubble(
                label = status.displayName.split(" ").firstOrNull() ?: status.displayName,
                initials = status.displayName.take(2).uppercase(),
                avatarUrl = status.avatarUrl,
                hasStatus = true,
                onClick = { onOpenStatus(status) }
            )
        }
    }
}

@Composable
private fun StoryBubble(
    label: String,
    initials: String?,
    avatarUrl: String?,
    isAdd: Boolean = false,
    hasStatus: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(SurfaceAlt, CircleShape)
                .then(
                    when {
                        isAdd -> Modifier.border(
                            BorderStroke(2.dp, TextSecondary.copy(alpha = 0.8f)),
                            CircleShape
                        )
                        hasStatus -> Modifier.border(
                            BorderStroke(2.5.dp, MaterialTheme.colorScheme.primaryContainer),
                            CircleShape
                        )
                        else -> Modifier.border(
                            BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
                            CircleShape
                        )
                    }
                )
                .padding(if (hasStatus) 2.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isAdd -> Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add status",
                    tint = TextSecondary
                )
                !avatarUrl.isNullOrBlank() -> AsyncImage(
                    model = avatarUrl,
                    contentDescription = label,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(CardSurface)
                )
                else -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(CardSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials.orEmpty(),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Text(
            text = label,
            color = if (isAdd) TextSecondary else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

@Composable
private fun StatusViewerDialog(
    status: StatusItem,
    onDismiss: () -> Unit,
    onUpdate: (() -> Unit)?
) {
    val background = colorFromHex(status.backgroundHex)
    val contentColor = if (background.luminance() > 0.45f) Color(0xFF111318) else Color.White

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = background,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = status.displayName,
                        color = contentColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = Formatters.timeAgo(status.createdAt),
                        color = contentColor.copy(alpha = 0.76f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.7f)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = status.text,
                    color = contentColor,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Visible for 24 hours",
                    color = contentColor.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            if (onUpdate != null) {
                Button(
                    onClick = onUpdate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = contentColor,
                        contentColor = background
                    )
                ) {
                    Text("Update Status")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = contentColor)
                }
            }
        },
        dismissButton = {
            if (onUpdate != null) {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = contentColor)
                }
            }
        }
    )
}

@Composable
private fun StatusComposerDialog(
    currentUser: User?,
    text: String,
    backgroundHex: String,
    isSubmitting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onTextChange: (String) -> Unit,
    onBackgroundChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val previewColor = colorFromHex(backgroundHex)
    val previewTextColor = if (previewColor.luminance() > 0.45f) Color(0xFF111318) else Color.White

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = {
            Text(
                text = "Your Status",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(previewColor)
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = currentUser?.displayName ?: "You",
                            color = previewTextColor.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = text.ifBlank { "Share your run, ride, or plan for today." },
                            color = previewTextColor,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Status") },
                    supportingText = {
                        Text(
                            text = "${text.length}/160",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    minLines = 3,
                    maxLines = 4
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusBackgroundOptions.forEach { option ->
                        val isSelected = option == backgroundHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(colorFromHex(option))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .clickable { onBackgroundChange(option) }
                        )
                    }
                }

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Post Status")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                Text("Cancel")
            }
        }
    )
}

private fun colorFromHex(hex: String): Color {
    val normalized = hex.removePrefix("#")
    val value = normalized.toLongOrNull(16) ?: return StatusFallbackColor
    return Color(0xFF000000 or value)
}

@Composable
private fun Scaffold(
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    containerColor: Color,
    content: @Composable (PaddingValues) -> Unit
) {
    androidx.compose.material3.Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        containerColor = containerColor,
        content = content
    )
}
