package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.theme.StrideColors

@Composable
fun StrideFloatingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isSuccess: Boolean = false,
    isError: Boolean = false,
    showVisibilityToggle: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityChange: ((Boolean) -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    supportingText: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { FieldLabel(label) },
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        shape = RoundedCornerShape(Spacing.sm),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        isError = isError,
        trailingIcon = {
            FieldTrailingIcon(
                isSuccess = isSuccess,
                showVisibilityToggle = showVisibilityToggle,
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityChange = onPasswordVisibilityChange
            )
        },
        colors = textFieldColors(),
        supportingText = supportingText,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun FieldLabel(label: String) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    )
}

@Composable
private fun FieldTrailingIcon(
    isSuccess: Boolean,
    showVisibilityToggle: Boolean,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: ((Boolean) -> Unit)?
) {
    when {
        isSuccess -> SuccessIcon()
        showVisibilityToggle && onPasswordVisibilityChange != null -> VisibilityToggle(
            isVisible = isPasswordVisible,
            onToggle = onPasswordVisibilityChange
        )
    }
}

@Composable
private fun SuccessIcon() {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Success",
        tint = Color.Unspecified
    )
}

@Composable
private fun VisibilityToggle(
    isVisible: Boolean,
    onToggle: (Boolean) -> Unit
) {
    IconButton(onClick = { onToggle(!isVisible) }) {
        Icon(
            imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = "Toggle Visibility"
        )
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = Color.Transparent,
    focusedContainerColor = StrideColors.SurfaceAlt,
    unfocusedContainerColor = StrideColors.SurfaceAlt,
    focusedTextColor = StrideColors.TextPrimary,
    unfocusedTextColor = StrideColors.TextPrimary,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = StrideColors.TextSecondary
)

@Composable
fun PasswordStrengthBar(
    strength: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        StrengthSegments(strength = strength)
        StrengthLabel(strength = strength)
    }
}

@Composable
private fun StrengthSegments(strength: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        repeat(4) { index ->
            StrengthSegment(isActive = index < strength)
        }
    }
}

@Composable
private fun StrengthSegment(isActive: Boolean) {
    val color = if (isActive) StrideColors.Success else MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Spacing.xs)
            .background(color, shape = RoundedCornerShape(Spacing.xxs))
    )
}

@Composable
private fun StrengthLabel(strength: Int) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = strengthLabel(strength),
            color = if (strength >= 3) StrideColors.Success else MaterialTheme.colorScheme.error,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

private fun strengthLabel(strength: Int): String = when (strength) {
    1 -> "WEAK PASSWORD"
    2 -> "MEDIUM PASSWORD"
    3, 4 -> "STRONG PASSWORD"
    else -> "VERY WEAK"
}
