package co.ke.xently.features.customers.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import co.ke.xently.features.customers.data.domain.CustomerFilters
import co.ke.xently.features.customers.data.source.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal open class CustomerScoreboardListViewModel @Inject constructor(
    private val repository: CustomerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CustomerListUiState())
    val uiState: StateFlow<CustomerListUiState> = _uiState.asStateFlow()

    private val _event = Channel<CustomerListEvent>()
    val event: Flow<CustomerListEvent> = _event.receiveAsFlow()

    private val _filters = MutableStateFlow(CustomerFilters())

    open val customers = getCustomerPagingDataFlow()

    protected fun getCustomerPagingDataFlow(dataUrl: String? = null) =
        _filters.flatMapLatest { filters ->
            val url = dataUrl ?: repository.getCustomersUrl()
            repository.getCustomers(url = url, filters = filters)
        }.cachedIn(viewModelScope)

    fun onAction(action: CustomerListAction) {
        when (action) {
            is CustomerListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
            }

            is CustomerListAction.Search -> {
                _filters.update { it.copy(query = action.query) }
            }
        }
    }
}