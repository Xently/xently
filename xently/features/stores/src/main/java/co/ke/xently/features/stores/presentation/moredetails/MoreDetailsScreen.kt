package co.ke.xently.features.stores.presentation.moredetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.moredetails.components.MoreDetailListItem
import co.ke.xently.features.stores.presentation.moredetails.components.OpeningHourItem
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.components.shimmer
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun MoreDetailsScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    val viewModel = hiltViewModel<MoreDetailsViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is MoreDetailsEvent.Success -> Unit
                is MoreDetailsEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    MoreDetailsScreen(
        state = state,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onClickBack = onClickBack,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoreDetailsScreen(
    state: MoreDetailsUiState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    onClickBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_more_details)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            val store = state.store ?: Store.DEFAULT
            MoreDetailListItem(
                isLoading = state.isLoading,
                title = stringResource(R.string.more_details_section_title_location),
            ) {
                Text(
                    modifier = Modifier.shimmer(state.isLoading),
                    text = remember(store) {
                        buildString {
                            append(store.shop.name)
                            append(", ")
                            append(store.name)
                        }
                    },
                )
            }
            MoreDetailListItem(
                isLoading = state.isLoading,
                title = stringResource(R.string.more_details_section_title_what_you_get),
            ) {
                Text(
                    modifier = Modifier.shimmer(state.isLoading),
                    text = remember(store.services) {
                        store.services.joinToString().ifBlank { "-" }
                    },
                )
            }
            MoreDetailListItem(
                isLoading = state.isLoading,
                title = stringResource(R.string.more_details_section_title_short_description),
            ) {
                Text(
                    modifier = Modifier.shimmer(state.isLoading),
                    text = remember(store.description) {
                        store.description?.ifBlank { "-" } ?: "-"
                    },
                )
            }
            MoreDetailListItem(
                isLoading = state.isLoading,
                title = stringResource(R.string.more_details_section_title_contact_details)
            ) {
                Text(
                    modifier = Modifier.shimmer(state.isLoading),
                    text = remember(store.telephone, store.email) {
                        buildList {
                            store.run {
                                if (!telephone.isNullOrBlank()) {
                                    add(telephone)
                                }
                                if (!email.isNullOrBlank()) {
                                    add(email)
                                }
                            }
                        }.joinToString().ifBlank { "-" }
                    },
                )
            }

            MoreDetailListItem(
                showDivider = false,
                isLoading = state.isLoading,
                title = stringResource(R.string.more_details_section_title_opening_hours),
            ) {
                Column {
                    if (store.openingHours.isEmpty()) {
                        Text(text = "-", modifier = Modifier.shimmer(state.isLoading))
                    } else {
                        val dayOfWeekToday = rememberDayOfWeekToday()
                        val timePickerState = rememberTimePickerState()
                        for (openingHour in store.openingHours) {
                            OpeningHourItem(
                                isLoading = state.isLoading,
                                openingHour = openingHour,
                                dayOfWeekToday = dayOfWeekToday,
                                timePickerState = timePickerState,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberDayOfWeekToday() = remember {
    Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek
}

internal class MoreDetailsUiStateParameterProvider : PreviewParameterProvider<MoreDetailsUiState> {
    override val values: Sequence<MoreDetailsUiState>
        get() = sequenceOf(
            MoreDetailsUiState(
                isLoading = true,
            ),
            MoreDetailsUiState(
                store = Store.DEFAULT,
            ),
        )
}

@XentlyPreview
@Composable
private fun MoreDetailsScreenPreview(
    @PreviewParameter(MoreDetailsUiStateParameterProvider::class)
    state: MoreDetailsUiState,
) {
    XentlyTheme {
        MoreDetailsScreen(
            state = state,
            onClickBack = {},
        )
    }
}
