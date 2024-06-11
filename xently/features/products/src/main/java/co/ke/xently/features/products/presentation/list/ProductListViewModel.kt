package co.ke.xently.features.products.presentation.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.source.ProductCategoryRepository
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductFilters
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.features.products.data.source.ProductRepository
import co.ke.xently.features.products.presentation.utils.asUiText
import co.ke.xently.libraries.pagination.data.XentlyPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class ProductListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
) : ViewModel() {
    private companion object {
        private const val KEY =
            "co.ke.xently.features.products.presentation.edit.SELECTED_PRODUCT_CATEGORIES"
    }

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProductListEvent>()
    val event: Flow<ProductListEvent> = _event.receiveAsFlow()

    private val _selectedCategories =
        savedStateHandle.getStateFlow(KEY, emptySet<ProductCategory>())

    val categories: Flow<PagingData<ProductCategory>> = _selectedCategories
        .flatMapLatest { selectedCategories ->
            Pager(
                PagingConfig(
                    pageSize = 20,
                    initialLoadSize = 20,
                )
            ) {
                XentlyPagingSource { url ->
                    productCategoryRepository.getCategories(url)
                }
            }.flow.map { data ->
                data.map { it.copy(selected = it in selectedCategories) }
            }
        }.cachedIn(viewModelScope)

    private val _filters = MutableStateFlow(ProductFilters())

    val products: Flow<PagingData<Product>> =
        _selectedCategories.combineTransform(_filters) { selectedCategories, filters ->
            emitAll(
                Pager(
                    PagingConfig(
                        pageSize = 20,
                        initialLoadSize = 20,
                    )
                ) {
                    XentlyPagingSource { url ->
                        repository.getProducts(
                            url = url,
                            filters = filters.copy(categories = selectedCategories),
                        )
                    }
                }.flow
            )
        }.cachedIn(viewModelScope)

    fun onAction(action: ProductListAction) {
        when (action) {
            is ProductListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
            }

            is ProductListAction.SelectCategory -> {
                val productCategories = (savedStateHandle.get<Set<ProductCategory>>(
                    KEY
                ) ?: emptySet())

                savedStateHandle[KEY] = productCategories + action.category
            }

            is ProductListAction.RemoveCategory -> {
                val productCategories = (savedStateHandle.get<Set<ProductCategory>>(
                    KEY
                ) ?: emptySet())
                savedStateHandle[KEY] = productCategories - action.category
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