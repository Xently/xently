package co.ke.xently.features.stores.presentation.list.selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.components.StoreCategoryFilterChip
import co.ke.xently.features.stores.presentation.list.components.StoreListItem
import co.ke.xently.features.stores.presentation.list.components.StoreListScreenContent
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import kotlinx.coroutines.flow.flowOf

@Composable
fun StoreSelectionListScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAddStore: () -> Unit,
    onClickEditStore: (Store) -> Unit,
    onStoreSelected: (Store) -> Unit,
) {
    val viewModel = hiltViewModel<StoreSelectionListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val stores = viewModel.stores.collectAsLazyPagingItems()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is StoreSelectionListEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }

                is StoreSelectionListEvent.Success -> {
                    when (event.action) {
                        is StoreSelectionListAction.DeleteStore -> {
                            snackbarHostState.showSnackbar(
                                context.getString(R.string.message_store_deleted),
                                duration = SnackbarDuration.Short,
                            )
                        }

                        is StoreSelectionListAction.SelectStore -> {
                            onStoreSelected(event.action.store)
                        }

                        else -> throw NotImplementedError()
                    }
                }
            }
        }
    }

    StoreSelectionListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        stores = stores,
        categories = categories,
        modifier = modifier,
        onClickAddStore = onClickAddStore,
        onClickEditStore = onClickEditStore,
        onAction = viewModel::onAction,
        onClickBack = onClickBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreSelectionListScreen(
    state: StoreSelectionListUiState,
    snackbarHostState: SnackbarHostState,
    stores: LazyPagingItems<Store>,
    categories: List<StoreCategory>,
    modifier: Modifier = Modifier,
    onClickAddStore: () -> Unit,
    onClickEditStore: (Store) -> Unit,
    onAction: (StoreSelectionListAction) -> Unit,
    onClickBack: () -> Unit,
) {
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
                                category = item,
                                onClickSelectCategory = {
                                    onAction(StoreSelectionListAction.SelectCategory(item))
                                },
                                onClickRemoveCategory = {
                                    onAction(StoreSelectionListAction.RemoveCategory(item))
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
        StoreListScreenContent(
            stores = stores,
            paddingValues = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) { store ->
            if (store != null) {
                StoreListItem(
                    store = store,
                    onClickUpdate = { onClickEditStore(store) },
                    onClickConfirmDelete = { onAction(StoreSelectionListAction.DeleteStore(store)) },
                    modifier = Modifier.clickable {
                        onAction(
                            StoreSelectionListAction.SelectStore(
                                store
                            )
                        )
                    },
                )
            } else {
                StoreListItem(
                    store = Store.DEFAULT,
                    isLoading = true,
                    onClickUpdate = {},
                    onClickConfirmDelete = {},
                )
            }
        }
    }
}

private class StoreSelectionListScreenUiState(
    val state: StoreSelectionListUiState,
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
            StoreSelectionListScreenUiState(state = StoreSelectionListUiState()),
            StoreSelectionListScreenUiState(state = StoreSelectionListUiState(isLoading = true)),
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
        StoreSelectionListScreen(
            state = state.state,
            snackbarHostState = remember {
                SnackbarHostState()
            },
            stores = stores,
            categories = emptyList(),
            modifier = Modifier.fillMaxSize(),
            onClickAddStore = {},
            onClickEditStore = {},
            onAction = {},
            onClickBack = {},
        )
    }
}
