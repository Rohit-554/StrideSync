package io.jadu.strideSync.ui.theme

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import stridesync.composeapp.generated.resources.Res
import stridesync.composeapp.generated.resources.google_sans_bold
import stridesync.composeapp.generated.resources.google_sans_medium
import stridesync.composeapp.generated.resources.google_sans_regular

val Primary = Color(0xFFFFB59F)
val OnPrimary = Color(0xFF5E1700)
val PrimaryContainer = Color(0xFFFF571B)
val OnPrimaryContainer = Color(0xFF531300)
val Secondary = Color(0xFFC4C6D0)
val OnSecondary = Color(0xFF2D3038)
val SecondaryContainer = Color(0xFF464951)
val OnSecondaryContainer = Color(0xFFB6B8C1)
val Tertiary = Color(0xFFA5C8FF)
val OnTertiary = Color(0xFF00315E)
val TertiaryContainer = Color(0xFF2692FF)
val OnTertiaryContainer = Color(0xFF002A53)
val Background = Color(0xFF111318)
val OnBackground = Color(0xFFFBDCD3)
val Surface = Color(0xFF1F0F0B)
val OnSurface = Color(0xFFFBDCD3)
val SurfaceVariant = Color(0xFF44302A)
val OnSurfaceVariant = Color(0xFFE6BEB2)
val Outline = Color(0xFFAC897E)
val OutlineVariant = Color(0xFF5C4038)
val InverseSurface = Color(0xFFFBDCD3)
val InverseOnSurface = Color(0xFF3F2C26)
val InversePrimary = Color(0xFFAE3100)
val Error = Color(0xFFF56565)
val OnError = Color(0xFF690005)
val ErrorContainer = Color(0xFF93000A)
val OnErrorContainer = Color(0xFFFFDAD6)

val Success = Color(0xFF3ECF8E)
val CardSurface = Color(0xFF1C1F26)
val SurfaceAlt = Color(0xFF252830)
val TextPrimary = Color(0xFFF0F0F0)
val TextSecondary = Color(0xFF9BA3B2)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    inversePrimary = InversePrimary,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

val StrideSyncShapes = Shapes(
    small = RoundedCornerShape(Spacing.xs),
    medium = RoundedCornerShape(Spacing.sm),
    large = RoundedCornerShape(Spacing.md),
    extraLarge = RoundedCornerShape(Spacing.lg)
)

@Composable
fun getStrideFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.google_sans_regular, weight = FontWeight.Normal),
        Font(Res.font.google_sans_medium, weight = FontWeight.Medium),
        Font(Res.font.google_sans_bold, weight = FontWeight.Bold)
    )
}

@Composable
fun StrideSyncTheme(content: @Composable () -> Unit) {
    val googleSans = getStrideFontFamily()

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = strideTypography(googleSans),
        shapes = StrideSyncShapes,
        content = content
    )
}

private fun strideTypography(fontFamily: FontFamily): Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
