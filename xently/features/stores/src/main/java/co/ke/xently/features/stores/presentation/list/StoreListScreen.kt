package co.ke.xently.features.stores.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.list.components.StoreItemCard
import co.ke.xently.features.stores.presentation.list.components.StoreListScreenContent
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.components.SearchBar
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import kotlinx.coroutines.flow.flowOf

@Composable
fun StoreListScreen(
    modifier: Modifier = Modifier,
    onClickStore: (Store) -> Unit,
    onClickFilterStores: () -> Unit,
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
        retrieveSuggestions = {
            val searchSuggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()
            searchSuggestions
        },
        snackbarHostState = snackbarHostState,
        stores = stores,
        modifier = modifier,
        onAction = viewModel::onAction,
        topBar = topBar,
        onClickStore = onClickStore,
        onClickFilterStores = onClickFilterStores,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreListScreen(
    state: StoreListUiState,
    snackbarHostState: SnackbarHostState,
    stores: LazyPagingItems<Store>,
    modifier: Modifier = Modifier,
    retrieveSuggestions: @Composable () -> List<String> = { emptyList() },
    onClickStore: (Store) -> Unit,
    onAction: (StoreListAction) -> Unit,
    onClickFilterStores: () -> Unit,
    topBar: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                var initSearch by rememberSaveable { mutableStateOf(false) }
                if (!initSearch) {
                    topBar()
                    AnimatedVisibility(state.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                SearchBar(
                    query = state.query,
                    retrieveSuggestions = retrieveSuggestions,
                    placeholder = stringResource(R.string.search_stores_placeholder),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onQueryChange = { onAction(StoreListAction.ChangeQuery(it)) },
                    onSearch = { onAction(StoreListAction.Search(it)) },
                    onExpandedChange = { initSearch = it },
                    blankQueryIcon = {
                        IconButton(onClick = onClickFilterStores) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = stringResource(R.string.content_desc_filter_stores),
                            )
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        StoreListScreenContent(
            stores = stores,
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp, end = 16.dp, start = 16.dp),
        ) { store ->
            if (store == null) {
                StoreItemCard(
                    store = Store.DEFAULT,
                    isLoading = true,
                    onClick = {},
                )
            } else {
                StoreItemCard(
                    store = store,
                    isLoading = false,
                    onClick = { onClickStore(store) },
                ) { (expanded, onClose) ->
                    DropdownMenu(expanded = expanded, onDismissRequest = onClose) {
                        if (store.links.containsKey("add-bookmark")) {
                            DropdownMenuItem(
                                onClick = { onAction(StoreListAction.ToggleBookmark(store)); onClose() },
                                text = { Text(text = stringResource(R.string.action_add_bookmark)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.BookmarkAdd,
                                        contentDescription = stringResource(R.string.action_add_bookmark),
                                    )
                                },
                            )
                        } else if (store.links.containsKey("remove-bookmark")) {
                            DropdownMenuItem(
                                onClick = { onAction(StoreListAction.ToggleBookmark(store)); onClose() },
                                text = { Text(text = stringResource(R.string.action_remove_bookmark)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.BookmarkRemove,
                                        contentDescription = stringResource(R.string.action_remove_bookmark),
                                    )
                                },
                            )
                        }
                    }
                }
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

@OptIn(ExperimentalMaterial3Api::class)
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
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Example") },
                    navigationIcon = {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    },
                )
            },
            onClickStore = {},
            onClickFilterStores = {},
        )
    }
}
