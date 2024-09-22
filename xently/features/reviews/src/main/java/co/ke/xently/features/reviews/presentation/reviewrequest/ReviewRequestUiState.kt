package co.ke.xently.features.reviews.presentation.reviewrequest

import androidx.compose.runtime.Stable
import co.ke.xently.libraries.data.core.UiText

@Stable
data class ReviewRequestUiState(
    val isLoading: Boolean = false,
    val categorySubStates: Map<String, SubState> = emptyMap(),
) {
    inline fun getUpdatedSubCategoryState(
        categoryName: String,
        update: SubState.() -> SubState,
    ): Map<String, SubState> {
        return buildMap {
            putAll(categorySubStates)
            val subState = categorySubStates[categoryName] ?: SubState()
            put(categoryName, subState.update())
        }
    }

    data class SubState(
        val isPosting: Boolean = false,
        val error: UiText? = null,
        val isEditRequested: Boolean = false,
        val message: String = "",
    )
}