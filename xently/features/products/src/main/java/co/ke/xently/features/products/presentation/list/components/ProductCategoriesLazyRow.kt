package co.ke.xently.features.products.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.presentation.components.ProductCategoryFilterChip
import co.ke.xently.features.products.presentation.list.ProductListAction

@Composable
internal fun ProductCategoriesLazyRow(
    categories: List<ProductCategory>,
    modifier: Modifier = Modifier,
    onAction: (ProductListAction) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(categories, key = { it.name }) { item ->
            ProductCategoryFilterChip(
                category = item,
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