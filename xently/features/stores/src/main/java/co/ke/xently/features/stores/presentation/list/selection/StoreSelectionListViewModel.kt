package co.ke.xently.features.stores.presentation.list.selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.features.shops.data.domain.error.ConfigurationError
import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.source.StoreCategoryRepository
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.StoreFilters
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.domain.error.ShopSelectionRequiredException
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.features.stores.presentation.utils.asUiText
import co.ke.xently.libraries.pagination.data.PagedResponse
import co.ke.xently.libraries.pagination.data.XentlyPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import co.ke.xently.features.shops.data.domain.error.Result as ShopResult


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class StoreSelectionListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StoreRepository,
    shopRepository: ShopRepository,
    private val storeCategoryRepository: StoreCategoryRepository,
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

    val categories: StateFlow<List<StoreCategory>> =
        _selectedCategories.flatMapLatest { selectedCategories ->
            storeCategoryRepository.getCategories(null).map { categories ->
                (selectedCategories.map {
                    StoreCategory(name = it, selected = true)
                }.sortedBy { it.name } + categories).distinctBy { it.name }
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        )

    private val _filters = MutableStateFlow(StoreFilters())

    val stores: Flow<PagingData<Store>> =
        shopRepository.findActivatedShop().flatMapLatest { result ->
            when (result) {
                is ShopResult.Failure -> {
                    pager {
                        when (result.error) {
                            ConfigurationError.ShopSelectionRequired -> throw ShopSelectionRequiredException()
                        }
                    }.flow
                }

                is ShopResult.Success -> {
                    _selectedCategories.combine(_filters) { categories, filters ->
                        filters.copy(
                            storeCategories = categories.map {
                                StoreCategory(name = it)
                            }.toSet(),
                        )
                    }.flatMapLatest { filters ->
                        pager { url ->
                            repository.getStores(
                                filters = filters,
                                url = url
                                    ?: result.data.links["stores"]!!.hrefWithoutQueryParamTemplates(),
                            )
                        }.flow
                    }
                }
            }
        }.cachedIn(viewModelScope)

    private fun pager(call: suspend (String?) -> PagedResponse<Store>) =
        Pager(PagingConfig(pageSize = 20)) {
            XentlyPagingSource(apiCall = call)
        }

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
                                    result.error.asUiText(),
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
                    when (val result = repository.selectStore(store = action.store)) {
                        is Result.Failure -> {
                            _event.send(
                                StoreSelectionListEvent.Error(
                                    result.error.asUiText(),
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
        }
    }
}