package com.kwanzatukule.features.customer.presentation.entry

import com.kwanzatukule.libraries.data.customer.presentation.utils.UiText

sealed interface CustomerEntryEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.libraries.data.customer.domain.error.Error,
    ) : CustomerEntryEvent
}