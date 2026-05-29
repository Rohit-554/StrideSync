package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.theme.StrideColors
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

    val style = toastStyleFor(type)

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(300)) + slideInVertically(
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ) { -it },
        exit = fadeOut(tween(250)) + slideOutVertically(tween(300)) { -it }
    ) {
        ToastContent(message = message, style = style)
    }
}

private data class ToastStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val icon: ImageVector
)

@Composable
private fun toastStyleFor(type: StrideToastType): ToastStyle = when (type) {
    StrideToastType.Success -> ToastStyle(
        backgroundColor = StrideColors.Success,
        contentColor = StrideColors.InkDark,
        icon = Icons.Default.CheckCircle
    )
    StrideToastType.Error -> ToastStyle(
        backgroundColor = StrideColors.Error,
        contentColor = StrideColors.White,
        icon = Icons.Default.Error
    )
}

@Composable
private fun ToastContent(message: String, style: ToastStyle) {
    Row(
        modifier = Modifier
            .padding(horizontal = Spacing.xxl, vertical = Spacing.sm)
            .shadow(elevation = Spacing.md, shape = CircleShape)
            .background(style.backgroundColor, CircleShape)
            .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = null,
            tint = style.contentColor,
            modifier = Modifier.size(Spacing.xl)
        )
        Text(
            text = message,
            color = style.contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
