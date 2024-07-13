package co.ke.xently.features.products.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.R
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.presentation.list.components.ManipulatableProductListItem
import co.ke.xently.features.products.presentation.list.components.ProductCategoriesLazyRow
import co.ke.xently.features.products.presentation.list.components.ProductListContent
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.SearchBar
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import kotlinx.coroutines.flow.flowOf

@Composable
fun ActiveStoreProductListScreen(
    modifier: Modifier = Modifier,
    onClickAddProduct: () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val viewModel = hiltViewModel<ActiveStoreProductListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val products = viewModel.products.collectAsLazyPagingItems()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
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
    }

    ActiveStoreProductListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        products = products,
        categories = categories,
        modifier = modifier,
        onClickAddProduct = onClickAddProduct,
        onClickEditProduct = onClickEditProduct,
        onAction = viewModel::onAction,
        topBar = topBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ActiveStoreProductListScreen(
    state: ProductListUiState,
    snackbarHostState: SnackbarHostState,
    products: LazyPagingItems<Product>,
    categories: List<ProductCategory>,
    modifier: Modifier = Modifier,
    onClickAddProduct: () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    onAction: (ProductListAction) -> Unit,
    topBar: @Composable () -> Unit = {},
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

                SearchBar(
                    query = state.query,
                    onSearch = { onAction(ProductListAction.Search(it)) },
                    onQueryChange = { onAction(ProductListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_products_placeholder),
                )

                if (categories.isNotEmpty()) {
                    ProductCategoriesLazyRow(
                        categories = categories,
                        onAction = onAction,
                        modifier = Modifier.fillMaxWidth(),
                    )
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
        val eventHandler = LocalEventHandler.current

        ProductListContent(
            products = products,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onClickSelectShop = eventHandler::requestShopSelection,
            onClickSelectStore = eventHandler::requestStoreSelection,
        ) { product ->
            if (product != null) {
                ManipulatableProductListItem(
                    product = product,
                    onClickUpdate = { onClickEditProduct(product) },
                    onClickConfirmDelete = { onAction(ProductListAction.DeleteProduct(product)) },
                )
            } else {
                ManipulatableProductListItem(
                    product = Product.DEFAULT,
                    isLoading = true,
                    onClickUpdate = {},
                    onClickConfirmDelete = {},
                )
            }
        }
    }
}


internal class ProductListScreenUiState(
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

internal class ProductListUiStateParameterProvider :
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
        ActiveStoreProductListScreen(
            state = state.state,
            snackbarHostState = rememberSnackbarHostState(),
            products = products,
            categories = state.categories,
            modifier = Modifier.fillMaxSize(),
            onClickAddProduct = {},
            onClickEditProduct = {},
            onAction = {},
        )
    }
}
