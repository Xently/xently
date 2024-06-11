package co.ke.xently.features.customers.presentation.list

import androidx.compose.runtime.Stable
import co.ke.xently.features.customers.data.domain.Customer

@Stable
internal data class CustomerListUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
    val currentUserRanking: Customer? = null,
)