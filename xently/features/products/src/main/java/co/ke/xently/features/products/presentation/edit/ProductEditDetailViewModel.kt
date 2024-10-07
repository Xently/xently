package co.ke.xently.features.products.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.source.ProductCategoryRepository
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductDataValidator
import co.ke.xently.features.products.data.domain.ProductSynonym
import co.ke.xently.features.products.data.domain.error.RemoteFieldError
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.features.products.data.source.ProductRepository
import co.ke.xently.libraries.data.image.domain.Upload
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClient
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class ProductEditDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val dataValidator: ProductDataValidator,
    private val webSocketClient: StompWebSocketClient,
) : ViewModel() {
    private companion object {
        private val KEY =
            ProductEditDetailViewModel::class.java.name.plus("SELECTED_PRODUCT_CATEGORIES")
    }

    private val _uiState = MutableStateFlow(ProductEditDetailUiState())
    val uiState: StateFlow<ProductEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProductEditDetailEvent>()
    val event: Flow<ProductEditDetailEvent> = _event.receiveAsFlow()

    val categories: StateFlow<List<ProductCategory>> =
        savedStateHandle.getStateFlow(KEY, emptySet<String>())
            .flatMapLatest { selectedCategories ->
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

    val productSynonymsSearchSuggestions = webSocketClient.watch {
        subscribe<List<String>>(destination = "/type-ahead/results/product-synonyms")
    }

    val productCategoriesSearchSuggestions = webSocketClient.watch {
        subscribe<List<String>>(destination = "/type-ahead/results/product-categories")
    }

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<Long>("productId", -1)
                .flatMapLatest(repository::findById)
                .onStart { _uiState.update { it.copy(isLoading = true, disableFields = true) } }
                .onCompletion {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            disableFields = false
                        )
                    }
                }
                .collect { result ->
                    _uiState.update {
                        when (result) {
                            is Result.Failure -> ProductEditDetailUiState()
                            is Result.Success -> {
                                savedStateHandle[KEY] =
                                    result.data.categories.map { it.name }.toSet()
                                ProductEditDetailUiState(product = result.data)
                            }
                        }
                    }
                }
        }
    }

    private var productSynonymsSearchSuggestionsJob: Job? = null

    private var productCategoriesSearchSuggestionsJob: Job? = null

    fun onAction(action: ProductEditDetailAction) {
        when (action) {
            is ProductEditDetailAction.ClearFieldsForNewProduct -> {
                _uiState.update {
                    it.copy(
                        name = "",
                        unitPrice = "",
                        description = "",
                        product = Product(),
                        nameError = null,
                        unitPriceError = null,
                        descriptionError = null,
                    )
                }
            }

            is ProductEditDetailAction.SelectCategory -> {
                val productCategories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet())
                savedStateHandle[KEY] = productCategories + action.category.name
            }

            is ProductEditDetailAction.RemoveCategory -> {
                val productCategories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet())
                savedStateHandle[KEY] = productCategories - action.category.name
            }

            is ProductEditDetailAction.AddSynonym -> {
                _uiState.update {
                    it.copy(synonyms = it.synonyms + Chip(action.synonym))
                }
            }

            is ProductEditDetailAction.RemoveSynonym -> {
                _uiState.update {
                    it.copy(synonyms = it.synonyms - action.synonym)
                }
            }

            is ProductEditDetailAction.OnSynonymQueryChange -> {
                productSynonymsSearchSuggestionsJob?.cancel()
                productSynonymsSearchSuggestionsJob = viewModelScope.launch {
                    webSocketClient.sendMessage {
                        convertAndSend(
                            destination = "/app/type-ahead/product-synonyms",
                            body = co.ke.xently.libraries.data.core.TypeAheadSearchRequest(query = action.query),
                        )
                    }
                }
            }

            is ProductEditDetailAction.AddAdditionalCategory -> {
                _uiState.update {
                    it.copy(additionalCategories = it.additionalCategories + Chip(action.category))
                }
            }

            is ProductEditDetailAction.RemoveAdditionalCategory -> {
                _uiState.update {
                    it.copy(additionalCategories = it.additionalCategories - action.category)
                }
            }

            is ProductEditDetailAction.OnCategoryQueryChange -> {
                productCategoriesSearchSuggestionsJob?.cancel()
                productCategoriesSearchSuggestionsJob = viewModelScope.launch {
                    webSocketClient.sendMessage {
                        convertAndSend(
                            destination = "/app/type-ahead/product-categories",
                            body = co.ke.xently.libraries.data.core.TypeAheadSearchRequest(query = action.query),
                        )
                    }
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

            is ProductEditDetailAction.RemoveImageAtPosition -> {
                _uiState.update {
                    it.copy(
                        images = it.images.mapIndexed { index, image ->
                            if (index == action.position) null else image
                        },
                    )
                }
            }

            is ProductEditDetailAction.ProcessImageData -> {
                val (position, imageData) = action.data
                _uiState.update {
                    it.copy(
                        images = it.images.mapIndexed { index, image ->
                            if (index == position) imageData else image
                        },
                    )
                }
            }

            ProductEditDetailAction.ClickSave, ProductEditDetailAction.ClickSaveAndAddAnother -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(
                            isLoading = true,
                            nameError = null,
                            unitPriceError = null,
                            descriptionError = null,
                        )
                    }
                    val product = validatedProduct(state)

                    if (_uiState.value.isFormValid) {
                        val images = state.images.filterIsInstance<Upload>()
                        when (val result = repository.save(product = product, images = images)) {
                            is Result.Success -> {
                                _event.send(ProductEditDetailEvent.Success(action))
                            }

                            is Result.Failure -> {
                                when (val error = result.error) {
                                    is RemoteFieldError -> {
                                        _uiState.update {
                                            it.copy(
                                                nameError = error.errors["name"] ?: emptyList(),
                                                unitPriceError = error.errors["unitPrice"]
                                                    ?: emptyList(),
                                                descriptionError = error.errors["description"]
                                                    ?: emptyList(),
                                            )
                                        }
                                    }

                                    else -> _event.send(
                                        ProductEditDetailEvent.Error(
                                            error = error.toUiText(),
                                            type = error,
                                        )
                                    )
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

    private fun validatedProduct(state: ProductEditDetailUiState): Product {
        var product = state.product.copy(
            synonyms = state.synonyms.map { ProductSynonym(name = it.text) },
            categories = (savedStateHandle.get<Set<String>>(KEY) ?: emptySet()).map {
                ProductCategory(name = it)
            } + state.additionalCategories.map { ProductCategory(name = it.text) },
        )

        when (val result = dataValidator.validatedPrice(state.unitPrice)) {
            is Result.Failure -> _uiState.update { it.copy(unitPriceError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(unitPriceError = null) }
                product = product.copy(unitPrice = result.data)
            }
        }

        when (val result = dataValidator.validatedName(state.name)) {
            is Result.Failure -> _uiState.update { it.copy(nameError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(nameError = null) }
                product = product.copy(name = result.data)
            }
        }

        when (val result = dataValidator.validatedDescription(state.description)) {
            is Result.Failure -> _uiState.update { it.copy(descriptionError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(descriptionError = null) }
                product = product.copy(description = result.data)
            }
        }

        return product
    }
}