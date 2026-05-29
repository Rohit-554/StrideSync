package io.jadu.strideSync.ui.screens.record

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import io.jadu.strideSync.ui.components.StridePrimaryButton
import io.jadu.strideSync.ui.components.StrideSecondaryButton
import io.jadu.strideSync.ui.components.StrideFloatingTextField
import io.jadu.strideSync.ui.components.StrideToast
import io.jadu.strideSync.ui.theme.Background
import io.jadu.strideSync.ui.theme.Primary
import io.jadu.strideSync.ui.theme.PrimaryContainer
import io.jadu.strideSync.ui.theme.Success
import io.jadu.strideSync.ui.theme.StrideColors
import io.jadu.strideSync.ui.theme.TertiaryContainer

@Composable
fun ActivitySummaryScreen(
    distanceKm: String = "0.00",
    duration: String = "00:00",
    pace: String = "—",
    isSaving: Boolean = false,
    errorMessage: String? = null,
    onSave: (title: String) -> Unit,
    onDiscard: () -> Unit
) {
    var activityTitle by remember { mutableStateOf("") }
    var toastMessage by remember { mutableStateOf("") }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) toastMessage = errorMessage
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        ConfettiLayer()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg)
                .padding(top = Spacing.sm)
                .windowInsetsPadding(WindowInsets.navigationBars),
            verticalArrangement = Arrangement.spacedBy(Spacing.d0)
        ) {
            Spacer(Modifier.height(Spacing.lg))
            ActivityHeader()
            Spacer(Modifier.height(Spacing.lg))
            RouteMapPreview()
            Spacer(Modifier.height(Spacing.lg))
            StatsGrid(
                distanceKm = distanceKm,
                duration = duration,
                pace = pace
            )
            Spacer(Modifier.height(Spacing.xxl))
            StrideFloatingTextField(
                value = activityTitle,
                onValueChange = { activityTitle = it },
                label = "Title your activity"
            )
            Spacer(Modifier.height(Spacing.xxl))
            StridePrimaryButton(
                text = if (isSaving) "Saving…" else "Save Activity",
                onClick = { if (!isSaving) onSave(activityTitle) }
            )
            if (isSaving) {
                Spacer(Modifier.height(Spacing.md))
                CircularProgressIndicator(
                    color = Primary,
                    modifier = Modifier.size(Spacing.d28).align(Alignment.CenterHorizontally)
                )
            }
            Spacer(Modifier.height(Spacing.md))
            StrideSecondaryButton(text = "Discard", onClick = { if (!isSaving) onDiscard() })
            Spacer(Modifier.height(Spacing.xxxl))
        }

        if (toastMessage.isNotEmpty()) {
            StrideToast(
                message = toastMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = Spacing.lg),
                onDismiss = { toastMessage = "" }
            )
        }
    }
}

@Composable
private fun ConfettiLayer() {
    Box(modifier = Modifier.fillMaxWidth().height(Spacing.d80)) {
        Box(Modifier.padding(top = Spacing.xs).offset(x = Spacing.d80).size(Spacing.sm).background(PrimaryContainer.copy(alpha = 0.8f), CircleShape))
        Box(Modifier.padding(top = Spacing.d48, start = Spacing.d40).size(Spacing.md).background(TertiaryContainer.copy(alpha = 0.6f), CircleShape))
        Box(Modifier.align(Alignment.TopEnd).padding(top = Spacing.sm, end = Spacing.d80).size(Spacing.sm).background(PrimaryContainer.copy(alpha = 0.9f), CircleShape))
        Box(Modifier.align(Alignment.TopEnd).padding(top = Spacing.d64, end = Spacing.d48).size(Spacing.d6).background(Success.copy(alpha = 0.7f), CircleShape))
    }
}

@Composable
private fun ActivityHeader() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "MORNING RUN", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(Spacing.xs))
        Box(contentAlignment = Alignment.BottomCenter) {
            Text(text = "Activity Complete!", color = StrideColors.TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
            Box(modifier = Modifier.padding(top = Spacing.d44).width(Spacing.d48).height(Spacing.xs).background(Primary, CircleShape))
        }
    }
}

@Composable
private fun RouteMapPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Spacing.d240)
            .clip(RoundedCornerShape(Spacing.md))
            .background(StrideColors.SurfaceAlt)
            .border(Spacing.d1, StrideColors.SurfaceDivider, RoundedCornerShape(Spacing.md))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = StrideColors.SurfaceAlt
            var x = 0f
            while (x < size.width) { drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f); x += Spacing.d48.toPx() }
            var y = 0f
            while (y < size.height) { drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f); y += Spacing.d48.toPx() }

            val streetColor = StrideColors.SurfaceDivider
            drawLine(streetColor, Offset(0f, size.height * 0.45f), Offset(size.width, size.height * 0.45f), strokeWidth = 10f)
            drawLine(streetColor, Offset(size.width * 0.35f, 0f), Offset(size.width * 0.35f, size.height), strokeWidth = 10f)
            drawLine(streetColor, Offset(size.width * 0.7f, 0f), Offset(size.width * 0.7f, size.height), strokeWidth = 10f)
            drawLine(streetColor, Offset(0f, size.height * 0.75f), Offset(size.width, size.height * 0.75f), strokeWidth = 10f)

            val routePath = Path().apply {
                moveTo(size.width * 0.18f, size.height * 0.75f)
                quadraticTo(size.width * 0.18f, size.height * 0.45f, size.width * 0.35f, size.height * 0.45f)
                quadraticTo(size.width * 0.52f, size.height * 0.45f, size.width * 0.52f, size.height * 0.28f)
                quadraticTo(size.width * 0.52f, size.height * 0.15f, size.width * 0.7f, size.height * 0.15f)
                lineTo(size.width * 0.82f, size.height * 0.15f)
            }
            drawPath(routePath, StrideColors.BrandPrimary.copy(alpha = 0.25f), style = Stroke(width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(routePath, StrideColors.BrandPrimary, style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }

        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(colors = listOf(Color.Transparent, StrideColors.BrandPrimary.copy(alpha = 0.06f), Color.Transparent))))
        Box(modifier = Modifier.align(Alignment.TopStart).padding(start = Spacing.d68, top = Spacing.d148).size(Spacing.d14).shadow(Spacing.xs, CircleShape).background(StrideColors.White, CircleShape).border(Spacing.d3, StrideColors.Surface, CircleShape))
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Finish", tint = Primary, modifier = Modifier.align(Alignment.BottomEnd).padding(end = Spacing.d108, bottom = Spacing.d56).size(Spacing.d28))
    }
}

@Composable
private fun StatsGrid(
    distanceKm: String,
    duration: String,
    pace: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            StatCard(icon = Icons.Default.Route, value = distanceKm, label = "KM", modifier = Modifier.weight(1f))
            StatCard(icon = Icons.Default.Timer, value = duration, label = "DURATION", modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            BestPaceStatCard(pace = pace, modifier = Modifier.weight(1f))
            StatCard(icon = Icons.Default.Terrain, value = "0", label = "ELEV (M)", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier, highlighted: Boolean = false) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Spacing.md))
            .background(StrideColors.Surface)
            .then(if (highlighted) Modifier.border(Spacing.xxs, Primary.copy(alpha = 0.2f), RoundedCornerShape(Spacing.md)) else Modifier)
            .padding(vertical = Spacing.lg, horizontal = Spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Icon(imageVector = icon, contentDescription = label, tint = StrideColors.TextSecondary, modifier = Modifier.size(Spacing.xl))
            Text(text = value, color = StrideColors.TextPrimary, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp, style = MaterialTheme.typography.displaySmall)
            Text(text = label, color = StrideColors.TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun BestPaceStatCard(
    pace: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        StatCard(icon = Icons.Default.Speed, value = pace, label = "AVG PACE", highlighted = true, modifier = Modifier.fillMaxWidth())
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = Spacing.d14)
                .background(StrideColors.SurfaceAlt, RoundedCornerShape(Spacing.xl))
                .border(Spacing.d1, Primary, RoundedCornerShape(Spacing.xl))
                .padding(horizontal = Spacing.sm, vertical = Spacing.d3)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Primary, modifier = Modifier.size(Spacing.md))
                Text(text = "BEST PACE!", color = Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
        }
    }
}
