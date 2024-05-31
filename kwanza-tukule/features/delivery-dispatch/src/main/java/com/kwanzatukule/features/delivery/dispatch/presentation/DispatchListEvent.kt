package com.kwanzatukule.features.delivery.dispatch.presentation

import com.kwanzatukule.features.delivery.dispatch.presentation.utils.UiText

sealed interface DispatchListEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.delivery.dispatch.domain.error.Error,
    ) : DispatchListEvent
}