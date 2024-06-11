package co.ke.xently.features.reviewcategory.presentation.edit

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory

internal sealed interface ReviewCategoryEditDetailAction {
    data object ClickSaveDetails : ReviewCategoryEditDetailAction
    class SelectCategory(val category: ReviewCategory) : ReviewCategoryEditDetailAction
    class RemoveCategory(val category: ReviewCategory) : ReviewCategoryEditDetailAction
    class ChangeName(val name: String) : ReviewCategoryEditDetailAction
}