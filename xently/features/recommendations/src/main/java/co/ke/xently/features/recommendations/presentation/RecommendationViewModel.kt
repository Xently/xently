package co.ke.xently.features.recommendations.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.source.ProductCategoryRepository
import co.ke.xently.features.recommendations.data.domain.RecommendationRequest
import co.ke.xently.features.recommendations.data.domain.ShoppingListItem
import co.ke.xently.features.recommendations.data.source.RecommendationRepository
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.source.StoreCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecommendationRepository,
    private val storeCategoryRepository: StoreCategoryRepository,
    private val productCategoryRepository: ProductCategoryRepository,
) : ViewModel() {
    private companion object {
        private val SHOPPING_LIST_KEY =
            RecommendationViewModel::class.java.name.plus("SHOPPING_LIST_CATEGORIES")
        private val STORE_CAT_KEY =
            RecommendationViewModel::class.java.name.plus("SELECTED_STORE_CATEGORIES")
        private val PRODUCT_CAT_KEY =
            RecommendationViewModel::class.java.name.plus("SELECTED_PRODUCT_CATEGORIES")
    }

    private val _uiState = MutableStateFlow(RecommendationUiState())
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    private val _event = Channel<RecommendationEvent>()
    internal val event: Flow<RecommendationEvent> = _event.receiveAsFlow()

    private val _selectedProductCategories =
        savedStateHandle.getStateFlow(PRODUCT_CAT_KEY, emptySet<String>())

    val productCategories: StateFlow<List<ProductCategory>> =
        _selectedProductCategories.flatMapLatest { selectedCategories ->
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

    private val _selectedStoreCategories =
        savedStateHandle.getStateFlow(STORE_CAT_KEY, emptySet<String>())

    val storeCategories: StateFlow<List<StoreCategory>> =
        _selectedStoreCategories.flatMapLatest { selectedCategories ->
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

//    val shoppingList = savedStateHandle.getStateFlow(SHOPPING_LIST_KEY, emptyList<String>())

    val recommendations = uiState.mapLatest { state ->
        RecommendationRequest(
            location = state.location,
            storeDistanceMeters = null,
            storeServices = emptyList(),
            storeCategories = emptyList(),
            productCategories = emptyList(),
            maximumPrice = state.maximumPrice?.toDoubleOrNull(),
            minimumPrice = state.minimumPrice?.toDoubleOrNull(),
            shoppingList = state.shoppingList.map { ShoppingListItem(it) }.toSet(),
        )
    }.distinctUntilChanged()
        .combine(_selectedProductCategories) { request, productCategories ->
            request.copy(productCategories = productCategories.toList())
        }.combine(_selectedStoreCategories) { request, storeCategories ->
            request.copy(productCategories = storeCategories.toList())
        }.flatMapLatest { request ->
            val url = repository.getRecommendationsUrl()
            repository.getRecommendations(url = url, request = request)
        }.map { data ->
            data.map { it.store }
        }.cachedIn(viewModelScope)

    internal fun onAction(action: RecommendationAction) {
        when (action) {
            is RecommendationAction.ChangeProductName -> {
                _uiState.update { it.copy(productName = action.name) }
            }

            is RecommendationAction.AddProductName -> {
                val shoppingList =
                    (savedStateHandle.get<List<String>>(SHOPPING_LIST_KEY) ?: emptyList())
                savedStateHandle[SHOPPING_LIST_KEY] = shoppingList - _uiState.value.productName

                _uiState.update {
                    it.copy(
                        shoppingList = it.shoppingList + it.productName,
                        productName = "",
                    )
                }
            }

            is RecommendationAction.RemoveProductName -> {
                val shoppingList =
                    (savedStateHandle.get<List<String>>(SHOPPING_LIST_KEY) ?: emptyList())
                savedStateHandle[SHOPPING_LIST_KEY] = shoppingList - action.name

                _uiState.update {
                    it.copy(shoppingList = it.shoppingList - action.name)
                }
            }

            is RecommendationAction.ChangeLocationQuery -> {
                _uiState.update { it.copy(locationQuery = action.query) }
            }

            is RecommendationAction.ChangeLocation -> {
                _uiState.update { it.copy(location = action.location) }
                onAction(RecommendationAction.ChangeLocationQuery(action.location.coordinatesString()))
            }

            is RecommendationAction.ChangeMaximumPrice -> {
                _uiState.update { it.copy(maximumPrice = action.price) }
            }

            is RecommendationAction.ChangeMinimumPrice -> {
                _uiState.update { it.copy(minimumPrice = action.price) }
            }

            is RecommendationAction.ProductRemoveCategory -> {
                val productCategories =
                    (savedStateHandle.get<Set<String>>(PRODUCT_CAT_KEY) ?: emptySet())
                savedStateHandle[PRODUCT_CAT_KEY] = productCategories - action.category.name
            }

            is RecommendationAction.ProductSelectCategory -> {
                val productCategories =
                    (savedStateHandle.get<Set<String>>(PRODUCT_CAT_KEY) ?: emptySet())
                savedStateHandle[PRODUCT_CAT_KEY] = productCategories + action.category.name
            }

            is RecommendationAction.SearchLocation -> {

            }

            is RecommendationAction.StoreRemoveCategory -> {
                val storeCategories =
                    (savedStateHandle.get<Set<String>>(STORE_CAT_KEY) ?: emptySet())
                savedStateHandle[STORE_CAT_KEY] = storeCategories - action.category.name
            }

            is RecommendationAction.StoreSelectCategory -> {
                val storeCategories =
                    (savedStateHandle.get<Set<String>>(STORE_CAT_KEY) ?: emptySet())
                savedStateHandle[STORE_CAT_KEY] = storeCategories + action.category.name
            }
        }
    }
}