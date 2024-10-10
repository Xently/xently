package co.ke.xently.features.productcategory.data.source

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import kotlinx.coroutines.flow.Flow

interface ProductCategoryRepository {
    fun getCategories(): Flow<List<ProductCategory>>
}
