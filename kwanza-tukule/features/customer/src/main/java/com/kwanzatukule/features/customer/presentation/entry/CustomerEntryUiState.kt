package com.kwanzatukule.features.customer.presentation.entry

import androidx.compose.runtime.Stable
import co.ke.xently.libraries.location.tracker.domain.Location
import com.kwanzatukule.libraries.data.route.domain.Route

@Stable
data class CustomerEntryUiState(
    val route: Route,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val query: String = "",
    val location: Location = Location(),
    val isLoading: Boolean = false,
) {
    @Stable
    val enableSaveButton: Boolean
        get() = name.isNotBlank()
                && email.isNotBlank()
                && phone.isNotBlank()
                && location.isUsable()
                && !isLoading
}
