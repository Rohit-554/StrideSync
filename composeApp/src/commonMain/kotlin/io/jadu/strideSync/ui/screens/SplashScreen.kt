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
import io.jadu.strideSync.ui.theme.Spacing
import io.jadu.strideSync.ui.theme.StrideColors
import kotlinx.coroutines.delay

private const val SPLASH_GRID_LINE = 0x08FFFFFF

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
            .background(StrideColors.Background)
    ) {
        SplashGridOverlay()
        SplashGlowOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = Spacing.d80),
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
                            modifier = Modifier.size(Spacing.d48)
                        )
                        Spacer(modifier = Modifier.height(Spacing.lg))
                        Text(
                            text = "StrideSync",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = "Every stride counts.",
                            color = StrideColors.TextSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = bottomVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 1000)) +
                            slideInVertically(animationSpec = tween(durationMillis = 1000)) { it / 5 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Spacing.xxxl + Spacing.lg)
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

@Composable
private fun SplashGridOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridStep = Spacing.d50.toPx()
        val lineColor = Color(SPLASH_GRID_LINE)
        val strokePx = Spacing.d1.toPx()
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
}

@Composable
private fun SplashGlowOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    StrideColors.BrandPrimarySoft,
                    StrideColors.BrandPrimaryOverlay,
                    Color.Transparent
                ),
                center = Offset(size.width / 2f, size.height * 1.2f),
                radius = size.width * 1.5f
            )
        )
    }
}
