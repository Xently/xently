package co.ke.xently.features.shops.data.source

import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.ShopFilters
import co.ke.xently.features.shops.data.domain.error.ConfigurationError
import co.ke.xently.features.shops.data.domain.error.DataError
import co.ke.xently.features.shops.data.domain.error.Error
import co.ke.xently.features.shops.data.domain.error.Result
import co.ke.xently.libraries.pagination.data.PagedResponse
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    suspend fun save(shop: Shop, merchant: Merchant): Result<Unit, Error>
    fun findActivatedShop(): Flow<Result<Shop, ConfigurationError>>
    suspend fun getShops(url: String?, filters: ShopFilters): PagedResponse<Shop>
    suspend fun deleteShop(shop: Shop): Result<Unit, Error>
    suspend fun selectShop(shop: Shop): Result<Unit, DataError.Local>
    fun findTop10ShopsOrderByIsActivated(): Flow<List<Shop>>
    suspend fun getActiveShop(): Result<Shop, ConfigurationError>
}
