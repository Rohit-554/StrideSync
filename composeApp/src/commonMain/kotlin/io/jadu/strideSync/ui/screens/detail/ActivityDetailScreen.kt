package io.jadu.strideSync.ui.screens.detail

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.jadu.strideSync.domain.model.Comment
import io.jadu.strideSync.map.PlatformMapView
import io.jadu.strideSync.ui.components.SportTypeIcon
import io.jadu.strideSync.ui.components.StrideIconButton
import io.jadu.strideSync.ui.theme.StrideColors
import io.jadu.strideSync.ui.viewmodel.ActivityDetailViewModel
import io.jadu.strideSync.utils.Formatters
import io.jadu.strideSync.utils.PolylineDecoder
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ActivityDetailScreen(
    activityId: String,
    onBack: () -> Unit = {}
) {
    val viewModel: ActivityDetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(activityId) {
        viewModel.loadActivity(activityId)
    }

    ActivityDetailContent(
        uiState = uiState,
        onBack = onBack,
        onToggleKudos = { viewModel.toggleKudos(activityId) },
        onCommentTextChange = { viewModel.onCommentTextChange(it) },
        onSendComment = { viewModel.addComment(activityId) }
    )
}

@Composable
private fun ActivityDetailContent(
    uiState: ActivityDetailViewModel.ActivityDetailUiState,
    onBack: () -> Unit,
    onToggleKudos: () -> Unit,
    onCommentTextChange: (String) -> Unit,
    onSendComment: () -> Unit
) {
    val activity = uiState.activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StrideColors.Background)
    ) {
        if (uiState.isLoading && activity == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (activity == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.errorMessage ?: "Activity not found",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(Spacing.lg))
                    Button(onClick = onBack) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = Spacing.d104, bottom = Spacing.d120),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                item {
                    val gpsPoints = remember(activity.polyline) {
                        PolylineDecoder.decode(activity.polyline)
                    }
                    PlatformMapView(
                        gpsPoints = gpsPoints,
                        modifier = Modifier
                            .padding(horizontal = Spacing.lg)
                            .fillMaxWidth()
                            .height(Spacing.d220)
                            .clip(RoundedCornerShape(Spacing.md))
                    )
                }

                item {
                    AthleteInfoRow(
                        displayName = uiState.athlete?.displayName ?: "Unknown athlete",
                        timeAgo = Formatters.timeAgo(activity.startedAt),
                        sportType = activity.sportType
                    )
                }

                item {
                    Text(
                        text = activity.title,
                        color = StrideColors.TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                }

                item {
                    ActivityStatsGrid(
                        distanceKm = Formatters.metersToKmString(activity.distanceM),
                        duration = Formatters.formatDuration(activity.durationSec),
                        pace = activity.avgPace?.let { Formatters.formatPace(it) } ?: "—",
                        elevation = "+${activity.elevationM.toInt()}",
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                        color = StrideColors.SurfaceAlt
                    )
                }

                item {
                    KudosRow(
                        kudosCount = uiState.kudosCount,
                        hasKudosed = uiState.hasKudosed,
                        onToggleKudos = onToggleKudos,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                        color = StrideColors.SurfaceAlt
                    )
                }

                item {
                    Text(
                        text = "Comments",
                        color = StrideColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                }

                items(uiState.comments) { comment ->
                    CommentCard(
                        comment = comment,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                }

                if (uiState.comments.isEmpty()) {
                    item {
                        Text(
                            text = "No comments yet. Be the first!",
                            color = StrideColors.TextSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                        )
                    }
                }
            }

            ActivityTopBar(onBack = onBack)

            CommentInputBar(
                value = uiState.commentText,
                onValueChange = onCommentTextChange,
                onSend = onSendComment,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .background(StrideColors.Background.copy(alpha = 0.95f))
            )
        }
    }
}

@Composable
private fun ActivityTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(StrideColors.Background.copy(alpha = 0.9f))
            .statusBarsPadding()
            .height(Spacing.d64)
            .padding(horizontal = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StrideIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            onClick = onBack
        )
        Text(
            text = "Activity",
            color = StrideColors.TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(Spacing.d40))
    }
}

@Composable
private fun AthleteInfoRow(
    displayName: String,
    timeAgo: String,
    sportType: io.jadu.strideSync.domain.model.SportType
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(Spacing.d48)
                .background(StrideColors.SurfaceWarm, CircleShape)
                .border(Spacing.xxs, StrideColors.SurfaceAlt, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayName.take(2).uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName,
                color = StrideColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = timeAgo,
                color = StrideColors.TextSecondary,
                fontSize = 14.sp
            )
        }
        SportTypeIcon(
            sportType = sportType,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ActivityStatsGrid(
    distanceKm: String,
    duration: String,
    pace: String,
    elevation: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            StatCell(modifier = Modifier.weight(1f), value = distanceKm, unit = "km", label = "Distance", valueFontSize = 32)
            StatCell(modifier = Modifier.weight(1f), value = pace, unit = "/km", label = "Avg Pace", valueFontSize = 32)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            StatCell(modifier = Modifier.weight(1f), value = duration, unit = null, label = "Moving Time", valueFontSize = 24)
            StatCell(modifier = Modifier.weight(1f), value = elevation, unit = "m", label = "Elevation", valueFontSize = 24)
        }
    }
}

@Composable
private fun StatCell(
    value: String,
    unit: String?,
    label: String,
    valueFontSize: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(StrideColors.Surface, RoundedCornerShape(Spacing.md))
            .padding(Spacing.lg)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = StrideColors.TextPrimary,
                    fontSize = valueFontSize.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                if (unit != null) {
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = unit,
                        color = StrideColors.TextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = Spacing.xxs)
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = label.uppercase(),
                color = StrideColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun KudosRow(
    kudosCount: Int,
    hasKudosed: Boolean,
    onToggleKudos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$kudosCount kudos",
            color = StrideColors.TextSecondary,
            fontSize = 14.sp
        )

        Button(
            onClick = onToggleKudos,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasKudosed) StrideColors.BrandPrimaryStrong else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (hasKudosed) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = Spacing.xl, vertical = Spacing.d0),
            modifier = Modifier
                .height(Spacing.d44)
                .shadow(
                    elevation = Spacing.sm,
                    shape = CircleShape,
                    ambientColor = StrideColors.BrandPrimarySoft,
                    spotColor = StrideColors.BrandPrimarySoft
                )
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = null,
                modifier = Modifier.size(Spacing.d18)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = if (hasKudosed) "Kudos Given" else "Give Kudos",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun CommentCard(
    comment: Comment,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(StrideColors.Surface, RoundedCornerShape(Spacing.md))
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(Spacing.d40)
                .background(StrideColors.SurfaceWarm, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.displayName.take(2).uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = comment.displayName,
                    color = StrideColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = Formatters.timeAgo(comment.createdAt),
                    color = StrideColors.TextSecondary,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = comment.text,
                color = StrideColors.TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun CommentInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Spacing.d48)
                .background(StrideColors.SurfaceAlt, CircleShape)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = StrideColors.TextPrimary,
                    fontSize = 14.sp
                ),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = Spacing.lg, end = Spacing.d52),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(
                                    text = "Add a comment...",
                                    color = StrideColors.TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
            StrideIconButton(
                icon = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send comment",
                onClick = onSend,
                isFavoriteActive = value.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = Spacing.xs)
            )
        }
    }
}
