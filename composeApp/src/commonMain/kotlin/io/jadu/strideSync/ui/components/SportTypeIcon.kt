package io.jadu.strideSync.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.jadu.strideSync.domain.model.SportType

@Composable
fun SportTypeIcon(
    sportType: SportType,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    val imageVector = when (sportType) {
        SportType.Run -> Icons.AutoMirrored.Filled.DirectionsRun
        SportType.Ride -> Icons.AutoMirrored.Filled.DirectionsBike
        SportType.Hike -> Icons.Default.Hiking
        SportType.Swim -> Icons.Default.Pool
        SportType.Walk -> Icons.AutoMirrored.Filled.DirectionsWalk
        SportType.Other -> Icons.Default.QuestionMark
    }

    Icon(
        imageVector = imageVector,
        contentDescription = sportType.name,
        modifier = modifier,
        tint = tint
    )
}
