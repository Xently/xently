package co.ke.xently.business.landing.presentation

import co.ke.xently.features.auth.presentation.utils.UiText


sealed interface LandingEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.auth.data.domain.error.Error,
    ) : LandingEvent

    data object Success : LandingEvent
}