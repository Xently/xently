package co.ke.xently.features.stores.data.source

import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.local.StoreDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class StoreRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: StoreDatabase,
) : StoreRepository {
    override suspend fun save(store: Store): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<Store, DataError>> {
        TODO("Not yet implemented")
    }
}