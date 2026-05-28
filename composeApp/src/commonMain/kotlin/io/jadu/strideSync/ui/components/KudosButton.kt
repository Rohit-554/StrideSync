package io.jadu.strideSync.ui.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KudosButton(
    kudosCount: Int,
    hasKudos: Boolean,
    onKudosToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heartColor by animateColorAsState(
        targetValue = if (hasKudos) Color(0xFFFC4C02) else Color(0xFF9BA3B2),
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
        IconButton(
            onClick = onKudosToggle,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (hasKudos) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (hasKudos) "Remove kudos" else "Give kudos",
                modifier = Modifier.scale(scale),
                tint = heartColor
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "$kudosCount",
            color = Color(0xFF9BA3B2),
            fontSize = 12.sp
        )
    }
}
