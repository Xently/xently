package com.kwanzatukule.features.route.presentation.entry

import com.kwanzatukule.libraries.data.route.presentation.utils.UiText

sealed interface RouteEntryEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.libraries.data.route.domain.error.Error,
    ) : RouteEntryEvent
}