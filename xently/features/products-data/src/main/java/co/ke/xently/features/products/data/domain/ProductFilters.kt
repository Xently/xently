package co.ke.xently.features.products.data.domain

import co.ke.xently.features.productcategory.data.domain.ProductCategory

data class ProductFilters(
    val query: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Double? = null,
    val categories: Set<ProductCategory>? = null,
)
