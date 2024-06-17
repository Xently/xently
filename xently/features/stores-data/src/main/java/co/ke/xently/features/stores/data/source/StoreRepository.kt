package co.ke.xently.features.stores.data.source

import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.StoreFilters
import co.ke.xently.features.stores.data.domain.error.ConfigurationError
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.Error
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.libraries.pagination.data.PagedResponse
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    suspend fun save(store: Store): Result<Unit, DataError>
    fun findById(id: Long): Flow<Result<Store, DataError>>
    suspend fun getActiveStore(): Result<Store, ConfigurationError>
    fun findActiveStore(): Flow<Result<Store, ConfigurationError>>
    suspend fun getStores(url: String?, filters: StoreFilters): PagedResponse<Store>
    suspend fun deleteStore(store: Store): Result<Unit, Error>
    suspend fun selectStore(store: Store): Result<Unit, DataError.Local>
}
