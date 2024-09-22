package co.ke.xently.features.products.presentation.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.source.ProductCategoryRepository
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductFilters
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.features.products.data.source.ProductRepository
import co.ke.xently.features.products.presentation.utils.asUiText
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


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
open class ProductListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
) : ViewModel() {
    private companion object {
        private val KEY = ProductListViewModel::class.java.name.plus("SELECTED_PRODUCT_CATEGORIES")
    }

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProductListEvent>()
    internal val event: Flow<ProductListEvent> = _event.receiveAsFlow()

    private val _selectedCategories = savedStateHandle.getStateFlow(KEY, emptySet<String>())

    val categories: StateFlow<List<ProductCategory>> =
        _selectedCategories.flatMapLatest { selectedCategories ->
            productCategoryRepository.getCategories(null).map { categories ->
                (selectedCategories.map {
                    ProductCategory(name = it, selected = true)
                }.sortedBy { it.name } + categories).distinctBy { it.name }
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        )

    private val _filters = MutableStateFlow(ProductFilters())

    open val products = savedStateHandle.getStateFlow(
        key = "productsUrl",
        initialValue = "",
    ).flatMapLatest(::getProductPagingDataFlow)
        .cachedIn(viewModelScope)

    protected fun getProductPagingDataFlow(productsUrl: String): Flow<PagingData<Product>> {
        return _selectedCategories.combine(_filters) { categories, filters ->
            filters.copy(
                categories = categories.map {
                    ProductCategory(name = it)
                }.toSet(),
            )
        }.flatMapLatest { filters ->
            repository.getProducts(url = productsUrl, filters = filters)
        }
    }

    internal fun onAction(action: ProductListAction) {
        when (action) {
            is ProductListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
            }

            is ProductListAction.SelectCategory -> {
                val productCategories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet())
                savedStateHandle[KEY] = productCategories + action.category.name
            }

            is ProductListAction.RemoveCategory -> {
                val productCategories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet())
                savedStateHandle[KEY] = productCategories - action.category.name
            }

            is ProductListAction.Search -> {
                _filters.update { it.copy(query = action.query) }
            }

            is ProductListAction.DeleteProduct -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                    when (val result = repository.deleteProduct(product = action.product)) {
                        is Result.Failure -> {
                            _event.send(
                                ProductListEvent.Error(
                                    result.error.asUiText(),
                                    result.error
                                )
                            )
                        }

                        is Result.Success -> {
                            _event.send(ProductListEvent.Success(action))
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