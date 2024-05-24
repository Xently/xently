package com.kwanzatukule.features.order.presentation

import com.kwanzatukule.features.order.presentation.utils.UiText

sealed interface OrderSummaryEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.order.domain.error.Error,
    ) : OrderSummaryEvent
}