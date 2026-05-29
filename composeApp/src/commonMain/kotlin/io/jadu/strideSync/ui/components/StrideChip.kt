package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.theme.StrideColors

@Composable
fun StrideChip(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) StrideColors.White else StrideColors.TextPrimary

    Box(
        modifier = modifier
            .background(containerColor, shape = CircleShape)
            .clickable { onClick() }
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(Spacing.lg)
            )
            Text(
                text = text.uppercase(),
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun StrideIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavoriteActive: Boolean = false
) {
    val tintColor = if (isFavoriteActive) MaterialTheme.colorScheme.primary else StrideColors.TextSecondary

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(Spacing.d40)
            .background(StrideColors.SurfaceAlt, shape = CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tintColor,
            modifier = Modifier.size(Spacing.xl)
        )
    }
}
