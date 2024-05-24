package com.kwanzatukule.features.customer.presentation.list

import androidx.compose.runtime.Stable
import com.kwanzatukule.libraries.data.route.domain.Route

@Stable
data class CustomerListUiState(
    val route: Route,
    val query: String = "",
    val isLoading: Boolean = false,
) {
}
