package co.ke.xently.features.stores.presentation.list.selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import co.ke.xently.features.shops.data.domain.error.ConfigurationError
import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.source.StoreCategoryRepository
import co.ke.xently.features.stores.data.domain.StoreFilters
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.domain.error.ShopSelectionRequiredException
import co.ke.xently.features.stores.data.source.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import co.ke.xently.features.shops.data.domain.error.Result as ShopResult


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class StoreSelectionListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StoreRepository,
    shopRepository: ShopRepository,
    storeCategoryRepository: StoreCategoryRepository,
) : ViewModel() {
    private companion object {
        private val KEY =
            StoreSelectionListViewModel::class.java.name.plus("SELECTED_STORE_CATEGORIES")
    }

    private val _uiState = MutableStateFlow(StoreSelectionListUiState())
    val uiState: StateFlow<StoreSelectionListUiState> = _uiState.asStateFlow()

    private val _event = Channel<StoreSelectionListEvent>()
    val event: Flow<StoreSelectionListEvent> = _event.receiveAsFlow()

    private val _selectedCategories = savedStateHandle.getStateFlow(KEY, emptySet<String>())

    val categories = storeCategoryRepository.getCategories()
        .combine(_selectedCategories) { categories, selectedCategories ->
            categories.map {
                it.copy(selected = selectedCategories.contains(it.name))
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        )

    private val _filters = MutableStateFlow(StoreFilters())

    val stores = shopRepository.findActivatedShop().flatMapLatest { result ->
        when (result) {
            is ShopResult.Failure -> {
                if (result.error == ConfigurationError.ShopSelectionRequired) {
                    throw ShopSelectionRequiredException()
                } else {
                    Timber.e(
                        "An unexpected error (%s) was encountered while fetching stores.",
                        result.error,
                    )
                    emptyFlow()
                }
            }

            is ShopResult.Success -> {
                _selectedCategories.combine(_filters) { categories, filters ->
                    filters.copy(
                        storeCategories = categories.map {
                            StoreCategory(name = it)
                        }.toSet(),
                    )
                }.flatMapLatest { filters ->
                    repository.getStores(
                        filters = filters,
                        url = result.data.links["stores"]!!.hrefWithoutQueryParamTemplates(),
                    )
                }
            }
        }
    }.cachedIn(viewModelScope)

    fun onAction(action: StoreSelectionListAction) {
        when (action) {
            is StoreSelectionListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
            }

            is StoreSelectionListAction.SelectCategory -> {
                val storeCategories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet())
                savedStateHandle[KEY] = storeCategories + action.category.name
            }

            is StoreSelectionListAction.RemoveCategory -> {
                val storeCategories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet())
                savedStateHandle[KEY] = storeCategories - action.category.name
            }

            is StoreSelectionListAction.Search -> {
                _filters.update { it.copy(query = action.query) }
            }

            is StoreSelectionListAction.DeleteStore -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                    when (val result = repository.deleteStore(store = action.store)) {
                        is Result.Failure -> {
                            _event.send(
                                StoreSelectionListEvent.Error(
                                    result.error.toUiText(),
                                    result.error
                                )
                            )
                        }

                        is Result.Success -> {
                            _event.send(StoreSelectionListEvent.Success(action))
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }

            is StoreSelectionListAction.SelectStore -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                    when (savedStateHandle.get<Operation>("operation")) {
                        Operation.CloneProducts -> cloneProducts(action)
                        else -> selectStore(action)
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private suspend fun cloneProducts(action: StoreSelectionListAction.SelectStore) {
        when (val result = repository.cloneProducts(store = action.store)) {
            is Result.Failure -> {
                _event.send(
                    StoreSelectionListEvent.Error(
                        result.error.toUiText(),
                        result.error
                    )
                )
            }

            is Result.Success -> {
                _event.send(StoreSelectionListEvent.Success(action))
            }
        }
    }

    private suspend fun selectStore(action: StoreSelectionListAction.SelectStore) {
        when (val result = repository.selectStore(store = action.store)) {
            is Result.Failure -> {
                _event.send(
                    StoreSelectionListEvent.Error(
                        result.error.toUiText(),
                        result.error
                    )
                )
            }

            is Result.Success -> {
                _event.send(StoreSelectionListEvent.Success(action))
            }
        }
    }
}