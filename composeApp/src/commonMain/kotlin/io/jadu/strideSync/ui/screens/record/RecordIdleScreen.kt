package io.jadu.strideSync.ui.screens.record

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.domain.model.SportType
import io.jadu.strideSync.ui.components.StrideChip
import io.jadu.strideSync.ui.theme.Background
import io.jadu.strideSync.ui.theme.Success
import io.jadu.strideSync.ui.theme.TertiaryContainer
import io.jadu.strideSync.ui.theme.StrideColors

@Composable
fun RecordIdleScreen(
    onStartRecording: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    var selectedSport by remember { mutableStateOf(SportType.Run) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        RecordTopBar(onNavigateBack = onNavigateBack)

        SportTypeSelector(
            selectedSport = selectedSport,
            onSportSelected = { selectedSport = it }
        )

        MapPlaceholder(modifier = Modifier.fillMaxWidth().weight(1f))

        BottomPanel(
            onStartRecording = onStartRecording,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        )
    }
}

@Composable
private fun RecordTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xs, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = "Record",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = StrideColors.TextSecondary
            )
        }
    }
}

@Composable
private fun SportTypeSelector(
    selectedSport: SportType,
    onSportSelected: (SportType) -> Unit
) {
    val sports: List<Pair<SportType, ImageVector>> = listOf(
        SportType.Run to Icons.Default.DirectionsRun,
        SportType.Ride to Icons.Default.DirectionsBike,
        SportType.Hike to Icons.Default.Terrain,
        SportType.Walk to Icons.Default.DirectionsWalk
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        sports.forEach { (sport, icon) ->
            StrideChip(
                text = sport.name,
                icon = icon,
                isSelected = selectedSport == sport,
                onClick = { onSportSelected(sport) }
            )
        }
    }
}

@Composable
private fun MapPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(StrideColors.BackgroundElevated)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = StrideColors.SurfaceAlt
            val gridSpacing = Spacing.d60.toPx()

            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, start = androidx.compose.ui.geometry.Offset(x, 0f), end = androidx.compose.ui.geometry.Offset(x, size.height), strokeWidth = 1f)
                x += gridSpacing
            }
            var y = 0f
            while (y < size.height) {
                drawLine(gridColor, start = androidx.compose.ui.geometry.Offset(0f, y), end = androidx.compose.ui.geometry.Offset(size.width, y), strokeWidth = 1f)
                y += gridSpacing
            }

            drawLine(color = StrideColors.SurfaceDivider, start = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.3f), end = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.3f), strokeWidth = 8f, pathEffect = PathEffect.cornerPathEffect(12f))
            drawLine(color = StrideColors.SurfaceDivider, start = androidx.compose.ui.geometry.Offset(size.width * 0.5f, 0f), end = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height), strokeWidth = 8f)
            drawLine(color = StrideColors.SurfaceDivider, start = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.6f), end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.6f), strokeWidth = 8f)
        }

        GpsStatusBadge(modifier = Modifier.align(Alignment.TopEnd).padding(Spacing.md))

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(Spacing.lg)
                .shadow(Spacing.sm, CircleShape, ambientColor = TertiaryContainer, spotColor = TertiaryContainer)
                .background(TertiaryContainer, CircleShape)
                .border(Spacing.d3, StrideColors.White.copy(alpha = 0.9f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(Spacing.d48)
                .border(Spacing.d1, TertiaryContainer.copy(alpha = 0.3f), CircleShape)
        )
    }
}

@Composable
private fun GpsStatusBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(StrideColors.Surface.copy(alpha = 0.9f), RoundedCornerShape(Spacing.xl))
            .border(Spacing.d1, Success.copy(alpha = 0.4f), RoundedCornerShape(Spacing.xl))
            .padding(horizontal = Spacing.d10, vertical = Spacing.d6)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.d6),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(Spacing.sm).background(Success, CircleShape))
            Text(text = "GPS Ready", color = Success, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun BottomPanel(
    onStartRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Background)
            .padding(horizontal = Spacing.xxl)
            .padding(top = Spacing.xl, bottom = Spacing.xxxl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxl)
    ) {
        StatsRow()
        StartButton(onClick = onStartRecording)
    }
}

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(label = "DISTANCE", value = "0.00", unit = "km")
        HorizontalDivider(modifier = Modifier.size(width = Spacing.d1, height = Spacing.d40).background(StrideColors.SurfaceDivider), color = StrideColors.SurfaceDivider)
        StatItem(label = "TIME", value = "00:00", unit = "")
        HorizontalDivider(modifier = Modifier.size(width = Spacing.d1, height = Spacing.d40).background(StrideColors.SurfaceDivider), color = StrideColors.SurfaceDivider)
        StatItem(label = "PACE", value = "0:00", unit = "/km")
    }
}

@Composable
private fun StatItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
            if (unit.isNotEmpty()) {
                Text(text = " $unit", color = StrideColors.TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(bottom = Spacing.d3))
            }
        }
        Text(text = label, color = StrideColors.TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun StartButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.d10)
    ) {
        Button(
            onClick = onClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = StrideColors.White),
            modifier = Modifier.size(Spacing.d80).shadow(elevation = Spacing.lg, shape = CircleShape, ambientColor = StrideColors.BrandPrimary, spotColor = StrideColors.BrandPrimary)
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start recording", modifier = Modifier.size(Spacing.d40))
        }
        Text(text = "Tap to start recording", color = StrideColors.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Normal, style = MaterialTheme.typography.bodySmall)
    }
}
