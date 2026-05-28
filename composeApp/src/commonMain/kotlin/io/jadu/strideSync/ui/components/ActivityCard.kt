package io.jadu.strideSync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.jadu.strideSync.domain.model.FeedItem
import io.jadu.strideSync.domain.model.GpsPoint
import io.jadu.strideSync.domain.model.SportType
import io.jadu.strideSync.ui.theme.CardSurface
import io.jadu.strideSync.ui.theme.SurfaceAlt
import io.jadu.strideSync.ui.theme.TextPrimary
import io.jadu.strideSync.ui.theme.TextSecondary
import io.jadu.strideSync.utils.Formatters
import io.jadu.strideSync.utils.PolylineDecoder

@Composable
fun ActivityCard(
    feedItem: FeedItem,
    onKudosToggle: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gpsPoints = remember(feedItem.activity.polyline) {
        PolylineDecoder.decode(feedItem.activity.polyline)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        onClick = onCardClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            ActivityCardHeader(
                title = feedItem.activity.title,
                userName = feedItem.user.displayName,
                sportType = feedItem.activity.sportType,
                timeAgo = Formatters.timeAgo(feedItem.activity.startedAt),
                avatarUrl = feedItem.user.avatarUrl,
                modifier = Modifier.padding(16.dp)
            )

            RoutePreview(
                gpsPoints = gpsPoints,
                accentColor = when (feedItem.activity.sportType) {
                    SportType.Ride -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.primaryContainer
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            ActivityStatsSection(
                distance = Formatters.metersToKmString(feedItem.activity.distanceM),
                duration = Formatters.formatDuration(feedItem.activity.durationSec),
                thirdValue = thirdMetricValue(feedItem),
                thirdLabel = thirdMetricLabel(feedItem.activity.sportType),
                sportType = feedItem.activity.sportType
            )

            HorizontalDivider(color = SurfaceAlt)

            ActivityEngagementRow(
                kudosCount = feedItem.kudosCount,
                commentsCount = feedItem.commentCount,
                hasKudos = feedItem.hasKudosed,
                onKudosToggle = onKudosToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceAlt.copy(alpha = 0.28f))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun ActivityCardHeader(
    title: String,
    userName: String,
    sportType: SportType,
    timeAgo: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AthleteAvatar(
                userName = userName,
                avatarUrl = avatarUrl
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = userName,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = timeAgo,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        SportTypePill(sportType = sportType)
    }
}

@Composable
private fun AthleteAvatar(
    userName: String,
    avatarUrl: String?
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(SurfaceAlt)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl.isNullOrBlank()) {
            Text(
                text = userName.take(2).uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            AsyncImage(
                model = avatarUrl,
                contentDescription = userName,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SportTypePill(
    sportType: SportType
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(SurfaceAlt)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SportTypeIcon(
            sportType = sportType,
            tint = when (sportType) {
                SportType.Ride -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
        Text(
            text = sportLabel(sportType),
            color = TextPrimary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun RoutePreview(
    gpsPoints: List<GpsPoint>,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0A0C0F))
    ) {
        PreviewGrid(modifier = Modifier.fillMaxSize())
        if (gpsPoints.isNotEmpty()) {
            RouteLinePreview(
                gpsPoints = gpsPoints,
                accentColor = accentColor,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LoadingShimmer(modifier = Modifier.fillMaxSize())
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.12f))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, CardSurface.copy(alpha = 0.92f))
                    )
                )
        )
    }
}

@Composable
private fun PreviewGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val gridColor = Color(0xFF4D4F57).copy(alpha = 0.22f)
        val step = size.width / 14f
        var x = 0f
        while (x <= size.width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
            x += step
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            y += step
        }
    }
}

@Composable
private fun RouteLinePreview(
    gpsPoints: List<GpsPoint>,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (gpsPoints.size < 2) return@Canvas

        val minLat = gpsPoints.minOf { it.lat }
        val maxLat = gpsPoints.maxOf { it.lat }
        val minLng = gpsPoints.minOf { it.lng }
        val maxLng = gpsPoints.maxOf { it.lng }

        val latRange = (maxLat - minLat).takeIf { it > 0.0 } ?: 0.001
        val lngRange = (maxLng - minLng).takeIf { it > 0.0 } ?: 0.001
        val horizontalPadding = size.width * 0.08f
        val verticalPadding = size.height * 0.12f

        val path = Path()
        gpsPoints.forEachIndexed { index, point ->
            val x = ((point.lng - minLng) / lngRange).toFloat() * (size.width - horizontalPadding * 2) + horizontalPadding
            val y = size.height - (((point.lat - minLat) / latRange).toFloat() * (size.height - verticalPadding * 2) + verticalPadding)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = accentColor.copy(alpha = 0.25f),
            style = Stroke(width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        drawPath(
            path = path,
            color = accentColor,
            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun LoadingShimmer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFBFC3CA).copy(alpha = 0.18f),
                    Color(0xFFBFC3CA).copy(alpha = 0.10f),
                    Color.Transparent
                )
            )
        )
    )
}

@Composable
private fun ActivityStatsSection(
    distance: String,
    duration: String,
    thirdValue: String,
    thirdLabel: String,
    sportType: SportType
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatBlock(value = distance, label = "Distance")
        StatBlock(value = duration, label = "Time")
        StatBlock(
            value = thirdValue,
            label = thirdLabel,
            accent = if (sportType == SportType.Ride) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun StatBlock(
    value: String,
    label: String,
    accent: Color = TextPrimary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = accent,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun ActivityEngagementRow(
    kudosCount: Int,
    commentsCount: Int,
    hasKudos: Boolean,
    onKudosToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KudosButton(
                kudosCount = kudosCount,
                hasKudos = hasKudos,
                onKudosToggle = onKudosToggle
            )

            Spacer(modifier = Modifier.width(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = "Comments",
                    modifier = Modifier.size(20.dp),
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$commentsCount",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(SurfaceAlt)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun sportLabel(sportType: SportType): String = when (sportType) {
    SportType.Run -> "Morning Run"
    SportType.Ride -> "Ride"
    SportType.Hike -> "Hike"
    SportType.Swim -> "Swim"
    SportType.Walk -> "Walk"
    SportType.Other -> "Workout"
}

private fun thirdMetricLabel(sportType: SportType): String = when (sportType) {
    SportType.Ride,
    SportType.Hike -> "Elev"
    else -> "Pace"
}

private fun thirdMetricValue(feedItem: FeedItem): String = when (feedItem.activity.sportType) {
    SportType.Ride,
    SportType.Hike -> "${feedItem.activity.elevationM.toInt()} m"
    else -> feedItem.activity.avgPace?.let { Formatters.formatPace(it) } ?: "—"
}
