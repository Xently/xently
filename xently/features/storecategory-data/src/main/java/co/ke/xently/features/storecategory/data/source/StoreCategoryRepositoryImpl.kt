package co.ke.xently.features.storecategory.data.source

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.domain.error.DataError
import co.ke.xently.features.storecategory.data.domain.error.Result
import co.ke.xently.features.storecategory.data.source.local.StoreCategoryDatabase
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

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
        TODO("Not yet implemented")
    }
}