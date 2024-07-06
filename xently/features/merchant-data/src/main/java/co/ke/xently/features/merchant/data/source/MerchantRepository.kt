package co.ke.xently.features.merchant.data.source

import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.merchant.data.domain.error.DataError
import co.ke.xently.features.merchant.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface MerchantRepository {
    suspend fun save(merchant: Merchant): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<Merchant, DataError>>
}
