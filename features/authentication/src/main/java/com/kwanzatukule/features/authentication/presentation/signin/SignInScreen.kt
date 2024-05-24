package com.kwanzatukule.features.authentication.presentation.signin


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
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
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.authentication.R
import com.kwanzatukule.features.authentication.domain.error.DataError
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(component: SignInComponent, modifier: Modifier = Modifier) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is SignInEvent.Error -> {
                    val result = snackbarHostState.showSnackbar(
                        it.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                        actionLabel = if (it.type is DataError.Network) "Retry" else null,
                    )

                    when (result) {
                        SnackbarResult.Dismissed -> {

                        }

                        SnackbarResult.ActionPerformed -> {

                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.action_sign_in)) },
                navigationIcon = {
                    IconButton(onClick = component::handleBackPress) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                        )
                    }
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
                onValueChange = component::setEmail,
                label = { Text("Username") },
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
                onValueChange = component::setPassword,
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
                    IconButton(onClick = component::togglePasswordVisibility) {
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
                onClick = { focusManager.clearFocus(); component.signIn() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                content = { Text("Continue") },
            )
        }
    }
}

class SignInUIStateParameterProvider : PreviewParameterProvider<SignInUIState> {
    override val values: Sequence<SignInUIState>
        get() = sequenceOf(
            SignInUIState(),
            SignInUIState(
                email = "example",
                password = "",
            ),
            SignInUIState(
                email = "",
                password = "password",
            ),
            SignInUIState(
                email = "example",
                password = "password",
            ),
            SignInUIState(
                email = "example",
                password = "password",
                isPasswordVisible = true,
            ),
            SignInUIState(
                email = "example",
                password = "password",
                isLoading = true,
            ),
        )
}

@KwanzaPreview
@Composable
private fun SignInScreenPreview(
    @PreviewParameter(SignInUIStateParameterProvider::class)
    uiState: SignInUIState,
) {
    KwanzaTukuleTheme {
        SignInScreen(
            component = SignInComponent.Fake(uiState),
            modifier = Modifier.fillMaxSize(),
        )
    }
}