package co.ke.xently.features.stores.presentation.edit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.source.StoreCategoryRepository
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.StoreDataValidator
import co.ke.xently.features.stores.data.domain.StorePaymentMethod
import co.ke.xently.features.stores.data.domain.error.RemoteFieldError
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClient
import co.ke.xently.libraries.location.tracker.domain.Location
import com.dokar.chiptextfield.Chip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.map

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class StoreEditDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StoreRepository,
    storeCategoryRepository: StoreCategoryRepository,
    private val dataValidator: StoreDataValidator,
    private val webSocketClient: StompWebSocketClient,
) : ViewModel() {
    private companion object {
        private val KEY =
            StoreEditDetailViewModel::class.java.name.plus("SELECTED_STORE_CATEGORIES")
    }

    private val _uiState = MutableStateFlow(StoreEditDetailUiState())
    val uiState: StateFlow<StoreEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<StoreEditDetailEvent>()
    val event: Flow<StoreEditDetailEvent> = _event.receiveAsFlow()

    val storeServicesSearchSuggestions = webSocketClient.watch {
        val destination = "/user/queue/type-ahead.store-services"
//        val destination = "/queue/type-ahead.store-services"
        Timber.tag(StompWebSocketClient.TAG).d("Subscribing to: $destination")
        subscribe<List<String>>(destination = destination)
    }

    val storeCategoriesSearchSuggestions = webSocketClient.watch {
        val destination = "/user/queue/type-ahead.store-categories"
//        val destination = "/queue/type-ahead.store-categories"
        Timber.tag(StompWebSocketClient.TAG).d("Subscribing to: $destination")
        subscribe<List<String>>(destination = destination)
    }

    val storePaymentMethodsSearchSuggestions = webSocketClient.watch {
        val destination = "/user/queue/type-ahead.store-payment-methods"
//        val destination = "/queue/type-ahead.store-payment-methods"
        Timber.tag(StompWebSocketClient.TAG).d("Subscribing to: $destination")
        subscribe<List<String>>(destination = destination)
    }

    val categories = storeCategoryRepository.getCategories().combine(
        savedStateHandle.getStateFlow(
            KEY,
            emptySet<String>(),
        )
    ) { categories, selectedCategories ->
        categories.map {
            it.copy(selected = selectedCategories.contains(it.name))
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
    )

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<Long>("storeId", -1)
                .flatMapLatest(repository::findById)
                .onStart { _uiState.update { it.copy(isLoading = true, disableFields = true) } }
                .onCompletion {
                    _uiState.update { it.copy(isLoading = false, disableFields = false) }
                }
                .collect { result ->
                    _uiState.update {
                        when (result) {
                            is Result.Failure -> StoreEditDetailUiState()
                            is Result.Success -> {
                                savedStateHandle[KEY] =
                                    result.data.categories.map { it.name }.toSet()
                                StoreEditDetailUiState(store = result.data)
                            }
                        }
                    }
                }
        }
    }

    private var storeServicesSearchSuggestionsJob: Job? = null

    private var storeCategoriesSearchSuggestionsJob: Job? = null

    private var storePaymentMethodsSearchSuggestionsJob: Job? = null

    @OptIn(ExperimentalMaterial3Api::class)
    fun onAction(action: StoreEditDetailAction) {
        when (action) {
            is StoreEditDetailAction.ClearFieldsForNewStore -> {
                onAction(StoreEditDetailAction.ChangeName(""))
                onAction(StoreEditDetailAction.ChangeLocation(Location()))
                onAction(StoreEditDetailAction.ChangeEmailAddress(""))
                onAction(StoreEditDetailAction.ChangePhoneNumber(""))
                onAction(StoreEditDetailAction.ChangeDescription(""))
                _uiState.update {
                    it.copy(
                        store = Store(),
                        emailError = null,
                        nameError = null,
                        phoneError = null,
                        locationError = null,
                        descriptionError = null,
                    )
                }
            }

            is StoreEditDetailAction.SelectCategory -> {
                val storeCategories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet())
                savedStateHandle[KEY] = storeCategories + action.category.name
            }

            is StoreEditDetailAction.RemoveCategory -> {
                val storeCategories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet())
                savedStateHandle[KEY] = storeCategories - action.category.name
            }

            is StoreEditDetailAction.AddService -> {
                _uiState.update {
                    it.copy(services = it.services + Chip(action.service))
                }
            }

            is StoreEditDetailAction.RemoveService -> {
                _uiState.update {
                    it.copy(services = it.services - action.service)
                }
            }

            is StoreEditDetailAction.OnServiceQueryChange -> {
                storeServicesSearchSuggestionsJob?.cancel()
                storeServicesSearchSuggestionsJob = viewModelScope.launch {
                    webSocketClient.sendMessage {
                        convertAndSend(
                            destination = "/app/type-ahead.store-services",
                            body = co.ke.xently.libraries.data.core.TypeAheadSearchRequest(query = action.query),
                        )
                    }
                }
            }

            is StoreEditDetailAction.OnCategoryQueryChange -> {
                storeCategoriesSearchSuggestionsJob?.cancel()
                storeCategoriesSearchSuggestionsJob = viewModelScope.launch {
                    webSocketClient.sendMessage {
                        convertAndSend(
                            destination = "/app/type-ahead.store-categories",
                            body = co.ke.xently.libraries.data.core.TypeAheadSearchRequest(query = action.query),
                        )
                    }
                }
            }

            is StoreEditDetailAction.AddPaymentMethod -> {
                _uiState.update {
                    it.copy(paymentMethods = it.paymentMethods + Chip(action.paymentMethod))
                }
            }

            is StoreEditDetailAction.RemovePaymentMethod -> {
                _uiState.update {
                    it.copy(paymentMethods = it.paymentMethods - action.paymentMethod)
                }
            }

            is StoreEditDetailAction.OnPaymentMethodQueryChange -> {
                storePaymentMethodsSearchSuggestionsJob?.cancel()
                storePaymentMethodsSearchSuggestionsJob = viewModelScope.launch {
                    webSocketClient.sendMessage {
                        convertAndSend(
                            destination = "/app/type-ahead.store-payment-methods",
                            body = co.ke.xently.libraries.data.core.TypeAheadSearchRequest(query = action.query),
                        )
                    }
                }
            }

            is StoreEditDetailAction.AddAdditionalCategory -> {
                _uiState.update {
                    it.copy(additionalCategories = it.additionalCategories + Chip(action.category))
                }
            }

            is StoreEditDetailAction.RemoveAdditionalCategory -> {
                _uiState.update {
                    it.copy(additionalCategories = it.additionalCategories - action.category)
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
                    it.copy(
                        location = action.location,
                        locationString = action.location.takeIf(Location::isUsable)
                            ?.coordinatesString() ?: "",
                    )
                }
            }

            is StoreEditDetailAction.ChangeLocationString -> {
                when (val result = dataValidator.validatedLocation(action.location)) {
                    is Result.Failure -> _uiState.update {
                        it.copy(
                            location = Location(),
                            locationString = action.location,
                            locationError = listOf(result.error),
                        )
                    }

                    is Result.Success -> _uiState.update {
                        it.copy(
                            locationError = null,
                            location = result.data,
                            locationString = action.location,
                        )
                    }
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
                            if (hour.dayOfWeek != dayOfWeek) {
                                hour
                            } else {
                                val time = Time(
                                    hour = openingHourTime.state.hour,
                                    minute = openingHourTime.state.minute,
                                )
                                if (openingHourTime.isOpenTime) {
                                    hour.copy(openTime = time)
                                } else {
                                    hour.copy(closeTime = time)
                                }
                            }
                        },
                    )
                }
            }

            StoreEditDetailAction.ClickSave, StoreEditDetailAction.ClickSaveAndAddAnother -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(
                            isLoading = true,
                            emailError = null,
                            nameError = null,
                            phoneError = null,
                            locationError = null,
                            descriptionError = null,
                        )
                    }
                    val store = validatedStore(state)

                    if (_uiState.value.isFormValid) {
                        val addStoreUrl = savedStateHandle.get<String?>("addStoreUrl")
                        when (val result =
                            repository.save(store = store, addStoreUrl = addStoreUrl)) {
                            is Result.Success -> _event.send(StoreEditDetailEvent.Success(action))
                            is Result.Failure -> {
                                when (val error = result.error) {
                                    is RemoteFieldError -> {
                                        _uiState.update {
                                            it.copy(
                                                locationError = error.errors["location"]
                                                    ?: emptyList(),
                                                descriptionError = error.errors["description"]
                                                    ?: emptyList(),
                                                emailError = error.errors["email"] ?: emptyList(),
                                                phoneError = error.errors["telephone"]
                                                    ?: emptyList(),
                                                nameError = error.errors["name"] ?: emptyList(),
                                            )
                                        }
                                    }

                                    else -> _event.send(StoreEditDetailEvent.Error(error))
                                }
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

    private fun validatedStore(state: StoreEditDetailUiState): Store {
        var store = state.store.copy(
            name = state.name,
            services = state.services.map { StoreService(it.text) },
            paymentMethods = state.paymentMethods.map { StorePaymentMethod(it.text) },
            openingHours = state.openingHours,
            description = state.description.trim().takeIf { it.isNotBlank() },
            categories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet()).map {
                StoreCategory(name = it)
            } + state.additionalCategories.map { StoreCategory(name = it.text) },
        )

        when (val result = dataValidator.validatedLocation(state.locationString)) {
            is Result.Failure -> _uiState.update { it.copy(locationError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(locationError = null) }
                store = store.copy(location = result.data)
            }
        }

        when (val result = dataValidator.validatedEmail(state.email)) {
            is Result.Failure -> _uiState.update { it.copy(emailError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(emailError = null) }
                store = store.copy(email = result.data)
            }
        }

        when (val result = dataValidator.validatedPhone(state.phone)) {
            is Result.Failure -> _uiState.update { it.copy(phoneError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(phoneError = null) }
                store = store.copy(telephone = result.data)
            }
        }

        when (val result = dataValidator.validatedName(state.name)) {
            is Result.Failure -> _uiState.update { it.copy(nameError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(nameError = null) }
                store = store.copy(name = result.data)
            }
        }

        return store
    }
}