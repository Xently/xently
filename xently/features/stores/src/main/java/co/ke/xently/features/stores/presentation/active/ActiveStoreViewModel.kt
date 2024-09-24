package co.ke.xently.features.stores.presentation.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.ConfigurationError
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.libraries.data.image.domain.Upload
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
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
                                    images = result.data.images,
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
        when (action) {
            is ActiveStoreAction.ProcessImageData -> {
                val newImage = action.image
                _uiState.update {
                    val images = if (it.images.firstOrNull() !is Upload) {
                        it.images.mapIndexed { index, image ->
                            if (index == 0) newImage else image
                        }
                    } else listOf(newImage) + it.images
                    it.copy(images = images)
                }

                if (newImage !is UploadRequest) return

                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true, isImageUploading = true)
                    }

                    val uploadUrl =
                        state.store?.links?.get("images")?.hrefWithoutQueryParamTemplates()

                    if (!uploadUrl.isNullOrBlank()) {
                        when (val result = repository.uploadNewImage(uploadUrl, newImage)) {
                            is Result.Success -> {
                                _event.send(ActiveStoreEvent.Success(action))
                            }

                            is Result.Failure -> {
                                _event.send(
                                    ActiveStoreEvent.Error(
                                        error = result.error.toUiText(),
                                        type = result.error,
                                    )
                                )
                            }
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false, isImageUploading = false)
                    }
                }
            }

            is ActiveStoreAction.ProcessImageUpdateData -> {
                val (position, newImage) = action.data
                _uiState.update {
                    it.copy(
                        images = it.images.mapIndexed { index, image ->
                            if (index == position) newImage else image
                        },
                    )
                }

                if (newImage !is UploadRequest) return

                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true, isImageUploading = true)
                    }
                    val imageToDelete = state.store?.images?.getOrNull(position)

                    if (imageToDelete is UploadResponse) {
                        when (val result = repository.updateImage(imageToDelete, newImage)) {
                            is Result.Success -> {
                                _event.send(ActiveStoreEvent.Success(action))
                            }

                            is Result.Failure -> {
                                _event.send(
                                    ActiveStoreEvent.Error(
                                        error = result.error.toUiText(),
                                        type = result.error,
                                    )
                                )
                            }
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false, isImageUploading = false)
                    }
                }
            }

            is ActiveStoreAction.RemoveImageAtPosition -> {
                /*_uiState.update {
                    it.copy(
                        images = it.images.mapIndexed { index, image ->
                            if (index == action.position) null else image
                        }.filterNotNull(),
                    )
                }*/

                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }
                    val imageToDelete = state.images.getOrNull(action.position)

                    if (imageToDelete is UploadResponse) {
                        when (val result = repository.removeImage(image = imageToDelete)) {
                            is Result.Success -> {
                                _event.send(ActiveStoreEvent.Success(action))
                            }

                            is Result.Failure -> {
                                _event.send(
                                    ActiveStoreEvent.Error(
                                        error = result.error.toUiText(),
                                        type = result.error,
                                    )
                                )
                            }
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }
}