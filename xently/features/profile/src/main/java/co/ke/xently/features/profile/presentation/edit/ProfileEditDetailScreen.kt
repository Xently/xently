package co.ke.xently.features.profile.presentation.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.profile.R
import co.ke.xently.features.profile.data.domain.ProfileStatistic
import co.ke.xently.features.profile.data.domain.error.NameError
import co.ke.xently.features.profile.presentation.utils.toUiText
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState

@Composable
fun ProfileEditDetailScreen(modifier: Modifier = Modifier, onClickBack: () -> Unit) {
    val viewModel = hiltViewModel<ProfileEditDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is ProfileEditDetailEvent.Success -> {
                    when (event.action) {
                        ProfileEditDetailAction.ClickSave -> onClickBack()

                        else -> throw NotImplementedError()
                    }
                }

                is ProfileEditDetailEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    ProfileEditDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileEditDetailScreen(
    state: ProfileEditDetailUiState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (ProfileEditDetailAction) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_edit_profile_details)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )
                }
            }
        },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        val authenticationState by LocalAuthenticationState.current
        LaunchedEffect(authenticationState.currentUser) {
            authenticationState.currentUser?.also {
                onAction(ProfileEditDetailAction.ChangeFirstName(it.firstName ?: ""))
                onAction(ProfileEditDetailAction.ChangeLastName(it.lastName ?: ""))
                onAction(ProfileEditDetailAction.ChangeEmail(it.email ?: ""))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.firstName,
                enabled = !state.disableFields,
                onValueChange = { onAction(ProfileEditDetailAction.ChangeFirstName(it)) },
                label = { Text(text = stringResource(R.string.text_field_label_profile_first_name)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                isError = !state.firstNameError.isNullOrEmpty(),
                supportingText = state.firstNameError?.let {
                    { Text(text = it.toUiText()) }
                },
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.lastName,
                enabled = !state.disableFields,
                onValueChange = { onAction(ProfileEditDetailAction.ChangeLastName(it)) },
                label = { Text(text = stringResource(R.string.text_field_label_profile_last_name)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                isError = !state.lastNameError.isNullOrEmpty(),
                supportingText = state.lastNameError?.let {
                    { Text(text = it.toUiText()) }
                },
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.email,
                enabled = !state.disableFields,
                onValueChange = { onAction(ProfileEditDetailAction.ChangeEmail(it)) },
                label = { Text(text = stringResource(R.string.text_field_label_profile_email)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                   keyboardType = KeyboardType.Email,
                ),
                isError = !state.emailError.isNullOrEmpty(),
                supportingText = state.emailError?.let {
                    { Text(text = it.toUiText()) }
                },
            )

            Button(
                enabled = false, // TODO: Replace with `state.enableSaveButton`
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                onClick = { onAction(ProfileEditDetailAction.ClickSave) },
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            ) { Text(text = stringResource(R.string.action_save).toUpperCase(Locale.current)) }
        }
    }
}

private class ProfileEditDetailScreenUiState(
    val state: ProfileEditDetailUiState,
)

private class ProfileEditDetailUiStateParameterProvider :
    PreviewParameterProvider<ProfileEditDetailScreenUiState> {
    private val profileStatistic = ProfileStatistic.DEFAULT
    override val values: Sequence<ProfileEditDetailScreenUiState>
        get() = sequenceOf(
            ProfileEditDetailScreenUiState(state = ProfileEditDetailUiState()),
            ProfileEditDetailScreenUiState(
                state = ProfileEditDetailUiState(
                    firstNameError = listOf(NameError.entries.random()),
                ),
            ),
            ProfileEditDetailScreenUiState(state = ProfileEditDetailUiState(profileStatistic = profileStatistic)),
            ProfileEditDetailScreenUiState(state = ProfileEditDetailUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun ProfileEditDetailScreenPreview(
    @PreviewParameter(ProfileEditDetailUiStateParameterProvider::class)
    state: ProfileEditDetailScreenUiState,
) {
    XentlyTheme {
        ProfileEditDetailScreen(
            state = state.state,
            snackbarHostState = rememberSnackbarHostState(),
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
        )
    }
}
