package co.ke.xently.features.storeservice.data.source

import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.features.storeservice.data.domain.error.DataError
import co.ke.xently.features.storeservice.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface StoreServiceRepository {
    suspend fun save(storeService: StoreService): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<StoreService, DataError>>
}
