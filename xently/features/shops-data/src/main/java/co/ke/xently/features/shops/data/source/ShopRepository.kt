package co.ke.xently.features.shops.data.source

import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.ShopFilters
import co.ke.xently.features.shops.data.domain.error.DataError
import co.ke.xently.features.shops.data.domain.error.Result
import co.ke.xently.libraries.pagination.data.PagedResponse
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    suspend fun save(shop: Shop, merchant: Merchant): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<Shop, DataError>>
    suspend fun getShops(url: String?, filters: ShopFilters): PagedResponse<Shop>
    suspend fun deleteShop(shop: Shop): Result<Unit, DataError>
    suspend fun selectShop(shop: Shop): Result<Unit, DataError.Local>
}
