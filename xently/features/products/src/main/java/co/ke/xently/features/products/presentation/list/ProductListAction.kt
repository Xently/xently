package co.ke.xently.features.products.presentation.list

import co.ke.xently.features.productcategory.data.domain.ProductCategory

internal sealed interface ProductListAction {
    class SelectCategory(val category: ProductCategory) : ProductListAction
    class RemoveCategory(val category: ProductCategory) : ProductListAction
    class ChangeQuery(val query: String) : ProductListAction
    class Search(val query: String) : ProductListAction
}