package co.ke.xently.features.recommendations.presentation.response


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.recommendations.R
import co.ke.xently.features.recommendations.presentation.RecommendationEvent
import co.ke.xently.features.recommendations.presentation.RecommendationUiState
import co.ke.xently.features.recommendations.presentation.RecommendationViewModel
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.list.components.StoreListItem
import co.ke.xently.features.stores.presentation.list.components.StoreListScreenContent
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun RecommendationResponseScreen(
    viewModel: RecommendationViewModel,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickRecommendation: (Store) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val stores = viewModel.recommendations.collectAsLazyPagingItems()
    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is RecommendationEvent.Success -> Unit
                is RecommendationEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    RecommendationResponseScreen(
        state = state,
        stores = stores,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onClickBack = onClickBack,
        onClickStore = onClickRecommendation,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecommendationResponseScreen(
    state: RecommendationUiState,
    stores: LazyPagingItems<Store>,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    onClickBack: () -> Unit,
    onClickStore: (Store) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                TopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.topbar_title_recommendation_response)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
    ) { paddingValues ->
        StoreListScreenContent(
            stores = stores,
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            emptyMessage = stringResource(R.string.message_no_recommendation_found),
        ) { store ->
            if (store != null) {
                StoreListItem(
                    store = store,
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        onClickStore(store)
                    },
                )
            } else {
                StoreListItem(
                    store = Store.DEFAULT,
                    isLoading = true,
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}

private class RecommendationResponseScreenUiState(
    val state: RecommendationUiState,
    val stores: PagingData<Store> = PagingData.from(
        List(10) {
            Store(
                id = it + 1L,
                name = "Store $it",
                slug = "store-$it",
                links = mapOf(
                    "self" to Link(href = "https://example.com"),
                ),
            )
        },
    ),
)

private class StoreListUiStateParameterProvider :
    PreviewParameterProvider<RecommendationResponseScreenUiState> {
    override val values: Sequence<RecommendationResponseScreenUiState>
        get() = sequenceOf(
            RecommendationResponseScreenUiState(state = RecommendationUiState()),
            RecommendationResponseScreenUiState(state = RecommendationUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun RecommendationResponseScreenPreview(
    @PreviewParameter(StoreListUiStateParameterProvider::class)
    state: RecommendationResponseScreenUiState,
) {
    val stores = flowOf(state.stores).collectAsLazyPagingItems()
    XentlyTheme {
        RecommendationResponseScreen(
            state = state.state,
            stores = stores,
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
        )
    }
}