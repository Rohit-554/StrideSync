package io.jadu.strideSync.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert

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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F0F0B) // surface container
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: User details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fallback avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(0.dp, Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title.take(2).uppercase(),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column {
                        Text(
                            text = title,
                            color = Color(0xFFF0F0F0),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$location • $timeAgo",
                            color = Color(0xFF9BA3B2),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                IconButton(onClick = onMoreClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color(0xFF9BA3B2)
                    )
                }
            }

            // Map polyline drawing preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.8f)
                        quadraticTo(size.width * 0.125f, size.height * 0.2f, size.width * 0.25f, size.height * 0.7f)
                        quadraticTo(size.width * 0.375f, size.height * 0.3f, size.width * 0.5f, size.height * 0.4f)
                        quadraticTo(size.width * 0.625f, size.height * 0.6f, size.width * 0.75f, size.height * 0.9f)
                        quadraticTo(size.width * 0.875f, size.height * 0.4f, size.width, size.height * 0.3f)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFFFF571B), // Orange route color
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF252830) // surface-alt
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header row: SUMMARY + LIVE badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SUMMARY",
                    color = Color(0xFF9BA3B2),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (isLive) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.Transparent, RoundedCornerShape(4.dp))
                            .border(0.dp, Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .border(0.dp, Color.Transparent)
                            .border(0.dp, Color.Transparent)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .border(0.dp, Color.Transparent)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Stats grid layout (2 columns x 2 rows)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = bpm,
                            color = Color(0xFFF0F0F0),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
                        )
                        Text(
                            text = "BPM",
                            color = Color(0xFF9BA3B2),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = kmh,
                            color = Color(0xFFF0F0F0),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
                        )
                        Text(
                            text = "KM/H",
                            color = Color(0xFF9BA3B2),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = elevGain,
                            color = Color(0xFFF0F0F0),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
                        )
                        Text(
                            text = "ELEV GAIN",
                            color = Color(0xFF9BA3B2),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = distance,
                            color = Color(0xFFF0F0F0),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
                        )
                        Text(
                            text = "KM",
                            color = Color(0xFF9BA3B2),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
