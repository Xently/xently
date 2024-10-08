package co.ke.xently.features.stores.presentation.moredetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.domain.error.toError
import co.ke.xently.features.stores.data.source.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class MoreDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StoreRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MoreDetailsUiState())
    val uiState: StateFlow<MoreDetailsUiState> = _uiState.asStateFlow()

    private val _event = Channel<MoreDetailsEvent>()
    val event: Flow<MoreDetailsEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow("storeId", -1L)
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { throwable ->
                    val error = throwable.toError()
                    _event.send(MoreDetailsEvent.Error(error = error.toUiText(), type = error))
                    _uiState.update { it.copy(isLoading = false) }
                }
                .flatMapLatest(repository::findById)
                .collect { result ->
                    when (result) {
                        is Result.Failure -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.send(
                                MoreDetailsEvent.Error(
                                    error = result.error.toUiText(),
                                    type = result.error,
                                )
                            )
                        }

                        is Result.Success -> {
                            _uiState.update {
                                it.copy(store = result.data, isLoading = false)
                            }
                        }
                    }
                }
        }
    }

    fun onAction(action: MoreDetailsAction) {

    }
}