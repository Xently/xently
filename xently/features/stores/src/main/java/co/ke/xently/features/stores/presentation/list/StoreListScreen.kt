package co.ke.xently.features.stores.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.toStoreError
import co.ke.xently.features.stores.presentation.components.StoreCategoryFilterChip
import co.ke.xently.features.stores.presentation.list.components.StoreListEmptyState
import co.ke.xently.features.stores.presentation.list.components.StoreListLazyColumn
import co.ke.xently.features.stores.presentation.utils.asUiText
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

@Composable
fun StoreListScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAddStore: () -> Unit,
    onClickEditStore: (Store) -> Unit,
    onStoreSelected: (Store) -> Unit,
) {
    val viewModel = hiltViewModel<StoreListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val stores = viewModel.stores.collectAsLazyPagingItems()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    StoreListScreen(
        state = state,
        event = event,
        stores = stores,
        categories = categories,
        modifier = modifier,
        onClickAddStore = onClickAddStore,
        onClickEditStore = onClickEditStore,
        onAction = viewModel::onAction,
        onClickBack = onClickBack,
        onStoreSelected = onStoreSelected,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreListScreen(
    state: StoreListUiState,
    event: StoreListEvent?,
    stores: LazyPagingItems<Store>,
    categories: List<StoreCategory>,
    modifier: Modifier = Modifier,
    onClickAddStore: () -> Unit,
    onClickEditStore: (Store) -> Unit,
    onAction: (StoreListAction) -> Unit,
    onClickBack: () -> Unit,
    onStoreSelected: (Store) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is StoreListEvent.Error -> {
                snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                )
            }

            is StoreListEvent.Success -> {
                when (event.action) {
                    is StoreListAction.DeleteStore -> {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.message_store_deleted),
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is StoreListAction.SelectStore -> {
                        onStoreSelected(event.action.store)
                    }

                    else -> throw NotImplementedError()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_select_store)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                /*SearchBar(
                    query = state.query,
                    onSearch = { onAction(StoreListAction.Search(it)) },
                    onQueryChange = { onAction(StoreListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_stores_placeholder),
                )*/
                if (categories.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) {
                        items(categories, key = { it.name }) { item ->
                            StoreCategoryFilterChip(
                                item = item,
                                onClickSelectCategory = {
                                    onAction(StoreListAction.SelectCategory(item))
                                },
                                onClickRemoveCategory = {
                                    onAction(StoreListAction.RemoveCategory(item))
                                },
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onClickAddStore,
                text = { Text(text = stringResource(R.string.action_add_store)) },
                icon = {
                    Icon(
                        Icons.Default.AddBusiness,
                        contentDescription = stringResource(R.string.action_add_store),
                    )
                },
            )
        },
    ) { paddingValues ->
        val refreshLoadState = stores.loadState.refresh
        val isRefreshing by remember(refreshLoadState, stores.itemCount) {
            derivedStateOf { refreshLoadState == LoadState.Loading && stores.itemCount > 0 }
        }
        PullRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = stores::refresh,
        ) {
            when {
                stores.itemCount == 0 && refreshLoadState is LoadState.NotLoading -> {
                    StoreListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = stringResource(R.string.message_no_stores_found),
                        onClickRetry = stores::refresh,
                    )
                }

                stores.itemCount == 0 && refreshLoadState is LoadState.Error -> {
                    val error = remember(refreshLoadState) {
                        runBlocking { refreshLoadState.error.toStoreError() }
                    }
                    StoreListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = error.asUiText().asString(),
                        canRetry = error is DataError.Network.Retryable,
                        onClickRetry = stores::retry,
                    )
                }

                stores.itemCount == 0 && refreshLoadState is LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }

                else -> {
                    StoreListLazyColumn(
                        modifier = Modifier.matchParentSize(),
                        stores = stores,
                        onClickEditStore = onClickEditStore,
                        onStoreSelected = { onAction(StoreListAction.SelectStore(it)) },
                        onClickConfirmDelete = { onAction(StoreListAction.DeleteStore(it)) },
                    )
                }
            }
        }
    }
}

private class StoreListScreenUiState(
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
    PreviewParameterProvider<StoreListScreenUiState> {
    override val values: Sequence<StoreListScreenUiState>
        get() = sequenceOf(
            StoreListScreenUiState(state = StoreListUiState()),
            StoreListScreenUiState(state = StoreListUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun StoreListScreenPreview(
    @PreviewParameter(StoreListUiStateParameterProvider::class)
    state: StoreListScreenUiState,
) {
    val stores = flowOf(state.stores).collectAsLazyPagingItems()
    XentlyTheme {
        StoreListScreen(
            state = state.state,
            event = null,
            stores = stores,
            categories = emptyList(),
            modifier = Modifier.fillMaxSize(),
            onClickAddStore = {},
            onClickEditStore = {},
            onAction = {},
            onClickBack = {},
            onStoreSelected = {},
        )
    }
}
