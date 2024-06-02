package co.ke.xently.features.auth.presentation.resetpassword

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.auth.R
import co.ke.xently.features.auth.data.domain.error.DataError
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview

@Composable
internal fun RequestPasswordResetScreen(
    state: RequestPasswordResetUiState,
    event: RequestPasswordResetEvent?,
    modifier: Modifier = Modifier,
    onAction: (RequestPasswordResetAction) -> Unit,
    onClickBack: () -> Unit,
    onClickSignIn: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is RequestPasswordResetEvent.Error -> {
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

            RequestPasswordResetEvent.Success -> onClickBack()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        RequestPasswordResetScreen(
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
private fun RequestPasswordResetScreen(
    state: RequestPasswordResetUiState,
    modifier: Modifier = Modifier,
    onAction: (RequestPasswordResetAction) -> Unit,
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
                text = stringResource(R.string.action_request_password_reset),
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
                    onValueChange = { onAction(RequestPasswordResetAction.ChangeEmail(it)) },
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
                Button(
                    enabled = state.enableRequestPasswordResetButton,
                    onClick = {
                        focusManager.clearFocus()
                        onAction(RequestPasswordResetAction.ClickSubmitCredentials)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary,
                    )
                ) { Text(text = stringResource(R.string.action_request_password_reset)) }
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


private class RequestPasswordResetUiStateParameterProvider :
    PreviewParameterProvider<RequestPasswordResetUiState> {
    override val values: Sequence<RequestPasswordResetUiState>
        get() = sequenceOf(
            RequestPasswordResetUiState(),
            RequestPasswordResetUiState(
                email = "john.doe@example.com",
            ),
            RequestPasswordResetUiState(
                email = "john.doe@example.com",
                isLoading = true,
            ),
        )
}

@XentlyPreview
@Composable
private fun RequestPasswordResetScreenPreview(
    @PreviewParameter(RequestPasswordResetUiStateParameterProvider::class)
    state: RequestPasswordResetUiState,
) {
    XentlyTheme {
        RequestPasswordResetScreen(
            state = state,
            event = null,
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClickBack = {},
            onClickSignIn = {},
        )
    }
}
