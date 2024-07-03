package co.ke.xently.features.recommendations.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.source.ProductCategoryRepository
import co.ke.xently.features.recommendations.data.domain.RecommendationRequest
import co.ke.xently.features.recommendations.data.domain.RecommendationResponse
import co.ke.xently.features.recommendations.data.source.RecommendationRepository
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.source.StoreCategoryRepository
import co.ke.xently.libraries.location.tracker.domain.Location
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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

    val recommendations: Flow<PagingData<RecommendationResponse>> =
        savedStateHandle.getStateFlow(
            key = "recommendationsUrl",
            initialValue = "",
        ).flatMapLatest(::getRecommendationPagingDataFlow)

    private fun getRecommendationPagingDataFlow(recommendationsUrl: String): Flow<PagingData<RecommendationResponse>> {
        return _selectedProductCategories.flatMapLatest { _ ->
            pager { url ->
                repository.getRecommendations(
                    request = RecommendationRequest(
                        location = Location(),
                        storeDistanceMeters = null,
                        storeCategories = emptyList(),
                        storeServices = emptyList(),
                        productCategories = emptyList(),
                        maximumPrice = null,
                        minimumPrice = null,
                    ),
                    url = url ?: recommendationsUrl,
                )
            }.flow
        }
    }

    private fun pager(call: suspend (String?) -> PagedResponse<RecommendationResponse>) =
        Pager(PagingConfig(pageSize = 20)) {
            XentlyPagingSource(apiCall = call)
        }

    internal fun onAction(action: RecommendationAction) {
    }
}