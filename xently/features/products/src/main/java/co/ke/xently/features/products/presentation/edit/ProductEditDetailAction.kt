package co.ke.xently.features.products.presentation.edit

import co.ke.xently.features.productcategory.data.domain.ProductCategory

internal sealed interface ProductEditDetailAction {
    data object ClickSaveDetails : ProductEditDetailAction
    class ChangeCategoryName(val name: String) : ProductEditDetailAction
    class SelectCategory(val category: ProductCategory) : ProductEditDetailAction
    class RemoveCategory(val category: ProductCategory) : ProductEditDetailAction
    class ChangeName(val name: String) : ProductEditDetailAction
    class ChangeUnitPrice(val unitPrice: String) : ProductEditDetailAction
    class ChangeDescription(val description: String) : ProductEditDetailAction
}