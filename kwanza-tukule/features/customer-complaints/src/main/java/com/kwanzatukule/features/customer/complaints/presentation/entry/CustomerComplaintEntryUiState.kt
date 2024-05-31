package com.kwanzatukule.features.customer.complaints.presentation.entry

import androidx.compose.runtime.Stable

@Stable
data class CustomerComplaintEntryUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val query: String = "",
    val isLoading: Boolean = false,
) {
    @Stable
    val enableSaveButton: Boolean
        get() = name.isNotBlank()
                && email.isNotBlank()
                && phone.isNotBlank()
                && !isLoading
}
