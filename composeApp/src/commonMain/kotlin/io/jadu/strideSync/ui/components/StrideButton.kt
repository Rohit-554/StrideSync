package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.theme.StrideColors

@Composable
fun StridePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(Spacing.d56)
            .shadow(
                elevation = Spacing.sm,
                shape = CircleShape,
                ambientColor = StrideColors.BrandPrimary,
                spotColor = StrideColors.BrandPrimary
            )
    ) {
        if (isLoading) {
            LoadingSpinner()
        } else {
            ButtonLabel(text)
        }
    }
}

@Composable
fun StrideSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(MaterialTheme.colorScheme.outline)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(Spacing.d56)
    ) {
        ButtonLabel(text, fontSize = 16.sp, fontWeight = FontWeight.Normal)
    }
}

@Composable
fun RecordFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = Color.White
        ),
        modifier = modifier
            .size(Spacing.d64)
            .shadow(
                elevation = Spacing.md,
                shape = CircleShape,
                ambientColor = StrideColors.BrandPrimary,
                spotColor = StrideColors.BrandPrimary
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.RadioButtonChecked,
                contentDescription = "Record",
                modifier = Modifier.size(Spacing.d36)
            )
        }
    }
}

@Composable
private fun LoadingSpinner() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        strokeWidth = Spacing.xxs,
        modifier = Modifier.size(Spacing.xxl)
    )
}

@Composable
private fun ButtonLabel(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight
    )
}
