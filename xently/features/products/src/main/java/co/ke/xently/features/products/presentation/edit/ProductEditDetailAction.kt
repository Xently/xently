package co.ke.xently.features.products.presentation.edit

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.libraries.data.image.domain.File

internal sealed interface ProductEditDetailAction {
    data object ClickSave : ProductEditDetailAction
    data object ClickSaveAndAddAnother : ProductEditDetailAction
    data object ClickAddCategory : ProductEditDetailAction
    data object ClearFieldsForNewProduct : ProductEditDetailAction
    class ChangeCategoryName(val name: String) : ProductEditDetailAction
    class SelectCategory(val category: ProductCategory) : ProductEditDetailAction
    class RemoveCategory(val category: ProductCategory) : ProductEditDetailAction
    class ChangeName(val name: String) : ProductEditDetailAction
    class ChangeUnitPrice(val unitPrice: String) : ProductEditDetailAction
    class ChangeDescription(val description: String) : ProductEditDetailAction
    class ProcessImageData(val data: Pair<Int, File?>) : ProductEditDetailAction
    class RemoveImageAtPosition(val position: Int) : ProductEditDetailAction
}