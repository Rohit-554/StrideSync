package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.theme.StrideColors

private val StatBackground = StrideColors.BackgroundElevated
private val StatValueColor = StrideColors.TextPrimary
private val StatLabelColor = StrideColors.TextSecondary

@Composable
fun StatRow(
    distanceKm: String,
    duration: String,
    pace: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(StatBackground)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatCell(value = distanceKm, label = "KM")
        StatCell(value = duration, label = "TIME")
        StatCell(value = pace, label = "PACE")
    }
}

@Composable
private fun StatCell(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = StatValueColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = StatLabelColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
