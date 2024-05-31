package com.kwanzatukule.features.customer.presentation.list

import com.kwanzatukule.libraries.data.customer.presentation.utils.UiText

sealed interface CustomerListEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.libraries.data.customer.domain.error.Error,
    ) : CustomerListEvent
}