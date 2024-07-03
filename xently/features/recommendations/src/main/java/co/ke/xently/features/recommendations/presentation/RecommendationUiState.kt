package co.ke.xently.features.recommendations.presentation

import androidx.compose.runtime.Stable
import co.ke.xently.features.recommendations.data.domain.error.LocalFieldError

@Stable
data class RecommendationUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
    val minimumPrice: String? = null,
    val maximumPrice: String? = null,
    val minimumPriceError: List<LocalFieldError>? = null,
    val maximumPriceError: List<LocalFieldError>? = null,
)