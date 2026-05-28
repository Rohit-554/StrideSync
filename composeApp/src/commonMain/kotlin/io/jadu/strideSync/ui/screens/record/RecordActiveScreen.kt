package io.jadu.strideSync.ui.screens.record

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.components.StrideBottomNavigation

@Composable
fun RecordActiveScreen(
    distanceMi: String = "5.24",
    duration: String = "24:18",
    pace: String = "5:48",
    elevGainM: String = "+112",
    heartRateBpm: String = "142",
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onTabSelected: (String) -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFF111318),
        bottomBar = {
            StrideBottomNavigation(
                selectedTab = "record",
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Top status row: GPS indicator + elapsed label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GpsActiveIndicator()
                Text(
                    text = "RECORDING",
                    color = Color(0xFF9BA3B2),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Primary stat: distance
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = distanceMi,
                    color = Color(0xFFF0F0F0),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-2).sp
                )
                Text(
                    text = "MILES",
                    color = Color(0xFF9BA3B2),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Map with live route
            RouteMapPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            // Stats grid (duration, pace, elev, hr)
            LiveStatsGrid(
                duration = duration,
                pace = pace,
                elevGainM = elevGainM,
                heartRateBpm = heartRateBpm
            )

            Spacer(modifier = Modifier.weight(1f))

            // Pause + Stop controls
            RecordingControls(
                onPause = onPause,
                onStop = onStop,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun GpsActiveIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color(0xFFFC4C02), spotColor = Color(0xFFFC4C02))
                .background(Color(0xFFFC4C02), shape = CircleShape)
        )
        Text(
            text = "GPS ACTIVE",
            color = Color(0xFFFC4C02),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun RouteMapPreview(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1D24))
            .border(1.dp, Color(0xFF2C1B16), RoundedCornerShape(16.dp))
    ) {
        // Dark map grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color(0xFF252830)
            val gridStep = 40.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += gridStep
            }
            var y = 0f
            while (y < size.height) {
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                y += gridStep
            }

            // Orange route polyline
            val routePath = Path().apply {
                moveTo(size.width * 0.05f, size.height * 0.75f)
                cubicTo(
                    size.width * 0.15f, size.height * 0.20f,
                    size.width * 0.25f, size.height * 0.60f,
                    size.width * 0.35f, size.height * 0.35f
                )
                cubicTo(
                    size.width * 0.45f, size.height * 0.10f,
                    size.width * 0.55f, size.height * 0.50f,
                    size.width * 0.65f, size.height * 0.65f
                )
                cubicTo(
                    size.width * 0.75f, size.height * 0.80f,
                    size.width * 0.82f, size.height * 0.30f,
                    size.width * 0.92f, size.height * 0.25f
                )
            }
            drawPath(
                path = routePath,
                color = Color(0xFFFF571B),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Current position dot
            drawCircle(
                color = Color(0xFFFC4C02),
                radius = 8.dp.toPx(),
                center = Offset(size.width * 0.92f, size.height * 0.25f)
            )
            drawCircle(
                color = Color(0x55FC4C02),
                radius = 18.dp.toPx(),
                center = Offset(size.width * 0.92f, size.height * 0.25f)
            )
        }
    }
}

@Composable
private fun LiveStatsGrid(
    duration: String,
    pace: String,
    elevGainM: String,
    heartRateBpm: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F0F0B), shape = RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(value = duration, label = "DURATION")
        StatDivider()
        StatItem(value = pace, label = "PACE /MI")
        StatDivider()
        StatItem(value = elevGainM, label = "ELEV (M)")
        StatDivider()
        StatItem(value = heartRateBpm, label = "BPM")
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color(0xFFF0F0F0),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = Color(0xFF9BA3B2),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(Color(0xFF2C1B16))
    )
}

@Composable
private fun RecordingControls(
    onPause: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pause button
        Button(
            onClick = onPause,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF252830),
                contentColor = Color(0xFFF0F0F0)
            ),
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PAUSE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Stop button
        Button(
            onClick = onStop,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF571B),
                contentColor = Color.White
            ),
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFFFF571B),
                    spotColor = Color(0xFFFF571B)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "STOP",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
