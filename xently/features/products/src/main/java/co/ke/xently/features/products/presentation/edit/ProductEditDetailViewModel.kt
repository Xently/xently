package co.ke.xently.features.products.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.source.ProductCategoryRepository
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductDataValidator
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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
    private val dataValidator: ProductDataValidator,
) : ViewModel() {
    private companion object {
        private val KEY = ProductEditDetailViewModel::class.java.name.plus("SELECTED_PRODUCT_CATEGORIES")
    }

    private val _uiState = MutableStateFlow(ProductEditDetailUiState())
    val uiState: StateFlow<ProductEditDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<Long>("productId", -1)
                .flatMapLatest(repository::findById)
                .onStart { _uiState.update { it.copy(isLoading = true, disableFields = true) } }
                .onCompletion { _uiState.update { it.copy(isLoading = false, disableFields = false) } }
                .collect { result ->
                    _uiState.update {
                        when (result) {
                            is Result.Failure -> ProductEditDetailUiState()
                            is Result.Success -> ProductEditDetailUiState(product = result.data)
                        }
                    }
                }
        }
    }

    private val _event = Channel<ProductEditDetailEvent>()
    val event: Flow<ProductEditDetailEvent> = _event.receiveAsFlow()

    val categories: StateFlow<List<ProductCategory>> =
        savedStateHandle.getStateFlow(KEY, emptySet<ProductCategory>())
            .flatMapLatest { selectedCategories ->
                productCategoryRepository.getCategories(null).map {
                    it.map { category ->
                        category.copy(selected = category in selectedCategories)
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            )

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

            ProductEditDetailAction.ClickSave, ProductEditDetailAction.ClickSaveAndAddAnother -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }
                    val product = validatedProduct(state)

                    if (_uiState.value.isFormValid) {
                        when (val result = repository.save(product = product)) {
                            is Result.Success -> {
                                _event.send(ProductEditDetailEvent.Success(action))
                            }

                            is Result.Failure -> {
                                _event.send(
                                    ProductEditDetailEvent.Error(
                                        result.error.asUiText(),
                                        result.error,
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

    private fun validatedProduct(state: ProductEditDetailUiState): Product {
        var product = state.product

        when (val result = dataValidator.validatedPrice(state.unitPrice)) {
            is Result.Failure -> _uiState.update { it.copy(unitPriceError = result.error) }
            is Result.Success -> {
                _uiState.update { it.copy(unitPriceError = null) }
                product = product.copy(unitPrice = result.data)
            }
        }

        when (val result = dataValidator.validatedName(state.name)) {
            is Result.Failure -> _uiState.update { it.copy(nameError = result.error) }
            is Result.Success -> {
                _uiState.update { it.copy(nameError = null) }
                product = product.copy(name = result.data)
            }
        }

        when (val result = dataValidator.validatedDescription(state.description)) {
            is Result.Failure -> _uiState.update { it.copy(descriptionError = result.error) }
            is Result.Success -> {
                _uiState.update { it.copy(descriptionError = null) }
                product = product.copy(description = result.data)
            }
        }

        return product
    }
}