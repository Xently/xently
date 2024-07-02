package co.ke.xently.features.stores.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.qrcode.data.source.QrCodeRepository
import co.ke.xently.features.qrcode.presentation.utils.asUiText
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.domain.error.toError
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.features.stores.presentation.utils.asUiText
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.location.tracker.domain.LocationTracker
import co.ke.xently.libraries.location.tracker.presentation.utils.asUiText
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
import co.ke.xently.features.qrcode.data.domain.error.Result as QrCodeResult
import co.ke.xently.libraries.location.tracker.domain.error.Result as LocationTrackerResult


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class StoreDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StoreRepository,
    private val qrCodeRepository: QrCodeRepository,
    private val locationTracker: LocationTracker,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StoreDetailUiState())
    val uiState: StateFlow<StoreDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<StoreDetailEvent>()
    val event: Flow<StoreDetailEvent> = _event.receiveAsFlow()

    private val _isProcessingQrCode = Channel<Boolean>()
    val isProcessingQrCode: Flow<Boolean> = _isProcessingQrCode.receiveAsFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow("storeId", -1L)
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { throwable ->
                    val error = throwable.toError()
                    _event.send(
                        StoreDetailEvent.Error.Store(
                            error = error.asUiText(),
                            type = error
                        )
                    )
                    _uiState.update { it.copy(isLoading = false) }
                }
                .flatMapLatest(repository::findById)
                .collect { result ->
                    when (result) {
                        is Result.Failure -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.send(
                                StoreDetailEvent.Error.Store(
                                    error = result.error.asUiText(),
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
                        is LocationTrackerResult.Success -> {
                            _isProcessingQrCode.send(true)
                            val pointsUrl = _uiState.value.store!!
                                .links["qr-code"]!!
                                .hrefWithoutQueryParamTemplates()
                            getPointsAndReview(
                                pointsUrl = pointsUrl,
                                location = result.data,
                            )
                        }

                        is LocationTrackerResult.Failure -> {
                            _event.send(
                                StoreDetailEvent.Error.LocationTracker(
                                    error = result.error.asUiText(),
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
            is QrCodeResult.Success -> {
                _uiState.update {
                    it.copy(qrCodeScanResponse = result.data)
                }
            }

            is QrCodeResult.Failure -> {
                _event.send(
                    StoreDetailEvent.Error.QrCode(
                        error = result.error.asUiText(),
                        type = result.error,
                    )
                )
            }
        }
    }
}