package co.ke.xently.features.recommendations.presentation

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import co.ke.xently.features.recommendations.R
import co.ke.xently.features.recommendations.data.domain.error.LocalFieldError
import co.ke.xently.libraries.location.tracker.domain.Location

@Stable
data class RecommendationUiState(
    val locationQuery: String = "",
    val isLoading: Boolean = false,
    val location: Location = Location(),
    val productName: String = "",
    val disableFields: Boolean = false,
    val minimumPrice: String? = null,
    val maximumPrice: String? = null,
    val minimumPriceError: List<LocalFieldError>? = null,
    val maximumPriceError: List<LocalFieldError>? = null,
    val shoppingList: List<String> = emptyList(),
) {
    val enableSearchButton: Boolean = location.isUsable()
            && !isLoading
            && !disableFields

    @StringRes
    val searchButtonLabel: Int = when {
        location.isUsable() -> R.string.action_label_search_place
        else -> R.string.action_label_pick_location
    }
}