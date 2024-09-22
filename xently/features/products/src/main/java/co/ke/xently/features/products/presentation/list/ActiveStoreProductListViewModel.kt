package co.ke.xently.features.products.presentation.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import co.ke.xently.features.productcategory.data.source.ProductCategoryRepository
import co.ke.xently.features.products.data.domain.error.ShopSelectionRequiredException
import co.ke.xently.features.products.data.domain.error.StoreSelectionRequiredException
import co.ke.xently.features.products.data.source.ProductRepository
import co.ke.xently.features.stores.data.domain.error.ConfigurationError
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class ActiveStoreProductListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ProductRepository,
    productCategoryRepository: ProductCategoryRepository,
    storeRepository: StoreRepository,
) : ProductListViewModel(
    savedStateHandle = savedStateHandle,
    repository = repository,
    productCategoryRepository = productCategoryRepository,
) {
    override val products = storeRepository.findActiveStore().flatMapLatest { result ->
        when (result) {
            is Result.Failure -> {
                when (result.error) {
                    ConfigurationError.ShopSelectionRequired -> throw ShopSelectionRequiredException()
                    ConfigurationError.StoreSelectionRequired -> throw StoreSelectionRequiredException()
                }
            }

            is Result.Success -> {
                val url = result.data.links["products"]!!.hrefWithoutQueryParamTemplates()
                getProductPagingDataFlow(productsUrl = url)
            }
        }
    }.cachedIn(viewModelScope)
}