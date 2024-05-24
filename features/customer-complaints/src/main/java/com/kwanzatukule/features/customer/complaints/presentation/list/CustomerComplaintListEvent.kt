package com.kwanzatukule.features.customer.complaints.presentation.list

import com.kwanzatukule.features.customer.complaints.presentation.utils.UiText

sealed interface CustomerComplaintListEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.customer.complaints.domain.error.Error,
    ) : CustomerComplaintListEvent
}