package co.ke.xently.features.productcategory.data.source

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.domain.error.DataError
import co.ke.xently.features.productcategory.data.domain.error.Result
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryDatabase
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

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

    override suspend fun getCategories(url: String?): PagedResponse<ProductCategory> {
        val categories = List(Random.nextInt(10, 20)) {
            ProductCategory(name = "Category ${it + 1}")
        }
        delay(Random.nextLong(2_000))
        return PagedResponse(embedded = mapOf("views" to categories))
        return httpClient.get(url ?: "https://localhost")
            .body()
    }
}