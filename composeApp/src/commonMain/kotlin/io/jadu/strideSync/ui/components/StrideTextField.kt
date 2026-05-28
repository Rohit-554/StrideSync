package io.jadu.strideSync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

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
        label = {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        isError = isError,
        trailingIcon = {
            if (isSuccess) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color.Unspecified
                )
            } else if (showVisibilityToggle && onPasswordVisibilityChange != null) {
                IconButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Visibility"
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color(0xFF252830),
            unfocusedContainerColor = Color(0xFF252830),
            focusedTextColor = Color(0xFFF0F0F0),
            unfocusedTextColor = Color(0xFFF0F0F0),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color(0xFF9BA3B2)
        ),
        supportingText = supportingText,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun PasswordStrengthBar(
    strength: Int, // value 0 to 4
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0 until 4) {
                val color = if (i < strength) Color(0xFF3ECF8E) else MaterialTheme.colorScheme.surfaceVariant
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(color, shape = RoundedCornerShape(2.dp))
                )
            }
        }
        Text(
            text = when (strength) {
                1 -> "WEAK PASSWORD"
                2 -> "MEDIUM PASSWORD"
                3, 4 -> "STRONG PASSWORD"
                else -> "VERY WEAK"
            },
            color = if (strength >= 3) Color(0xFF3ECF8E) else MaterialTheme.colorScheme.error,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
