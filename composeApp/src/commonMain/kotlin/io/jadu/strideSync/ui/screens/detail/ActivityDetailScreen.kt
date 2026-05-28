package io.jadu.strideSync.ui.screens.detail

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
        activityId = activityId,
        onBack = onBack,
        onToggleKudos = { viewModel.toggleKudos(activityId) },
        onCommentTextChange = { viewModel.onCommentTextChange(it) },
        onSendComment = { viewModel.addComment(activityId) }
    )
}

@Composable
private fun ActivityDetailContent(
    uiState: ActivityDetailViewModel.ActivityDetailUiState,
    activityId: String,
    onBack: () -> Unit,
    onToggleKudos: () -> Unit,
    onCommentTextChange: (String) -> Unit,
    onSendComment: () -> Unit
) {
    val activity = uiState.activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111318))
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
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 104.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    val gpsPoints = remember(activity.polyline) {
                        PolylineDecoder.decode(activity.polyline)
                    }
                    PlatformMapView(
                        gpsPoints = gpsPoints,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
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
                        color = Color(0xFFF0F0F0),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    ActivityStatsGrid(
                        distanceKm = Formatters.metersToKmString(activity.distanceM),
                        duration = Formatters.formatDuration(activity.durationSec),
                        pace = activity.avgPace?.let { Formatters.formatPace(it) } ?: "—",
                        elevation = "+${activity.elevationM.toInt()}",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFF252830)
                    )
                }

                item {
                    KudosRow(
                        kudosCount = uiState.kudosCount,
                        hasKudosed = uiState.hasKudosed,
                        onToggleKudos = onToggleKudos,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFF252830)
                    )
                }

                item {
                    Text(
                        text = "Comments",
                        color = Color(0xFFF0F0F0),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(uiState.comments) { comment ->
                    CommentCard(
                        comment = comment,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                if (uiState.comments.isEmpty()) {
                    item {
                        Text(
                            text = "No comments yet. Be the first!",
                            color = Color(0xFF9BA3B2),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
                    .background(Color(0xF2111318))
            )
        }
    }
}

@Composable
private fun ActivityTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xE6111318))
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 4.dp),
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
            color = Color(0xFFF0F0F0),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(40.dp))
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
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF382620), CircleShape)
                .border(2.dp, Color(0xFF252830), CircleShape),
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
                color = Color(0xFFF0F0F0),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = timeAgo,
                color = Color(0xFF9BA3B2),
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
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCell(modifier = Modifier.weight(1f), value = distanceKm, unit = "km", label = "Distance", valueFontSize = 32)
            StatCell(modifier = Modifier.weight(1f), value = pace, unit = "/km", label = "Avg Pace", valueFontSize = 32)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            .background(Color(0xFF1F0F0B), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = Color(0xFFF0F0F0),
                    fontSize = valueFontSize.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                if (unit != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        color = Color(0xFF9BA3B2),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                color = Color(0xFF9BA3B2),
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
            color = Color(0xFF9BA3B2),
            fontSize = 14.sp
        )

        Button(
            onClick = onToggleKudos,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasKudosed) Color(0xFFFC4C02) else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (hasKudosed) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
            modifier = Modifier
                .height(44.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x66FF571B),
                    spotColor = Color(0x66FF571B)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
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
            .background(Color(0xFF1F0F0B), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF382620), CircleShape),
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = comment.displayName,
                    color = Color(0xFFF0F0F0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = Formatters.timeAgo(comment.createdAt),
                    color = Color(0xFF9BA3B2),
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.text,
                color = Color(0xFF9BA3B2),
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
    Box(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF252830), CircleShape)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = Color(0xFFF0F0F0),
                    fontSize = 14.sp
                ),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp, end = 52.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(
                                    text = "Add a comment...",
                                    color = Color(0xFF9BA3B2),
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
                    .padding(end = 4.dp)
            )
        }
    }
}
