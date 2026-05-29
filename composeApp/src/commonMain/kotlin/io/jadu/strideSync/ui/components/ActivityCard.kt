package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

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
import androidx.compose.ui.graphics.Brush
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
import io.jadu.strideSync.ui.theme.StrideColors
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
        shape = RoundedCornerShape(Spacing.md),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        onClick = onCardClick
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.d0)) {
            ActivityCardHeader(
                title = feedItem.activity.title,
                userName = feedItem.user.displayName,
                sportType = feedItem.activity.sportType,
                timeAgo = Formatters.timeAgo(feedItem.activity.startedAt),
                avatarUrl = feedItem.user.avatarUrl,
                modifier = Modifier.padding(Spacing.lg)
            )

            RoutePreview(
                gpsPoints = gpsPoints,
                accentColor = sportAccentColor(feedItem.activity.sportType),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Spacing.d180)
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
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md)
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
        AthleteInfoRow(
            userName = userName,
            timeAgo = timeAgo,
            title = title,
            avatarUrl = avatarUrl,
            modifier = Modifier.weight(1f)
        )

        SportTypePill(sportType = sportType)
    }
}

@Composable
private fun AthleteInfoRow(
    userName: String,
    timeAgo: String,
    title: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AthleteAvatar(userName = userName, avatarUrl = avatarUrl)

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
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
}

@Composable
private fun AthleteAvatar(userName: String, avatarUrl: String?) {
    Box(
        modifier = Modifier
            .size(Spacing.d40)
            .clip(CircleShape)
            .background(SurfaceAlt)
            .border(Spacing.d1, MaterialTheme.colorScheme.outline, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl.isNullOrBlank()) {
            InitialsFallback(name = userName)
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
private fun InitialsFallback(name: String) {
    Text(
        text = name.take(2).uppercase(),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SportTypePill(sportType: SportType) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Spacing.d999))
            .background(SurfaceAlt)
            .padding(horizontal = Spacing.d10, vertical = Spacing.d6),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SportTypeIcon(
            sportType = sportType,
            tint = sportAccentColor(sportType)
        )
        Text(
            text = sportLabel(sportType),
            color = TextPrimary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun sportAccentColor(sportType: SportType): Color = when (sportType) {
    SportType.Ride -> MaterialTheme.colorScheme.tertiaryContainer
    else -> MaterialTheme.colorScheme.primaryContainer
}

@Composable
private fun RoutePreview(
    gpsPoints: List<GpsPoint>,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(StrideColors.Black.copy(alpha = 0.96f))
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

        VignetteOverlay(modifier = Modifier.fillMaxSize())
        BottomFadeOverlay(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun PreviewGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val gridColor = StrideColors.TextSecondary.copy(alpha = 0.22f)
        val step = size.width / 14f

        drawVerticalGridLines(gridColor, step)
        drawHorizontalGridLines(gridColor, step)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawVerticalGridLines(color: Color, step: Float) {
    var x = 0f
    while (x <= size.width) {
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f
        )
        x += step
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHorizontalGridLines(color: Color, step: Float) {
    var y = 0f
    while (y <= size.height) {
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
        y += step
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

        val path = gpsPath(gpsPoints)

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

private fun androidx.compose.ui.graphics.drawscope.DrawScope.gpsPath(gpsPoints: List<GpsPoint>): Path {
    val bounds = GpsBounds.from(gpsPoints)
    val horizontalPadding = size.width * 0.08f
    val verticalPadding = size.height * 0.12f

    return Path().apply {
        gpsPoints.forEachIndexed { index, point ->
            val x = bounds.normalizeLongitude(point.lng, size.width, horizontalPadding)
            val y = bounds.normalizeLatitude(point.lat, size.height, verticalPadding)
            if (index == 0) moveTo(x, y) else lineTo(x, y)
        }
    }
}

private data class GpsBounds(
    val minLat: Double,
    val maxLat: Double,
    val minLng: Double,
    val maxLng: Double
) {
    val latRange = (maxLat - minLat).takeIf { it > 0.0 } ?: 0.001
    val lngRange = (maxLng - minLng).takeIf { it > 0.0 } ?: 0.001

    fun normalizeLongitude(lng: Double, width: Float, padding: Float): Float {
        return ((lng - minLng) / lngRange).toFloat() * (width - padding * 2) + padding
    }

    fun normalizeLatitude(lat: Double, height: Float, padding: Float): Float {
        return height - (((lat - minLat) / latRange).toFloat() * (height - padding * 2) + padding)
    }

    companion object {
        fun from(points: List<GpsPoint>): GpsBounds {
            return GpsBounds(
                minLat = points.minOf { it.lat },
                maxLat = points.maxOf { it.lat },
                minLng = points.minOf { it.lng },
                maxLng = points.maxOf { it.lng }
            )
        }
    }
}

@Composable
private fun LoadingShimmer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    StrideColors.TextPrimary.copy(alpha = 0.18f),
                    StrideColors.TextPrimary.copy(alpha = 0.10f),
                    Color.Transparent
                )
            )
        )
    )
}

@Composable
private fun VignetteOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color.Black.copy(alpha = 0.12f))
    )
}

@Composable
private fun BottomFadeOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(Spacing.d72)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, CardSurface.copy(alpha = 0.92f))
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
            .padding(horizontal = Spacing.md, vertical = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatBlock(value = distance, label = "Distance")
        StatBlock(value = duration, label = "Time")
        StatBlock(
            value = thirdValue,
            label = thirdLabel,
            accent = sportAccentColor(sportType)
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
        verticalArrangement = Arrangement.spacedBy(Spacing.d6)
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
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KudosButton(
                kudosCount = kudosCount,
                hasKudos = hasKudos,
                onKudosToggle = onKudosToggle
            )

            Spacer(modifier = Modifier.width(Spacing.lg))
            CommentCount(count = commentsCount)
        }

        ShareButton()
    }
}

@Composable
private fun CommentCount(count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.ChatBubble,
            contentDescription = "Comments",
            modifier = Modifier.size(Spacing.xl),
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = "$count",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ShareButton() {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(SurfaceAlt)
            .padding(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = TextSecondary,
            modifier = Modifier.size(Spacing.d18)
        )
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
    SportType.Ride, SportType.Hike -> "Elev"
    else -> "Pace"
}

private fun thirdMetricValue(feedItem: FeedItem): String = when (feedItem.activity.sportType) {
    SportType.Ride, SportType.Hike -> "${feedItem.activity.elevationM.toInt()} m"
    else -> feedItem.activity.avgPace?.let { Formatters.formatPace(it) } ?: "—"
}
