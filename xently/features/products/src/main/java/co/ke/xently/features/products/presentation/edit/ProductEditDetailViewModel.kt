package co.ke.xently.features.products.presentation.edit

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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class ProductEditDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
) : ViewModel() {
    private companion object {
        private const val KEY =
            "co.ke.xently.features.products.presentation.edit.SELECTED_PRODUCT_CATEGORIES"
    }

    private val _uiState = MutableStateFlow(ProductEditDetailUiState())
    val uiState: StateFlow<ProductEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProductEditDetailEvent>()
    val event: Flow<ProductEditDetailEvent> = _event.receiveAsFlow()

    val categories: Flow<PagingData<ProductCategory>> =
        savedStateHandle.getStateFlow(KEY, emptySet<ProductCategory>())
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

    fun onAction(action: ProductEditDetailAction) {
        when (action) {
            is ProductEditDetailAction.SelectCategory -> {
                val productCategories = (savedStateHandle.get<Set<ProductCategory>>(KEY)
                    ?: emptySet())

                savedStateHandle[KEY] = productCategories + action.category
            }

            is ProductEditDetailAction.RemoveCategory -> {
                val productCategories = (savedStateHandle.get<Set<ProductCategory>>(KEY)
                    ?: emptySet())
                savedStateHandle[KEY] = productCategories - action.category
            }

            is ProductEditDetailAction.ChangeCategoryName -> {
                _uiState.update {
                    it.copy(categoryName = action.name)
                }
            }

            is ProductEditDetailAction.ChangeDescription -> {
                _uiState.update {
                    it.copy(description = action.description)
                }
            }

            is ProductEditDetailAction.ChangeUnitPrice -> {
                _uiState.update {
                    it.copy(unitPrice = action.unitPrice)
                }
            }

            is ProductEditDetailAction.ChangeName -> {
                _uiState.update {
                    it.copy(name = action.name)
                }
            }

            ProductEditDetailAction.ClickSaveDetails -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }
                    val product = state.product.copy(
                        name = state.name,
                        unitPrice = state.unitPrice.toDouble(),
                    )
                    when (val result = repository.save(product = product)) {
                        is Result.Failure -> {
                            _event.send(
                                ProductEditDetailEvent.Error(
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
                            _event.send(ProductEditDetailEvent.Success)
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