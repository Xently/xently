package co.ke.xently.features.auth.presentation.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.auth.R
import co.ke.xently.features.auth.data.domain.error.DataError
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SignInScreen(
    state: SignInUiState,
    event: SignInEvent?,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (SignInAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            is SignInEvent.Error -> {
                val result = snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                    actionLabel = if (event.type is DataError.Network) "Retry" else null,
                )

                when (result) {
                    SnackbarResult.Dismissed -> {

                    }

                    SnackbarResult.ActionPerformed -> {

                    }
                }
            }

            null -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.action_sign_in)) },
                navigationIcon = {
                    NavigateBackIconButton(onClick = onClickBack)
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AnimatedVisibility(state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            TextField(
                value = state.email,
                onValueChange = { onAction(SignInAction.ChangeEmail(it)) },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            TextField(
                value = state.password,
                onValueChange = { onAction(SignInAction.ChangePassword(it)) },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                ),
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                trailingIcon = {
                    IconButton(onClick = { onAction(SignInAction.TogglePasswordVisibility) }) {
                        AnimatedContent(
                            state.isPasswordVisible,
                            label = "toggle password visibility",
                        ) { isPasswordShowing ->
                            if (isPasswordShowing) {
                                Icon(
                                    Icons.Default.VisibilityOff,
                                    contentDescription = "Hide password",
                                )
                            } else {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = "Show password",
                                )
                            }
                        }
                    }
                },
            )
            val focusManager = LocalFocusManager.current
            Button(
                enabled = state.enableSignInButton,
                onClick = {
                    focusManager.clearFocus()
                    onAction(SignInAction.ClickSubmitCredentials)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                content = { Text("Continue") },
            )
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
            modifier = Modifier.fillMaxSize(),
        )
    }
}
