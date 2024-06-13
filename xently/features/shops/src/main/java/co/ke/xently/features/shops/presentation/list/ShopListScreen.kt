package co.ke.xently.features.shops.presentation.list

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
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import co.ke.xently.features.shops.R
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.error.DataError
import co.ke.xently.features.shops.presentation.list.components.ShopListItem
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.pagination.PaginatedLazyColumn
import kotlinx.coroutines.flow.flowOf

@Composable
fun ShopListScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAddShop: () -> Unit,
    onClickEditShop: (Shop) -> Unit,
    onShopSelected: (Shop) -> Unit,
) {
    val viewModel = hiltViewModel<ShopListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val shops = viewModel.shops.collectAsLazyPagingItems()

    ShopListScreen(
        state = state,
        event = event,
        shops = shops,
        modifier = modifier,
        onClickAddShop = onClickAddShop,
        onClickEditShop = onClickEditShop,
        onAction = viewModel::onAction,
        onClickBack = onClickBack,
        onShopSelected = onShopSelected,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShopListScreen(
    state: ShopListUiState,
    event: ShopListEvent?,
    shops: LazyPagingItems<Shop>,
    modifier: Modifier = Modifier,
    onClickAddShop: () -> Unit,
    onClickEditShop: (Shop) -> Unit,
    onAction: (ShopListAction) -> Unit,
    onClickBack: () -> Unit,
    onShopSelected: (Shop) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is ShopListEvent.Error -> {
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

            is ShopListEvent.Success -> {
                when (event.action) {
                    is ShopListAction.DeleteShop -> {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.message_shop_deleted),
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is ShopListAction.SelectShop -> {
                        onShopSelected(event.action.shop)
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
                    title = { Text(text = stringResource(R.string.top_bar_title_select_shop)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                /*SearchBar(
                    query = state.query,
                    onSearch = { onAction(ShopListAction.Search(it)) },
                    onQueryChange = { onAction(ShopListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_shops_placeholder),
                )*/
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onClickAddShop,
                text = { Text(text = stringResource(R.string.action_add_shop)) },
                icon = {
                    Icon(
                        Icons.Default.PostAdd,
                        contentDescription = stringResource(R.string.action_add_shop),
                    )
                },
            )
        },
    ) { paddingValues ->
        PaginatedLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            items = shops,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            emptyContentMessage = "No shops found",
            prependErrorStateContent = {},
            appendErrorStateContent = {},
            errorStateContent = {},
        ) {
            items(
                shops.itemCount,
                key = {
                    shops[it]?.id
                        ?: (shops.itemCount + it).toLong()
                },
            ) {
                val shop = shops[it]!!

                ShopListItem(
                    shop = shop,
                    onClickUpdate = { onClickEditShop(shop) },
                    onClickConfirmDelete = { onAction(ShopListAction.DeleteShop(shop)) },
                    modifier = Modifier.clickable {
                        onAction(ShopListAction.SelectShop(shop))
                    },
                )
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

private class ShopListUiStateParameterProvider :
    PreviewParameterProvider<ShopListScreenUiState> {
    override val values: Sequence<ShopListScreenUiState>
        get() = sequenceOf(
            ShopListScreenUiState(state = ShopListUiState()),
            ShopListScreenUiState(state = ShopListUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun ShopListScreenPreview(
    @PreviewParameter(ShopListUiStateParameterProvider::class)
    state: ShopListScreenUiState,
) {
    val shops = flowOf(state.shops).collectAsLazyPagingItems()
    XentlyTheme {
        ShopListScreen(
            state = state.state,
            event = null,
            shops = shops,
            modifier = Modifier.fillMaxSize(),
            onClickAddShop = {},
            onClickEditShop = {},
            onAction = {},
            onClickBack = {},
            onShopSelected = {},
        )
    }
}
