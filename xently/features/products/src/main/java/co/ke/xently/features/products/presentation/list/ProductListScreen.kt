package co.ke.xently.features.products.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Button
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
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.R
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.ConfigurationError
import co.ke.xently.features.products.data.domain.error.DataError
import co.ke.xently.features.products.data.domain.error.toProductError
import co.ke.xently.features.products.presentation.components.ProductCategoryFilterChip
import co.ke.xently.features.products.presentation.list.components.ProductListEmptyState
import co.ke.xently.features.products.presentation.list.components.ProductListLazyColumn
import co.ke.xently.features.products.presentation.utils.asUiText
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
    onClickAddProduct: () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val viewModel = hiltViewModel<ProductListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val products = viewModel.products.collectAsLazyPagingItems()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    ProductListScreen(
        state = state,
        event = event,
        products = products,
        categories = categories,
        modifier = modifier,
        onClickSelectShop = onClickSelectShop,
        onClickSelectBranch = onClickSelectBranch,
        onClickAddProduct = onClickAddProduct,
        onClickEditProduct = onClickEditProduct,
        onAction = viewModel::onAction,
        topBar = topBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductListScreen(
    state: ProductListUiState,
    event: ProductListEvent?,
    products: LazyPagingItems<Product>,
    categories: List<ProductCategory>,
    modifier: Modifier = Modifier,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
    onClickAddProduct: () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    onAction: (ProductListAction) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is ProductListEvent.Error -> {
                snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                )
            }

            is ProductListEvent.Success -> {
                when (event.action) {
                    is ProductListAction.DeleteProduct -> {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.message_product_deleted),
                            duration = SnackbarDuration.Short,
                        )
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
                topBar()
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                /*SearchBar(
                    query = state.query,
                    onSearch = { onAction(ProductListAction.Search(it)) },
                    onQueryChange = { onAction(ProductListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_products_placeholder),
                )*/

                if (categories.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) {
                        items(categories, key = { it.name }) { item ->
                            ProductCategoryFilterChip(
                                item = item,
                                onClickSelectCategory = {
                                    onAction(ProductListAction.SelectCategory(item))
                                },
                                onClickRemoveCategory = {
                                    onAction(ProductListAction.RemoveCategory(item))
                                },
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onClickAddProduct,
                text = { Text(text = stringResource(R.string.action_add_product)) },
                icon = {
                    Icon(
                        Icons.Default.PostAdd,
                        contentDescription = stringResource(R.string.action_add_product),
                    )
                },
            )
        },
    ) { paddingValues ->
        val refreshLoadState = products.loadState.refresh
        val isRefreshing by remember(refreshLoadState, products.itemCount) {
            derivedStateOf {
                refreshLoadState == LoadState.Loading
                        && products.itemCount > 0
            }
        }
        PullRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = products::refresh,
        ) {
            when {
                products.itemCount == 0 && refreshLoadState is LoadState.NotLoading -> {
                    ProductListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = stringResource(R.string.message_no_products_found),
                        onClickRetry = products::refresh,
                    )
                }

                products.itemCount == 0 && refreshLoadState is LoadState.Error -> {
                    val error = remember(refreshLoadState) {
                        runBlocking { refreshLoadState.error.toProductError() }
                    }
                    ProductListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = error.asUiText().asString(),
                        canRetry = error is DataError.Network.Retryable,
                        onClickRetry = products::retry,
                    ) {
                        when (error as? ConfigurationError) {
                            null -> Unit
                            ConfigurationError.ShopSelectionRequired -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = onClickSelectShop) {
                                    Text(text = stringResource(R.string.action_select_shop))
                                }
                            }

                            ConfigurationError.StoreSelectionRequired -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = onClickSelectBranch) {
                                    Text(text = stringResource(R.string.action_select_store))
                                }
                            }
                        }
                    }
                }

                products.itemCount == 0 && refreshLoadState is LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }

                else -> {
                    ProductListLazyColumn(
                        products = products,
                        modifier = Modifier.matchParentSize(),
                        onClickEditProduct = onClickEditProduct,
                        onClickConfirmDelete = { onAction(ProductListAction.DeleteProduct(it)) },
                    )
                }
            }
        }
    }
}

private class ProductListScreenUiState(
    val state: ProductListUiState,
    val products: PagingData<Product> = PagingData.from(
        List(10) {
            Product(
                name = "Product $it",
                unitPrice = 1234.0,
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
            )
        },
    ),
    val categories: List<ProductCategory> = List(10) {
        ProductCategory(
            name = "Category $it",
            selected = it < 2,
        )
    },
)

private class ProductListUiStateParameterProvider :
    PreviewParameterProvider<ProductListScreenUiState> {
    override val values: Sequence<ProductListScreenUiState>
        get() = sequenceOf(
            ProductListScreenUiState(state = ProductListUiState()),
            ProductListScreenUiState(state = ProductListUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun ProductListScreenPreview(
    @PreviewParameter(ProductListUiStateParameterProvider::class)
    state: ProductListScreenUiState,
) {
    val products = flowOf(state.products).collectAsLazyPagingItems()
    XentlyTheme {
        ProductListScreen(
            state = state.state,
            event = null,
            products = products,
            categories = state.categories,
            modifier = Modifier.fillMaxSize(),
            onClickAddProduct = {},
            onClickEditProduct = {},
            onClickSelectShop = {},
            onClickSelectBranch = {},
            onAction = {},
        )
    }
}
