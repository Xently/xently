package co.ke.xently.features.storecategory.data.source

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.domain.error.DataError
import co.ke.xently.features.storecategory.data.domain.error.Result
import co.ke.xently.libraries.pagination.data.PagedResponse
import kotlinx.coroutines.flow.Flow

interface StoreCategoryRepository {
    suspend fun save(storeCategory: StoreCategory): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<StoreCategory, DataError>>
    suspend fun getCategories(url: String?): PagedResponse<StoreCategory>
}
