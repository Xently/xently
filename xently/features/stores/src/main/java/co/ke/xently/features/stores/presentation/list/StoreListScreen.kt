package co.ke.xently.features.stores.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.list.components.StoreListItemCard
import co.ke.xently.features.stores.presentation.list.components.StoreListScreenContent
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import kotlinx.coroutines.flow.flowOf

@Composable
fun StoreListScreen(
    modifier: Modifier = Modifier,
    onClickStore: (Store) -> Unit,
    topBar: @Composable () -> Unit,
) {
    val viewModel = hiltViewModel<StoreListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val stores = viewModel.stores.collectAsLazyPagingItems()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is StoreListEvent.Success -> Unit
                is StoreListEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    StoreListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        stores = stores,
        modifier = modifier,
        onAction = viewModel::onAction,
        topBar = topBar,
        onClickStore = onClickStore,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreListScreen(
    state: StoreListUiState,
    snackbarHostState: SnackbarHostState,
    stores: LazyPagingItems<Store>,
    modifier: Modifier = Modifier,
    onClickStore: (Store) -> Unit,
    onAction: (StoreListAction) -> Unit,
    topBar: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                topBar()
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                /*SearchBar(
                    query = state.query,
                    onSearch = { onAction(StoreListAction.Search(it)) },
                    onQueryChange = { onAction(StoreListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_stores_placeholder),
                )*/
            }
        },
    ) { paddingValues ->
        StoreListScreenContent(
            stores = stores,
            paddingValues = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) { store ->
            if (store != null) {
                StoreListItemCard(
                    store = store,
                    isLoading = false,
                    onClick = { onClickStore(store) },
                    onClickToggleBookmark = { onAction(StoreListAction.ToggleBookmark(store)) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            } else {
                StoreListItemCard(
                    store = Store.DEFAULT,
                    isLoading = true,
                    onClick = {},
                    onClickToggleBookmark = {},
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                )
            }
        }
    }
}

private class StoreSelectionListScreenUiState(
    val state: StoreListUiState,
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
    PreviewParameterProvider<StoreSelectionListScreenUiState> {
    override val values: Sequence<StoreSelectionListScreenUiState>
        get() = sequenceOf(
            StoreSelectionListScreenUiState(state = StoreListUiState()),
            StoreSelectionListScreenUiState(state = StoreListUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun StoreSelectionListScreenPreview(
    @PreviewParameter(StoreListUiStateParameterProvider::class)
    state: StoreSelectionListScreenUiState,
) {
    val stores = flowOf(state.stores).collectAsLazyPagingItems()
    XentlyTheme {
        StoreListScreen(
            state = state.state,
            snackbarHostState = rememberSnackbarHostState(),
            stores = stores,
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            topBar = {},
            onClickStore = {},
        )
    }
}
