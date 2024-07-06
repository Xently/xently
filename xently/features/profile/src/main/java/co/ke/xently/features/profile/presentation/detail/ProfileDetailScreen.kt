package co.ke.xently.features.profile.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.profile.R
import co.ke.xently.features.profile.data.domain.ProfileStatistic
import co.ke.xently.features.profile.presentation.detail.components.StatisticSummaryCard
import co.ke.xently.features.ui.core.presentation.components.CircularButton
import co.ke.xently.features.ui.core.presentation.components.PlaceHolderImageThumbnail
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.XentlyPreview

@Composable
fun ProfileDetailScreen(
    modifier: Modifier = Modifier,
    onClickEditProfile: () -> Unit,
    topBar: @Composable () -> Unit,
) {
    val viewModel = hiltViewModel<ProfileDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is ProfileDetailEvent.Success -> Unit

                is ProfileDetailEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    ProfileDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onClickEditProfile = onClickEditProfile,
        onAction = viewModel::onAction,
        topBar = topBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileDetailScreen(
    state: ProfileDetailUiState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onClickEditProfile: () -> Unit,
    onAction: (ProfileDetailAction) -> Unit,
    topBar: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                topBar()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ListItem(
                headlineContent = {
                    val authenticationState by LocalAuthenticationState.current
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = authenticationState.currentUser?.name ?: "Anonymous",
                    )
                },
                leadingContent = {
                    PlaceHolderImageThumbnail(size = 60.dp) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                        )
                    }
                },
                trailingContent = {
                    CircularButton(
                        onClick = onClickEditProfile,
                        content = {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = stringResource(R.string.action_edit_profile),
                            )
                        },
                    )
                },
            )

            var selectedStatisticName: String? by rememberSaveable(state.profileStatistic) {
                mutableStateOf(null)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(horizontal = 16.dp),
            ) {
                val onClick: (ProfileStatistic.Statistic) -> Unit by rememberUpdatedState {
                    if (it.name == selectedStatisticName) {
                        selectedStatisticName = null
                    } else {
                        // Do not trigger double selection, that is likely to waste precious
                        // customer bandwidth.
                        selectedStatisticName = it.name
                    }
                }
                StatisticSummaryCard(
                    stat = state.profileStatistic.placesVisited.stat,
                    title = state.profileStatistic.placesVisited.name,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isSelected = state.profileStatistic.placesVisited.name == selectedStatisticName,
                    onClick = { onClick(state.profileStatistic.placesVisited) },
                )

                StatisticSummaryCard(
                    stat = state.profileStatistic.bookmarks.stat,
                    title = state.profileStatistic.bookmarks.name,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isSelected = state.profileStatistic.bookmarks.name == selectedStatisticName,
                    onClick = { onClick(state.profileStatistic.bookmarks) },
                )

                StatisticSummaryCard(
                    stat = state.profileStatistic.points.stat,
                    title = state.profileStatistic.points.name,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isSelected = state.profileStatistic.points.name == selectedStatisticName,
                    onClick = {},
                )
            }
        }
    }
}

private class ProfileDetailScreenUiState(
    val state: ProfileDetailUiState,
)

private class ProfileDetailUiStateParameterProvider :
    PreviewParameterProvider<ProfileDetailScreenUiState> {
    private val profileStatistic = ProfileStatistic.DEFAULT
    override val values: Sequence<ProfileDetailScreenUiState>
        get() = sequenceOf(
            ProfileDetailScreenUiState(state = ProfileDetailUiState()),
            ProfileDetailScreenUiState(
                state = ProfileDetailUiState(),
            ),
            ProfileDetailScreenUiState(state = ProfileDetailUiState(profileStatistic = profileStatistic)),
            ProfileDetailScreenUiState(state = ProfileDetailUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun ProfileDetailScreenPreview(
    @PreviewParameter(ProfileDetailUiStateParameterProvider::class)
    state: ProfileDetailScreenUiState,
) {
    XentlyTheme {
        ProfileDetailScreen(
            state = state.state,
            snackbarHostState = remember {
                SnackbarHostState()
            },
            modifier = Modifier.fillMaxSize(),
            onClickEditProfile = {},
            onAction = {},
            topBar = {},
        )
    }
}
