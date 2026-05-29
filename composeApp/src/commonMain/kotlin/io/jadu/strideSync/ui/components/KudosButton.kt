package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.theme.StrideColors

private val KudosActiveColor = StrideColors.BrandPrimaryStrong
private val KudosInactiveColor = StrideColors.TextSecondary

@Composable
fun KudosButton(
    kudosCount: Int,
    hasKudos: Boolean,
    onKudosToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heartColor by animateColorAsState(
        targetValue = if (hasKudos) KudosActiveColor else KudosInactiveColor,
        label = "heartColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (hasKudos) 1.2f else 1f,
        label = "heartScale"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeartIcon(
            hasKudos = hasKudos,
            color = heartColor,
            scale = scale,
            onClick = onKudosToggle
        )
        Spacer(modifier = Modifier.width(Spacing.xxs))
        KudosCount(count = kudosCount)
    }
}

@Composable
private fun HeartIcon(
    hasKudos: Boolean,
    color: Color,
    scale: Float,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(Spacing.d40)
    ) {
        Icon(
            imageVector = if (hasKudos) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (hasKudos) "Remove kudos" else "Give kudos",
            modifier = Modifier.scale(scale),
            tint = color
        )
    }
}

@Composable
private fun KudosCount(count: Int) {
    Text(
        text = "$count",
        color = KudosInactiveColor,
        fontSize = 12.sp
    )
}
