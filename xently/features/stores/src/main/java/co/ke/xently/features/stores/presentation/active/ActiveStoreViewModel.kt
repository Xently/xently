package co.ke.xently.features.stores.presentation.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.stores.data.source.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            repository.findActiveStore().collect { store ->
                _uiState.update {
                    it.copy(
                        store = store,
                        canAddStore = store?.shop?.links?.containsKey("add-store") ?: false,
                    )
                }
            }
        }
    }

    fun onAction(action: ActiveStoreAction) {
    }
}