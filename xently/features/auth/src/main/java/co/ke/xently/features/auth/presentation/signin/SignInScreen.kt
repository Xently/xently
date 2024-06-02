package co.ke.xently.features.auth.presentation.signin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.auth.R
import co.ke.xently.features.auth.data.domain.error.DataError
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.theme.LocalThemeIsDark
import com.mmk.kmpauth.uihelper.google.GoogleButtonMode
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton

@Composable
internal fun SignInScreen(
    state: SignInUiState,
    event: SignInEvent?,
    modifier: Modifier = Modifier,
    onAction: (SignInAction) -> Unit,
    onClickBack: () -> Unit,
    onClickCreateAccount: () -> Unit,
    onClickForgotPassword: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is SignInEvent.Error -> {
                val result = snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                    actionLabel = if (event.type is DataError.Network) {
                        context.getString(R.string.action_retry)
                    } else {
                        null
                    },
                )

                when (result) {
                    SnackbarResult.Dismissed -> {

                    }

                    SnackbarResult.ActionPerformed -> {

                    }
                }
            }

            SignInEvent.Success -> onClickBack()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        SignInScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = state,
            onAction = onAction,
            onClickCreateAccount = onClickCreateAccount,
            onClickForgotPassword = onClickForgotPassword,
        )
    }
}

@Composable
private fun SignInScreen(
    state: SignInUiState,
    modifier: Modifier = Modifier,
    onAction: (SignInAction) -> Unit,
    onClickCreateAccount: () -> Unit,
    onClickForgotPassword: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            /*Image(
                Icons.Default.Business,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(50.dp),
            )*/
            Text(
                text = stringResource(R.string.action_sign_in),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(min = 300.dp, max = 300.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 30.dp,
                ),
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(min = 300.dp, max = 300.dp),
            ) {
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.field_label_email),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onAction(SignInAction.ChangeEmail(it)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email,
                    ),
                    placeholder = { Text(text = "john.doe@example.com") },
                    shape = RoundedCornerShape(35),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.field_label_password),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onAction(SignInAction.ChangePassword(it)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password,
                    ),
                    placeholder = { Text(text = "************") },
                    visualTransformation = remember(state.isPasswordVisible) {
                        derivedStateOf {
                            if (state.isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            }
                        }
                    }.value,
                    trailingIcon = {
                        IconButton(onClick = { onAction(SignInAction.TogglePasswordVisibility) }) {
                            if (state.isPasswordVisible) {
                                Icon(
                                    Icons.Default.VisibilityOff,
                                    contentDescription = stringResource(R.string.action_hide_password),
                                )
                            } else {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = stringResource(R.string.action_show_password),
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(35),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
                TextButton(
                    onClick = onClickForgotPassword,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.action_label_forgot_password),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Button(
                    enabled = state.enableSignInButton,
                    onClick = {
                        focusManager.clearFocus()
                        onAction(SignInAction.ClickSubmitCredentials)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary,
                    )
                ) { Text(text = stringResource(R.string.action_sign_in)) }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.or),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (!LocalInspectionMode.current) {
                    val isDark by LocalThemeIsDark.current
                    GoogleSignInButton(
                        text = stringResource(R.string.action_sign_in_with_google),
                        onClick = { onAction(SignInAction.ClickSignInWithGoogle) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        mode = remember(isDark) {
                            derivedStateOf {
                                if (isDark) {
                                    GoogleButtonMode.Dark
                                } else {
                                    GoogleButtonMode.Light
                                }
                            }
                        }.value,
                    )
                }
                TextButton(
                    onClick = onClickCreateAccount,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = stringResource(R.string.action_register),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}


private class SignInUiStateParameterProvider : PreviewParameterProvider<SignInUiState> {
    override val values: Sequence<SignInUiState>
        get() = sequenceOf(
            SignInUiState(),
            SignInUiState(
                email = "example",
                password = "",
            ),
            SignInUiState(
                email = "",
                password = "password",
            ),
            SignInUiState(
                email = "example",
                password = "password",
            ),
            SignInUiState(
                email = "example",
                password = "password",
                isPasswordVisible = true,
            ),
            SignInUiState(
                email = "example",
                password = "password",
                isLoading = true,
            ),
        )
}

@XentlyPreview
@Composable
private fun SignInScreenPreview(
    @PreviewParameter(SignInUiStateParameterProvider::class)
    state: SignInUiState,
) {
    XentlyTheme {
        SignInScreen(
            state = state,
            event = null,
            onClickBack = {},
            onAction = {},
            onClickCreateAccount = {},
            onClickForgotPassword = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}