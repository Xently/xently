package co.ke.xently.features.storecategory.data.source

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.domain.error.DataError
import co.ke.xently.features.storecategory.data.domain.error.Result
import co.ke.xently.features.storecategory.data.source.local.StoreCategoryDatabase
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
internal class StoreCategoryRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: StoreCategoryDatabase,
) : StoreCategoryRepository {
    override suspend fun save(storeCategory: StoreCategory): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<StoreCategory, DataError>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategories(url: String?): PagedResponse<StoreCategory> {
        val categories = List(Random.nextInt(10, 20)) {
            StoreCategory(name = "Category ${it + 1}")
        }
        delay(Random.nextLong(2_000))
        return PagedResponse(embedded = mapOf("views" to categories))
        return httpClient.get(url ?: "https://localhost")
            .body()
    }
}