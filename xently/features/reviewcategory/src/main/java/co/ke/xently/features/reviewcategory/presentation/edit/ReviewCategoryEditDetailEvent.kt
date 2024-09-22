package co.ke.xently.features.reviewcategory.presentation.edit

import co.ke.xently.libraries.data.core.UiText


internal sealed interface ReviewCategoryEditDetailEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.reviewcategory.data.domain.error.Error,
    ) : ReviewCategoryEditDetailEvent

    data class Success(val action: ReviewCategoryEditDetailAction) : ReviewCategoryEditDetailEvent
}
