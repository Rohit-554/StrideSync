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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var toastMessage by remember { mutableStateOf("") }

    if (uiState is AuthViewModel.UiState.Success) {
        onLoginSuccess()
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
                .widthIn(max = Spacing.d360),
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
                    text = "Welcome back",
                    color = StrideColors.TextPrimary,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 48.sp,
                    letterSpacing = (-1).sp,
                    textAlign = TextAlign.Center
                )
            }

            LoginForm(
                onLogin = { email, password -> viewModel.login(email, password) },
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
                    border = ButtonDefaults.outlinedButtonBorder.copy(
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
                    border = ButtonDefaults.outlinedButtonBorder.copy(
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
                    text = "Don't have an account? ",
                    color = StrideColors.TextSecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign up",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onNavigateToRegister() }
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
private fun LoginForm(
    onLogin: (String, String) -> Unit,
    isLoading: Boolean
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
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
            onValueChange = { password = it },
            label = "Password",
            isPasswordVisible = passwordVisible,
            showVisibilityToggle = true,
            onPasswordVisibilityChange = { passwordVisible = it },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot password?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { }
            )
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        Button(
            onClick = { onLogin(email, password) },
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
                    text = "Log in",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
