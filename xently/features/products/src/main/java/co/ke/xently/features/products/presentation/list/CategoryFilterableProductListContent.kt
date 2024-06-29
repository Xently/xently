package co.ke.xently.features.products.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.presentation.list.components.ProductCategoriesLazyRow
import co.ke.xently.features.products.presentation.list.components.ProductListContent
import co.ke.xently.features.products.presentation.list.components.ProductListItem
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import kotlinx.coroutines.flow.flowOf

@Composable
fun CategoryFilterableProductListContent(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<ProductListViewModel>()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val products = viewModel.products.collectAsLazyPagingItems()

    CategoryFilterableProductListContent(
        modifier = modifier,
        categories = categories,
        products = products,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun CategoryFilterableProductListContent(
    products: LazyPagingItems<Product>,
    categories: List<ProductCategory>,
    modifier: Modifier = Modifier,
    onAction: (ProductListAction) -> Unit,
) {
    Column(modifier = modifier) {
        AnimatedVisibility(visible = categories.isNotEmpty()) {
            ProductCategoriesLazyRow(
                categories = categories,
                onAction = onAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        ProductListContent(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            products = products,
            onClickSelectShop = {},
            onClickSelectStore = {},
        ) { product ->
            if (product != null) {
                ProductListItem(product = product)
            } else {
                ProductListItem(
                    isLoading = true,
                    product = Product.DEFAULT,
                )
            }
        }
    }
}


@XentlyPreview
@Composable
private fun CategoryFilterableProductListContentPreview(
    @PreviewParameter(ProductListUiStateParameterProvider::class)
    state: ProductListScreenUiState,
) {
    val products = flowOf(state.products).collectAsLazyPagingItems()
    XentlyTheme {
        CategoryFilterableProductListContent(
            products = products,
            categories = state.categories,
            modifier = Modifier.fillMaxSize(),
            onAction = {},
        )
    }
}
