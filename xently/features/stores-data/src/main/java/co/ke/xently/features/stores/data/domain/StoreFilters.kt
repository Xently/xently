package co.ke.xently.features.stores.data.domain

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.location.tracker.domain.Location

data class StoreFilters(
    val query: String? = null,
    val location: Location? = null,
    val storeServices: Set<StoreService> = emptySet(),
    val storeCategories: Set<StoreCategory> = emptySet(),
    val productCategories: Set<ProductCategory> = emptySet(),
    val sortBy: List<String> = emptyList(),
    val minimumPrice: String? = null,
    val maximumPrice: String? = null,
    val radiusMeters: Int? = null,
)