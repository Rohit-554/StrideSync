package io.jadu.strideSync.ui.screens.record

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
            .padding(horizontal = 4.dp, vertical = 8.dp),
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
                tint = Color(0xFF9BA3B2)
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
        modifier = modifier.background(Color(0xFF1A1D24))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color(0xFF252A34)
            val gridSpacing = 60.dp.toPx()

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

            drawLine(color = Color(0xFF2E3340), start = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.3f), end = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.3f), strokeWidth = 8f, pathEffect = PathEffect.cornerPathEffect(12f))
            drawLine(color = Color(0xFF2E3340), start = androidx.compose.ui.geometry.Offset(size.width * 0.5f, 0f), end = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height), strokeWidth = 8f)
            drawLine(color = Color(0xFF2E3340), start = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.6f), end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.6f), strokeWidth = 8f)
        }

        GpsStatusBadge(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp))

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(16.dp)
                .shadow(8.dp, CircleShape, ambientColor = Color(0xFF2692FF), spotColor = Color(0xFF2692FF))
                .background(Color(0xFF2692FF), CircleShape)
                .border(3.dp, Color.White.copy(alpha = 0.9f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp)
                .border(1.dp, Color(0xFF2692FF).copy(alpha = 0.3f), CircleShape)
        )
    }
}

@Composable
private fun GpsStatusBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF1F2530).copy(alpha = 0.9f), RoundedCornerShape(20.dp))
            .border(1.dp, Success.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).background(Success, CircleShape))
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
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
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
        HorizontalDivider(modifier = Modifier.size(width = 1.dp, height = 40.dp).background(Color(0xFF44302A)), color = Color(0xFF44302A))
        StatItem(label = "TIME", value = "00:00", unit = "")
        HorizontalDivider(modifier = Modifier.size(width = 1.dp, height = 40.dp).background(Color(0xFF44302A)), color = Color(0xFF44302A))
        StatItem(label = "PACE", value = "0:00", unit = "/km")
    }
}

@Composable
private fun StatItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
            if (unit.isNotEmpty()) {
                Text(text = " $unit", color = Color(0xFF9BA3B2), fontSize = 14.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
        Text(text = label, color = Color(0xFF9BA3B2), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun StartButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = Color.White),
            modifier = Modifier.size(80.dp).shadow(elevation = 16.dp, shape = CircleShape, ambientColor = Color(0xFFFF571B), spotColor = Color(0xFFFF571B))
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start recording", modifier = Modifier.size(40.dp))
        }
        Text(text = "Tap to start recording", color = Color(0xFF9BA3B2), fontSize = 13.sp, fontWeight = FontWeight.Normal, style = MaterialTheme.typography.bodySmall)
    }
}
