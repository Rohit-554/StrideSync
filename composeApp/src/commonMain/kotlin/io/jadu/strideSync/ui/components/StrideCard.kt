package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.theme.StrideColors

@Composable
fun ActivityFeedCard(
    title: String,
    location: String,
    timeAgo: String,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Spacing.md),
        colors = CardDefaults.cardColors(containerColor = StrideColors.Surface)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            ActivityCardHeader(
                title = title,
                location = location,
                timeAgo = timeAgo,
                onMoreClick = onMoreClick
            )
            RouteMapPreview()
        }
    }
}

@Composable
private fun ActivityCardHeader(
    title: String,
    location: String,
    timeAgo: String,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        AthleteInfo(title = title, location = location, timeAgo = timeAgo)
        IconButton(onClick = onMoreClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = StrideColors.TextSecondary
            )
        }
    }
}

@Composable
private fun AthleteInfo(title: String, location: String, timeAgo: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InitialsAvatar(initials = title.take(2).uppercase())
        Column {
            Text(
                text = title,
                color = StrideColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$location • $timeAgo",
                color = StrideColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun InitialsAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(Spacing.d48)
            .border(Spacing.d1, MaterialTheme.colorScheme.outline, CircleShape)
            .padding(Spacing.xxs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RouteMapPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Spacing.d160)
            .border(Spacing.d1, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(Spacing.sm))
            .padding(Spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = demoRoutePath()
            drawPath(
                path = path,
                color = StrideColors.BrandPrimary,
                style = Stroke(width = Spacing.xs.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.demoRoutePath(): Path {
    return Path().apply {
        moveTo(0f, size.height * 0.8f)
        quadraticTo(size.width * 0.125f, size.height * 0.2f, size.width * 0.25f, size.height * 0.7f)
        quadraticTo(size.width * 0.375f, size.height * 0.3f, size.width * 0.5f, size.height * 0.4f)
        quadraticTo(size.width * 0.625f, size.height * 0.6f, size.width * 0.75f, size.height * 0.9f)
        quadraticTo(size.width * 0.875f, size.height * 0.4f, size.width, size.height * 0.3f)
    }
}

@Composable
fun StatGridCard(
    bpm: String,
    kmh: String,
    elevGain: String,
    distance: String,
    modifier: Modifier = Modifier,
    isLive: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Spacing.md),
        colors = CardDefaults.cardColors(containerColor = StrideColors.SurfaceAlt),
        border = BorderStroke(Spacing.xxs, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxl)
        ) {
            StatGridHeader(isLive = isLive)
            StatGridBody(bpm = bpm, kmh = kmh, elevGain = elevGain, distance = distance)
        }
    }
}

@Composable
private fun StatGridHeader(isLive: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "SUMMARY",
            color = StrideColors.TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        if (isLive) {
            LiveBadge()
        }
    }
}

@Composable
private fun LiveBadge() {
    Box(
        modifier = Modifier
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
    ) {
        Text(
            text = "LIVE",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatGridBody(bpm: String, kmh: String, elevGain: String, distance: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxl)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            StatGridCell(value = bpm, label = "BPM", modifier = Modifier.weight(1f))
            StatGridCell(value = kmh, label = "KM/H", modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            StatGridCell(value = elevGain, label = "ELEV GAIN", modifier = Modifier.weight(1f))
            StatGridCell(value = distance, label = "KM", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatGridCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = StrideColors.TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
        )
        Text(
            text = label,
            color = StrideColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
