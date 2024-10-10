package co.ke.xently.features.storecategory.data.source

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import kotlinx.coroutines.flow.Flow

interface StoreCategoryRepository {
    fun getCategories(): Flow<List<StoreCategory>>
}
