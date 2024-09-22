package co.ke.xently.features.customers.presentation.list

import co.ke.xently.libraries.data.core.UiText


internal sealed interface CustomerListEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.customers.data.domain.error.Error,
    ) : CustomerListEvent

    data class Success(val action: CustomerListAction) : CustomerListEvent
}
