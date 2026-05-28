package io.jadu.strideSync.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.components.StridePrimaryButton
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit = {}
) {
    var centerVisible by remember { mutableStateOf(false) }
    var bottomVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        centerVisible = true
        delay(400)
        bottomVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111318))
    ) {
        // Subtle grid texture overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridStep = 50.dp.toPx()
            val lineColor = Color(0x08FFFFFF)
            val strokePx = 1.dp.toPx()
            val cols = (size.width / gridStep).toInt() + 1
            val rows = (size.height / gridStep).toInt() + 1
            for (i in 0..cols) {
                drawLine(
                    color = lineColor,
                    start = Offset(i * gridStep, 0f),
                    end = Offset(i * gridStep, size.height),
                    strokeWidth = strokePx
                )
            }
            for (i in 0..rows) {
                drawLine(
                    color = lineColor,
                    start = Offset(0f, i * gridStep),
                    end = Offset(size.width, i * gridStep),
                    strokeWidth = strokePx
                )
            }
        }

        // Orange radial glow rising from below the bottom edge
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x66FF571B), // 40% opacity orange
                        Color(0x1AFF571B), // 10% opacity orange
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2f, size.height * 1.2f),
                    radius = size.width * 1.5f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero block — vertically centered with top padding
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = centerVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 1000)) +
                            slideInVertically(animationSpec = tween(durationMillis = 1000)) { it / 5 }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "StrideSync",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Every stride counts.",
                            color = Color(0xFF9BA3B2),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Bottom CTA
            Box(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = bottomVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 1000)) +
                            slideInVertically(animationSpec = tween(durationMillis = 1000)) { it / 5 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 48.dp)
                    ) {
                        StridePrimaryButton(
                            text = "Get Started",
                            onClick = onGetStarted
                        )
                    }
                }
            }
        }
    }
}
