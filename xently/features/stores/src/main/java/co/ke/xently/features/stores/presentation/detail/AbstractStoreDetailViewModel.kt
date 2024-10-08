package co.ke.xently.features.stores.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.qrcode.data.source.QrCodeRepository
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.domain.error.toError
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.location.tracker.domain.LocationTracker
import co.ke.xently.libraries.location.tracker.domain.error.Result.Failure
import co.ke.xently.libraries.location.tracker.domain.error.Result.Success
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

@OptIn(ExperimentalCoroutinesApi::class)
abstract class AbstractStoreDetailViewModel(
    protected val savedStateHandle: SavedStateHandle,
    protected val repository: StoreRepository,
    protected val qrCodeRepository: QrCodeRepository,
    protected val locationTracker: LocationTracker,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StoreDetailUiState())
    internal val uiState: StateFlow<StoreDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<StoreDetailEvent>()
    internal val event: Flow<StoreDetailEvent> = _event.receiveAsFlow()

    private val _isProcessingQrCode = Channel<Boolean>()
    val isProcessingQrCode: Flow<Boolean> = _isProcessingQrCode.receiveAsFlow()

    init {
        viewModelScope.launch {
            getStoreResultFlow()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { throwable ->
                    val error = throwable.toError()
                    _event.send(
                        StoreDetailEvent.Error.Store(
                            error = error.toUiText(),
                            type = error
                        )
                    )
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { result ->
                    when (result) {
                        is Result.Failure -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.send(
                                StoreDetailEvent.Error.Store(
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

    protected open fun getStoreResultFlow(): Flow<Result<Store, DataError>> {
        return savedStateHandle.getStateFlow("storeId", -1L)
            .flatMapLatest(repository::findById)
    }

    fun onAction(action: StoreDetailAction) {
        when (action) {
            is StoreDetailAction.DismissQrCodeProcessingDialog -> {
                viewModelScope.launch {
                    _isProcessingQrCode.send(false)
                }
            }

            is StoreDetailAction.GetPointsAndReview -> {
                viewModelScope.launch {
                    when (val result = locationTracker.getCurrentLocation()) {
                        is Success -> {
                            _isProcessingQrCode.send(true)
                            val pointsUrl = _uiState.value.store!!
                                .links["qr-code"]!!
                                .hrefWithoutQueryParamTemplates()
                            getPointsAndReview(
                                pointsUrl = pointsUrl,
                                location = result.data,
                            )
                        }

                        is Failure -> {
                            _event.send(
                                StoreDetailEvent.Error.LocationTracker(
                                    error = result.error.toUiText(),
                                    type = result.error,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getPointsAndReview(pointsUrl: String, location: Location) {
        when (val result = qrCodeRepository.getPointsAndReview(pointsUrl, location)) {
            is co.ke.xently.features.qrcode.data.domain.error.Result.Success -> {
                _uiState.update {
                    it.copy(qrCodeScanResponse = result.data)
                }
            }

            is co.ke.xently.features.qrcode.data.domain.error.Result.Failure -> {
                _event.send(
                    StoreDetailEvent.Error.QrCode(
                        error = result.error.toUiText(),
                        type = result.error,
                    )
                )
            }
        }
    }
}