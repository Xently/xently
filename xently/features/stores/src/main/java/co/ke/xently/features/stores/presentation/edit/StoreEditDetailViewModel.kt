package co.ke.xently.features.stores.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.source.StoreCategoryRepository
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.features.stores.presentation.utils.asUiText
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.pagination.data.XentlyPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class StoreEditDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StoreRepository,
    private val storeCategoryRepository: StoreCategoryRepository,
) : ViewModel() {
    private companion object {
        private const val KEY = "co.ke.xently.features.stores.presentation.edit.SELECTED_STORE_CATEGORIES"
    }

    private val _uiState = MutableStateFlow(StoreEditDetailUiState())
    val uiState: StateFlow<StoreEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<StoreEditDetailEvent>()
    val event: Flow<StoreEditDetailEvent> = _event.receiveAsFlow()

    val categories: Flow<PagingData<StoreCategory>> =
        savedStateHandle.getStateFlow(KEY, emptyList<StoreCategory>())
            .flatMapLatest { selectedCategories ->
                Pager(
                    PagingConfig(
                        pageSize = 20,
                        initialLoadSize = 20,
                    )
                ) {
                    XentlyPagingSource { url ->
                        storeCategoryRepository.getCategories(url)
                    }
                }.flow.map { data ->
                    data.map { it.copy(selected = it in selectedCategories) }
                }
            }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            repository.findActiveStore().collect { store ->
                if (store != null) {
                    _uiState.update {
                        it.copy(store = store)
                    }
                    if (savedStateHandle.get<List<StoreCategory>>(KEY) == null) {
                        savedStateHandle[KEY] = store.categories
                    }
                }
            }
        }
    }

    fun onAction(action: StoreEditDetailAction) {
        when (action) {
            is StoreEditDetailAction.SelectCategory -> {
                val storeCategories = (savedStateHandle.get<List<StoreCategory>>(KEY)
                    ?: emptyList())

                savedStateHandle[KEY] = storeCategories + action.category
            }

            is StoreEditDetailAction.RemoveCategory -> {
                val storeCategories = (savedStateHandle.get<List<StoreCategory>>(KEY)
                    ?: emptyList())
                savedStateHandle[KEY] = storeCategories - action.category
            }

            is StoreEditDetailAction.ChangeCategoryName -> {
                _uiState.update {
                    it.copy(categoryName = action.name)
                }
            }

            is StoreEditDetailAction.ClickAddCategory -> {
                val storeCategories = (savedStateHandle.get<List<StoreCategory>>(KEY)
                    ?: emptyList())

                savedStateHandle[KEY] =
                    storeCategories + StoreCategory(name = _uiState.value.categoryName)
            }

            is StoreEditDetailAction.AddService -> {
                _uiState.update {
                    it.copy(services = it.services.plus(StoreService(action.service)))
                }
            }

            is StoreEditDetailAction.ChangeDescription -> {
                _uiState.update {
                    it.copy(description = action.description)
                }
            }

            is StoreEditDetailAction.ChangeEmailAddress -> {
                _uiState.update {
                    it.copy(email = action.email)
                }
            }

            is StoreEditDetailAction.ChangeName -> {
                _uiState.update {
                    it.copy(name = action.name)
                }
            }

            is StoreEditDetailAction.ChangeOpeningHour -> {

            }

            is StoreEditDetailAction.ChangeOpeningHourOpenStatus -> {

            }

            is StoreEditDetailAction.ChangePhoneNumber -> {
                _uiState.update {
                    it.copy(phone = action.phone)
                }
            }

            StoreEditDetailAction.ClickSaveDetails -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }
                    val store = state.store.copy(
                        name = state.name,
                        email = state.email,
                        telephone = state.phone,
                        services = state.services,
                        openingHours = state.openingHours,
                    )
                    when (val result = repository.save(store = store)) {
                        is Result.Failure -> {
                            _event.send(
                                StoreEditDetailEvent.Error(
                                    result.error.asUiText(),
                                    result.error,
                                )
                            )
                        }

                        is Result.Success -> {
                            // Sign-in can be requested from any screen after an access token is
                            // flagged as expired by the server. Therefore, instead of navigating
                            // to a dedicated screen, we should simply retain the screen the
                            // authentication was requested from, hence creating a good UX.
                            _event.send(StoreEditDetailEvent.Success)
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }

            is StoreEditDetailAction.ChangeOpeningHourTime -> {
                //TODO
            }
        }
    }
}