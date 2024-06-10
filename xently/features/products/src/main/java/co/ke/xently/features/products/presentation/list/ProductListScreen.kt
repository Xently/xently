package co.ke.xently.features.products.presentation.list

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
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.R
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.DataError
import co.ke.xently.features.products.presentation.components.ProductCategoryFilterChip
import co.ke.xently.features.products.presentation.list.components.ProductListItem
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.pagination.PaginatedLazyColumn
import co.ke.xently.libraries.ui.pagination.components.PaginatedContentLazyRow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    onClickAddProduct: () -> Unit,
) {
    val viewModel = hiltViewModel<ProductListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val products = viewModel.products.collectAsLazyPagingItems()
    val categories = viewModel.categories.collectAsLazyPagingItems()

    ProductListScreen(
        state = state,
        event = event,
        modifier = modifier,
        products = products,
        categories = categories,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
        onClickEditProduct = onClickEditProduct,
        onClickAddProduct = onClickAddProduct,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductListScreen(
    state: ProductListUiState,
    event: ProductListEvent?,
    products: LazyPagingItems<Product>,
    categories: LazyPagingItems<ProductCategory>,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAddProduct: () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    onAction: (ProductListAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is ProductListEvent.Error -> {
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
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_product_list)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                /*SearchBar(
                    query = state.query,
                    onSearch = { onAction(ProductListAction.Search(it)) },
                    onQueryChange = { onAction(ProductListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_products_placeholder),
                )*/

                if (categories.itemCount > 0) {
                    PaginatedContentLazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        items = categories,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) {
                        items(
                            categories.itemCount,
                            key = { categories[it]?.name ?: ">>>$it<<<" },
                        ) { index ->
                            val item = categories[index]
                            if (item != null) {
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
        PaginatedLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            items = products,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            emptyContentMessage = "No products found",
            prependErrorStateContent = {},
            appendErrorStateContent = {},
            errorStateContent = {},
        ) {
            items(
                products.itemCount,
                key = {
                    products[it]?.id
                        ?: (products.itemCount + it).toLong()
                },
            ) {
                val product = products[it]!!

                ProductListItem(
                    product = product,
                    onClickUpdate = { onClickEditProduct(product) },
                    onClickConfirmDelete = { onAction(ProductListAction.DeleteProduct(product)) },
                )
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
    val categories: PagingData<ProductCategory> = PagingData.from(
        List(10) {
            ProductCategory(
                name = "Category $it",
                selected = it < 2,
            )
        },
    ),
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
    val categories = flowOf(state.categories).collectAsLazyPagingItems()
    XentlyTheme {
        ProductListScreen(
            state = state.state,
            event = null,
            products = products,
            categories = categories,
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
            onClickAddProduct = {},
            onClickEditProduct = {},
        )
    }
}
