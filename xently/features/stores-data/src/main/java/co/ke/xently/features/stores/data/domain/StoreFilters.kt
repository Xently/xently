package co.ke.xently.features.stores.data.domain

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.location.tracker.domain.Location

data class StoreFilters(
    val query: String? = null,
    val location: Location? = null,
    val storeServices: List<StoreService> = emptyList(),
    val storeCategories: List<StoreCategory> = emptyList(),
    val productCategories: List<ProductCategory> = emptyList(),
    val sortBy: List<String> = emptyList(),
    val minimumPrice: String? = null,
    val maximumPrice: String? = null,
    val radiusMeters: Int? = null,
    val loadType: LoadType = LoadType.All,
) {
    enum class LoadType {
        All,
        ActiveStore,
    }
}