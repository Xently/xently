package com.kwanzatukule.features.order.presentation.list

import com.kwanzatukule.features.order.presentation.utils.UiText
import com.kwanzatukule.libraries.location.tracker.domain.DirectionNavigation

sealed interface OrderListEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.order.domain.error.Error,
    ) : OrderListEvent

    data class FindNavigableLocations(
        val directionNavigation: DirectionNavigation,
    ) : OrderListEvent
}