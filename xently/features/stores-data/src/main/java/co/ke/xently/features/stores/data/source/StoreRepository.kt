package co.ke.xently.features.stores.data.source

import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    suspend fun save(store: Store): Result<Unit, DataError>
    fun findById(id: Long): Flow<Result<Store, DataError>>
    fun findActiveStore(): Flow<Store?>
}
