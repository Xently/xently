package com.kwanzatukule.features.customer.complaints.presentation.entry

import com.kwanzatukule.features.customer.complaints.presentation.utils.UiText

sealed interface CustomerComplaintEntryEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.customer.complaints.domain.error.Error,
    ) : CustomerComplaintEntryEvent
}