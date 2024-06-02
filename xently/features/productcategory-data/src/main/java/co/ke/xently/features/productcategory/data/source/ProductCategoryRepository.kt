package co.ke.xently.features.productcategory.data.source

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.domain.error.DataError
import co.ke.xently.features.productcategory.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ProductCategoryRepository {
    suspend fun save(productCategory: ProductCategory): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<ProductCategory, DataError>>
}
