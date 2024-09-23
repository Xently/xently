package co.ke.xently.features.shops.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.shops.R
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.error.toError
import co.ke.xently.features.shops.presentation.list.components.ShopListEmptyState
import co.ke.xently.features.shops.presentation.list.components.ShopListLazyColumn
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.components.LoginAndRetryButtonsRow
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.AuthorisationError
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.core.RetryableError
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.components.SearchBar
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import co.ke.xently.libraries.ui.pagination.ListState
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import co.ke.xently.libraries.ui.pagination.asListState
import kotlinx.coroutines.flow.flowOf

@Composable
fun ShopListScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAddShop: () -> Unit,
    onClickEditShop: (Shop) -> Unit,
) {
    val viewModel = hiltViewModel<ShopListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val shops = viewModel.shops.collectAsLazyPagingItems()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is ShopListEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }

                is ShopListEvent.Success -> {
                    when (event.action) {
                        is ShopListAction.DeleteShop -> {
                            snackbarHostState.showSnackbar(
                                context.getString(R.string.message_shop_deleted),
                                duration = SnackbarDuration.Short,
                            )
                        }

                        is ShopListAction.SelectShop -> {
                            eventHandler.requestStoreSelection(event.action.shop)
                        }

                        else -> throw NotImplementedError()
                    }
                }
            }
        }
    }

    ShopListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        shops = shops,
        modifier = modifier,
        onClickAddShop = onClickAddShop,
        onClickEditShop = onClickEditShop,
        onAction = viewModel::onAction,
        onClickBack = onClickBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShopListScreen(
    state: ShopListUiState,
    snackbarHostState: SnackbarHostState,
    shops: LazyPagingItems<Shop>,
    modifier: Modifier = Modifier,
    onClickAddShop: () -> Unit,
    onClickEditShop: (Shop) -> Unit,
    onAction: (ShopListAction) -> Unit,
    onClickBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                var initSearch by rememberSaveable { mutableStateOf(false) }
                if (!initSearch) {
                    CenterAlignedTopAppBar(
                        windowInsets = WindowInsets.waterfall,
                        title = { Text(text = stringResource(R.string.top_bar_title_select_shop)) },
                        navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                    )
                    AnimatedVisibility(state.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                SearchBar(
                    query = state.query,
                    exitSearchIcon = Icons.Default.Close,
                    clearSearchQueryIcon = Icons.AutoMirrored.Filled.Backspace,
                    onExpandedChange = { initSearch = it },
                    onSearch = { onAction(ShopListAction.Search(it)) },
                    onQueryChange = { onAction(ShopListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_shops_placeholder),
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onClickAddShop,
                text = { Text(text = stringResource(R.string.action_add_shop)) },
                icon = {
                    Icon(
                        Icons.Default.AddBusiness,
                        contentDescription = stringResource(R.string.action_add_shop),
                    )
                },
            )
        },
    ) { paddingValues ->
        val refreshLoadState = shops.loadState.refresh
        val isRefreshing by remember(refreshLoadState, shops.itemCount) {
            derivedStateOf {
                refreshLoadState == LoadState.Loading && shops.itemCount > 0
            }
        }
        PullRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = shops::refresh,
        ) {
            when (val listState =
                refreshLoadState.asListState(shops.itemCount, Throwable::toError)) {
                ListState.Empty -> {
                    ShopListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = stringResource(R.string.message_no_shops_found),
                        onClickRetry = shops::refresh,
                    )
                }

                ListState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }

                ListState.Ready -> {
                    ShopListLazyColumn(
                        modifier = Modifier.matchParentSize(),
                        shops = shops,
                        onClickEditShop = onClickEditShop,
                        onShopSelected = { onAction(ShopListAction.SelectShop(it)) },
                        onClickConfirmDelete = { onAction(ShopListAction.DeleteShop(it)) },
                    )
                }

                is ListState.Error -> {
                    ShopListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = listState.error.asString(),
                        canRetry = listState.error is RetryableError,
                        onClickRetry = shops::retry,
                    ) {
                        if (listState.error is AuthorisationError) {
                            Spacer(modifier = Modifier.height(16.dp))

                            LoginAndRetryButtonsRow(onRetry = shops::retry)
                        }
                    }
                }
            }
        }
    }
}

private class ShopListScreenUiState(
    val state: ShopListUiState,
    val shops: PagingData<Shop> = PagingData.from(
        List(10) {
            Shop(
                id = it + 1L,
                name = "Shop $it",
                slug = "shop-$it",
                onlineShopUrl = "https://example.com",
                links = mapOf(
                    "self" to Link(href = "https://example.com"),
                    "add-store" to Link(href = "https://example.com/edit"),
                ),
            )
        },
    ),
)

private class ShopListUiStateParameterProvider : PreviewParameterProvider<ShopListScreenUiState> {
    override val values: Sequence<ShopListScreenUiState>
        get() = sequenceOf(
            ShopListScreenUiState(state = ShopListUiState()),
            ShopListScreenUiState(state = ShopListUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun ShopListScreenPreview(
    @PreviewParameter(ShopListUiStateParameterProvider::class) state: ShopListScreenUiState,
) {
    val shops = flowOf(state.shops).collectAsLazyPagingItems()
    XentlyTheme {
        ShopListScreen(
            state = state.state,
            snackbarHostState = rememberSnackbarHostState(),
            shops = shops,
            modifier = Modifier.fillMaxSize(),
            onClickAddShop = {},
            onClickEditShop = {},
            onAction = {},
            onClickBack = {},
        )
    }
}
