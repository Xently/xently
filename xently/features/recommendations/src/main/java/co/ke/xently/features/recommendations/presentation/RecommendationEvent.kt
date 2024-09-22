package co.ke.xently.features.recommendations.presentation

import co.ke.xently.libraries.data.core.UiText


internal sealed interface RecommendationEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.recommendations.data.domain.error.Error,
    ) : RecommendationEvent

    data class Success(val action: RecommendationAction) : RecommendationEvent
}
