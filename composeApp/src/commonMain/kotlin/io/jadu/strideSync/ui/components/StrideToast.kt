package io.jadu.strideSync.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class StrideToastType { Success, Error }

@Composable
fun StrideToast(
    message: String,
    type: StrideToastType = StrideToastType.Error,
    modifier: Modifier = Modifier,
    durationMillis: Long = 3000L,
    onDismiss: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(message) {
        visible = true
        delay(durationMillis)
        visible = false
        delay(400)
        onDismiss()
    }

    val bgColor = if (type == StrideToastType.Success) Color(0xFF3ECF8E) else Color(0xFFF56565)
    val contentColor = if (type == StrideToastType.Success) Color(0xFF001C3A) else Color.White
    val icon = if (type == StrideToastType.Success) Icons.Default.CheckCircle else Icons.Default.Error

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(300)) + slideInVertically(
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ) { -it },
        exit = fadeOut(tween(250)) + slideOutVertically(tween(300)) { -it }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .shadow(elevation = 12.dp, shape = CircleShape)
                .background(bgColor, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = message,
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
