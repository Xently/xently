package co.ke.xently.features.stores.presentation.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.ConfigurationError
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ActiveStoreViewModel @Inject constructor(
    private val repository: StoreRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ActiveStoreUiState())
    val uiState: StateFlow<ActiveStoreUiState> = _uiState.asStateFlow()

    private val _event = Channel<ActiveStoreEvent>()
    val event: Flow<ActiveStoreEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.findActiveStore()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { result ->
                    when (result) {
                        is Result.Failure -> {
                            val event = result.getActiveStoreEvent()
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isShopSelected = event !is ActiveStoreEvent.SelectShop,
                                )
                            }
                            _event.send(event)
                        }

                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    store = result.data,
                                    canAddStore = result.data.shop.links.containsKey("add-store"),
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun Result.Failure<Store, ConfigurationError>.getActiveStoreEvent(): ActiveStoreEvent {
        return when (error) {
            ConfigurationError.ShopSelectionRequired -> ActiveStoreEvent.SelectShop
            ConfigurationError.StoreSelectionRequired -> ActiveStoreEvent.SelectShop
        }
    }

    fun onAction(action: ActiveStoreAction) {
    }
}