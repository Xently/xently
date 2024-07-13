package co.ke.xently.features.auth.presentation.signup

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.auth.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState

@Composable
internal fun SignUpScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickSignIn: () -> Unit,
) {
    val viewModel = hiltViewModel<SignUpViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                SignUpEvent.Success -> onClickBack()
                is SignUpEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    SignUpScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = viewModel::onAction,
        onClickSignIn = onClickSignIn,
    )
}

@Composable
private fun SignUpScreen(
    state: SignUpUiState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (SignUpAction) -> Unit,
    onClickSignIn: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        SignUpScreen(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onAction = onAction,
            onClickSignIn = onClickSignIn,
        )
    }
}

@Composable
private fun SignUpScreen(
    state: SignUpUiState,
    modifier: Modifier = Modifier,
    onAction: (SignUpAction) -> Unit,
    onClickSignIn: () -> Unit,
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
                text = stringResource(R.string.action_sign_up),
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
                    text = stringResource(R.string.field_label_name),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onAction(SignUpAction.ChangeName(it)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                    ),
                    shape = RoundedCornerShape(35),
                    placeholder = {
                        Text(
                            text = "John Doe",
                            fontWeight = FontWeight.ExtraLight,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.field_label_email),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onAction(SignUpAction.ChangeEmail(it)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email,
                    ),
                    placeholder = {
                        Text(
                            text = "your.email@example.org",
                            fontWeight = FontWeight.ExtraLight,
                        )
                    },
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
                    onValueChange = { onAction(SignUpAction.ChangePassword(it)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password,
                    ),
                    placeholder = {
                        Text(
                            text = "************",
                            fontWeight = FontWeight.ExtraLight,
                        )
                    },
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
                        IconButton(onClick = { onAction(SignUpAction.TogglePasswordVisibility) }) {
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
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    enabled = state.enableSignUpButton,
                    onClick = {
                        focusManager.clearFocus()
                        onAction(SignUpAction.ClickSubmitCredentials)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary,
                    )
                ) { Text(text = stringResource(R.string.action_sign_up)) }
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
                TextButton(
                    onClick = onClickSignIn,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = stringResource(R.string.action_sign_in),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}


private class SignUpUiStateParameterProvider : PreviewParameterProvider<SignUpUiState> {
    override val values: Sequence<SignUpUiState>
        get() = sequenceOf(
            SignUpUiState(),
            SignUpUiState(
                email = "example",
                password = "",
            ),
            SignUpUiState(
                email = "",
                password = "password",
            ),
            SignUpUiState(
                name = "John Doe",
                email = "example",
                password = "password",
            ),
            SignUpUiState(
                name = "John Doe",
                email = "example",
                password = "password",
                isPasswordVisible = true,
            ),
            SignUpUiState(
                name = "John Doe",
                email = "example",
                password = "password",
                isLoading = true,
            ),
        )
}

@XentlyPreview
@Composable
private fun SignUpScreenPreview(
    @PreviewParameter(SignUpUiStateParameterProvider::class)
    state: SignUpUiState,
) {
    XentlyTheme {
        SignUpScreen(
            state = state,
            snackbarHostState = rememberSnackbarHostState(),
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClickSignIn = {},
        )
    }
}
