package io.jadu.strideSync.ui.vectors

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GoogleIcon = ImageVector.Builder(
    name = "GoogleIcon",
    defaultWidth = Spacing.xxl,
    defaultHeight = Spacing.xxl,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(fill = SolidColor(Color(0xFF4285F4))) {
        moveTo(22.56f, 12.25f)
        curveTo(22.56f, 11.47f, 22.49f, 10.72f, 22.36f, 10f)
        lineTo(12f, 10f)
        verticalLineTo(14.26f)
        lineTo(17.92f, 14.26f)
        curveTo(17.66f, 15.63f, 16.88f, 16.79f, 15.71f, 17.57f)
        verticalLineTo(20.34f)
        lineTo(19.28f, 20.34f)
        curveTo(21.36f, 18.42f, 22.56f, 15.6f, 22.56f, 12.25f)
        close()
    }
    path(fill = SolidColor(Color(0xFF34A853))) {
        moveTo(12f, 23f)
        curveTo(14.97f, 23f, 17.46f, 22.02f, 19.28f, 20.34f)
        lineTo(15.71f, 17.57f)
        curveTo(14.73f, 18.23f, 13.48f, 18.63f, 12f, 18.63f)
        curveTo(9.14f, 18.63f, 6.71f, 16.7f, 5.84f, 14.1f)
        lineTo(2.18f, 14.1f)
        verticalLineTo(16.94f)
        curveTo(3.99f, 20.53f, 7.7f, 23f, 12f, 23f)
        close()
    }
    path(fill = SolidColor(Color(0xFFFBBC05))) {
        moveTo(5.84f, 14.09f)
        curveTo(5.62f, 13.43f, 5.49f, 12.73f, 5.49f, 12f)
        curveTo(5.49f, 11.27f, 5.62f, 10.57f, 5.84f, 9.91f)
        verticalLineTo(7.07f)
        lineTo(2.18f, 7.07f)
        curveTo(1.43f, 8.55f, 1f, 10.22f, 1f, 12f)
        curveTo(1f, 13.78f, 1.43f, 15.45f, 2.18f, 16.93f)
        lineTo(5.03f, 14.71f)
        lineTo(5.84f, 14.09f)
        close()
    }
    path(fill = SolidColor(Color(0xFFEA4335))) {
        moveTo(12f, 5.38f)
        curveTo(13.62f, 5.38f, 15.06f, 5.94f, 16.21f, 7.02f)
        lineTo(19.36f, 3.87f)
        curveTo(17.45f, 2.09f, 14.97f, 1f, 12f, 1f)
        curveTo(7.7f, 1f, 3.99f, 3.47f, 2.18f, 7.07f)
        lineTo(5.84f, 9.91f)
        curveTo(6.71f, 7.31f, 9.14f, 5.38f, 12f, 5.38f)
        close()
    }
}.build()

val AppleIcon = ImageVector.Builder(
    name = "AppleIcon",
    defaultWidth = Spacing.xxl,
    defaultHeight = Spacing.xxl,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(fill = SolidColor(Color.White)) {
        moveTo(17.05f, 20.28f)
        curveTo(16.07f, 21.23f, 15f, 21.08f, 13.97f, 20.63f)
        curveTo(12.88f, 20.17f, 11.88f, 20.15f, 10.73f, 20.63f)
        curveTo(9.29f, 21.25f, 8.53f, 21.07f, 7.67f, 20.28f)
        curveTo(2.79f, 15.25f, 3.51f, 7.59f, 9.05f, 7.31f)
        curveTo(10.4f, 7.36f, 11.31f, 7.76f, 12.14f, 7.76f)
        curveTo(12.95f, 7.76f, 14.18f, 7.32f, 15.5f, 7.32f)
        curveTo(17.24f, 7.32f, 18.51f, 7.94f, 19.36f, 9.04f)
        curveTo(16.19f, 10.87f, 16.74f, 14.96f, 19.87f, 16.13f)
        curveTo(19.14f, 17.8f, 18.3f, 19.36f, 17.05f, 20.28f)
        close()
    }
    path(fill = SolidColor(Color.White)) {
        moveTo(12.03f, 7.25f)
        curveTo(11.88f, 5.02f, 13.69f, 3.18f, 15.77f, 3f)
        curveTo(16.06f, 5.58f, 13.43f, 7.5f, 12.03f, 7.25f)
        close()
    }
}.build()
