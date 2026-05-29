package io.jadu.strideSync.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.components.StrideFloatingTextField
import io.jadu.strideSync.ui.components.StrideToast
import io.jadu.strideSync.ui.theme.Spacing
import io.jadu.strideSync.ui.theme.StrideColors
import io.jadu.strideSync.ui.vectors.AppleIcon
import io.jadu.strideSync.ui.vectors.GoogleIcon
import io.jadu.strideSync.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var toastMessage by remember { mutableStateOf("") }

    if (uiState is AuthViewModel.UiState.Success) {
        onRegisterSuccess()
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect { message -> toastMessage = message }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = Spacing.d360)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxxl)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "StrideSync",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Create account",
                    color = StrideColors.TextPrimary,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 48.sp,
                    letterSpacing = (-1).sp,
                    textAlign = TextAlign.Center
                )
            }

            RegisterForm(
                onRegister = { email, name, password -> viewModel.register(email, name, password) },
                isLoading = uiState is AuthViewModel.UiState.Loading
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "or",
                    color = StrideColors.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = Spacing.lg)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                OutlinedButton(
                    onClick = { },
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = SolidColor(MaterialTheme.colorScheme.outline)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Spacing.d56)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = GoogleIcon,
                            contentDescription = "Google Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(Spacing.xl)
                        )
                        Text(
                            text = "Continue with Google",
                            color = StrideColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                OutlinedButton(
                    onClick = { },
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = SolidColor(MaterialTheme.colorScheme.outline)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Spacing.d56)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppleIcon,
                            contentDescription = "Apple Logo",
                            tint = Color.White,
                            modifier = Modifier.size(Spacing.d22)
                        )
                        Text(
                            text = "Continue with Apple",
                            color = StrideColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account? ",
                    color = StrideColors.TextSecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = "Log in",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }

        if (toastMessage.isNotEmpty()) {
            StrideToast(
                message = toastMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = Spacing.sm),
                onDismiss = { toastMessage = "" }
            )
        }
    }
}

@Composable
private fun RegisterForm(
    onRegister: (String, String, String) -> Unit,
    isLoading: Boolean
) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        StrideFloatingTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = "Display Name",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        StrideFloatingTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email address",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        StrideFloatingTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = "Password",
            isPasswordVisible = passwordVisible,
            showVisibilityToggle = true,
            onPasswordVisibilityChange = { passwordVisible = it },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        StrideFloatingTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordError = null
            },
            label = "Confirm Password",
            isPasswordVisible = confirmPasswordVisible,
            showVisibilityToggle = true,
            onPasswordVisibilityChange = { confirmPasswordVisible = it },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isError = passwordError != null,
            supportingText = passwordError?.let { { Text(it) } }
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    passwordError = "Passwords do not match"
                    return@Button
                }
                if (password.length < 6) {
                    passwordError = "Password must be at least 6 characters"
                    return@Button
                }
                onRegister(email, displayName, password)
            },
            enabled = !isLoading,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
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
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    strokeWidth = Spacing.xxs,
                    modifier = Modifier.size(Spacing.xxl)
                )
            } else {
                Text(
                    text = "Create account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
