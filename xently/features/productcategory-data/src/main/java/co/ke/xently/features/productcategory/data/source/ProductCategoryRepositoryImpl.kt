package co.ke.xently.features.productcategory.data.source

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.domain.error.DataError
import co.ke.xently.features.productcategory.data.domain.error.Result
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ProductCategoryRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ProductCategoryDatabase,
) : ProductCategoryRepository {
    override suspend fun save(productCategory: ProductCategory): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<ProductCategory, DataError>> {
        TODO("Not yet implemented")
    }
}