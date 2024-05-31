package com.kwanzatukule.features.route.presentation.list

import com.kwanzatukule.libraries.data.route.presentation.utils.UiText

sealed interface RouteListEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.libraries.data.route.domain.error.Error,
    ) : RouteListEvent
}