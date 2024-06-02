package co.ke.xently.features.shops.data.source

import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.error.DataError
import co.ke.xently.features.shops.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    suspend fun save(shop: Shop): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<Shop, DataError>>
}
