package co.ke.xently.features.storecategory.data.source

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import kotlinx.coroutines.flow.Flow

interface StoreCategoryRepository {
    suspend fun getCategories(url: String?): Flow<List<StoreCategory>>
}
