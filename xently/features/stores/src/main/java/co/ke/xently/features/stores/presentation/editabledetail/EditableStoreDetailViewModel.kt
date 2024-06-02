package co.ke.xently.features.stores.presentation.editabledetail

import androidx.lifecycle.ViewModel
import co.ke.xently.features.stores.data.source.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
internal class EditableStoreDetailViewModel @Inject constructor(
    private val repository: StoreRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditableStoreDetailUiState())
    val uiState: StateFlow<EditableStoreDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<EditableStoreDetailEvent>()
    val event: Flow<EditableStoreDetailEvent> = _event.receiveAsFlow()
    fun onAction(action: EditableStoreDetailAction) {
    }
}