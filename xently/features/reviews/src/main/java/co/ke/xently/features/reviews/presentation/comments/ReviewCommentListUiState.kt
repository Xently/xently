package co.ke.xently.features.reviews.presentation.comments

import androidx.compose.runtime.Stable
import co.ke.xently.features.reviews.domain.Star

@Stable
internal data class ReviewCommentListUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
    val stars: List<Star> = List(5) { Star(number = it + 1, selected = false) },
)