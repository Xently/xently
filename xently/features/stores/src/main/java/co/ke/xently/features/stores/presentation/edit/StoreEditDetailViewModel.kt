package co.ke.xently.features.stores.presentation.edit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.source.StoreCategoryRepository
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.StoreDataValidator
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.data.core.Time
import com.dokar.chiptextfield.Chip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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
    private val dataValidator: StoreDataValidator,
) : ViewModel() {
    private companion object {
        private const val KEY =
            "co.ke.xently.features.stores.presentation.edit.SELECTED_STORE_CATEGORIES"
    }

    private val _uiState = MutableStateFlow(StoreEditDetailUiState())
    val uiState: StateFlow<StoreEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<StoreEditDetailEvent>()
    val event: Flow<StoreEditDetailEvent> = _event.receiveAsFlow()

    val categories: StateFlow<List<StoreCategory>> =
        savedStateHandle.getStateFlow(KEY, emptySet<StoreCategory>())
            .flatMapLatest { selectedCategories ->
                storeCategoryRepository.getCategories(null).map {
                    it.map { category ->
                        category.copy(selected = category in selectedCategories)
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            )

    init {
        viewModelScope.launch {
            repository.findActiveStore().collect { result ->
                when (result) {
                    is Result.Failure -> Unit
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(store = result.data)
                        }
                        if (savedStateHandle.get<Set<StoreCategory>>(KEY) == null) {
                            savedStateHandle[KEY] = result.data.categories
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onAction(action: StoreEditDetailAction) {
        when (action) {
            is StoreEditDetailAction.SelectCategory -> {
                val storeCategories = (savedStateHandle.get<Set<StoreCategory>>(KEY)
                    ?: emptySet())

                savedStateHandle[KEY] = storeCategories + action.category
            }

            is StoreEditDetailAction.RemoveCategory -> {
                val storeCategories = (savedStateHandle.get<Set<StoreCategory>>(KEY)
                    ?: emptySet())
                savedStateHandle[KEY] = storeCategories - action.category
            }

            is StoreEditDetailAction.ChangeCategoryName -> {
                _uiState.update {
                    it.copy(categoryName = action.name)
                }
            }

            is StoreEditDetailAction.ClickAddCategory -> {
                val storeCategories = (savedStateHandle.get<Set<StoreCategory>>(KEY)
                    ?: emptySet())

                savedStateHandle[KEY] =
                    storeCategories + StoreCategory(name = _uiState.value.categoryName)
            }

            is StoreEditDetailAction.AddService -> {
                _uiState.update {
                    it.copy(services = it.services + Chip(action.service))
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

            is StoreEditDetailAction.ChangeLocation -> {
                _uiState.update {
                    it.copy(locationString = action.location)
                }
            }

            is StoreEditDetailAction.ChangePhoneNumber -> {
                _uiState.update {
                    it.copy(phone = action.phone)
                }
            }

            is StoreEditDetailAction.ChangeOpeningHourOpenStatus -> {
                val (dayOfWeek, isOpen) = action.dayOfWeekIsOpen
                _uiState.update {
                    it.copy(
                        openingHours = it.openingHours.map { hour ->
                            if (hour.dayOfWeek == dayOfWeek) {
                                hour.copy(open = isOpen)
                            } else {
                                hour
                            }
                        },
                    )
                }
            }

            is StoreEditDetailAction.ChangeOpeningHourTime -> {
                val (dayOfWeek, openingHourTime) = action.dayOfWeekChangeOpeningHour
                _uiState.update {
                    it.copy(
                        openingHours = it.openingHours.map { hour ->
                            if (hour.dayOfWeek == dayOfWeek) {
                                val time = Time(
                                    hour = openingHourTime.state.hour,
                                    minute = openingHourTime.state.minute,
                                )
                                if (openingHourTime.isOpenTime) {
                                    hour.copy(openTime = time)
                                } else {
                                    hour.copy(closeTime = time)
                                }
                            } else {
                                hour
                            }
                        },
                    )
                }
            }

            StoreEditDetailAction.ClickSave, StoreEditDetailAction.ClickSaveAndAddAnother -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }
                    val store = validatedStore(state)

                    if (_uiState.value.isFormValid) {
                        when (val result = repository.save(store = store)) {
                            is Result.Success -> _event.send(StoreEditDetailEvent.Success(action))
                            is Result.Failure -> _event.send(StoreEditDetailEvent.Error(result.error))
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

    private fun validatedStore(state: StoreEditDetailUiState): Store {
        var store = state.store.copy(
            name = state.name,
            description = state.description.takeIf { it.isNotBlank() },
            openingHours = state.openingHours,
            services = state.services.map { StoreService(name = it.text) },
        )

        when (val result = dataValidator.validatedLocation(state.locationString)) {
            is Result.Failure -> _uiState.update { it.copy(locationError = result.error) }
            is Result.Success -> {
                _uiState.update { it.copy(locationError = null) }
                store = store.copy(location = result.data)
            }
        }

        when (val result = dataValidator.validatedEmail(state.email)) {
            is Result.Failure -> _uiState.update { it.copy(emailError = result.error) }
            is Result.Success -> {
                _uiState.update { it.copy(emailError = null) }
                store = store.copy(email = result.data)
            }
        }

        when (val result = dataValidator.validatedPhone(state.phone)) {
            is Result.Failure -> _uiState.update { it.copy(phoneError = result.error) }
            is Result.Success -> {
                _uiState.update { it.copy(phoneError = null) }
                store = store.copy(telephone = result.data)
            }
        }

        when (val result = dataValidator.validatedName(state.name)) {
            is Result.Failure -> _uiState.update { it.copy(nameError = result.error) }
            is Result.Success -> {
                _uiState.update { it.copy(nameError = null) }
                store = store.copy(name = result.data)
            }
        }

        return store
    }
}