package co.ke.xently.features.storeservice.data.source

import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.features.storeservice.data.domain.error.DataError
import co.ke.xently.features.storeservice.data.domain.error.Result
import co.ke.xently.features.storeservice.data.source.local.StoreServiceDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class StoreServiceRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: StoreServiceDatabase,
) : StoreServiceRepository {
    override suspend fun save(storeService: StoreService): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<StoreService, DataError>> {
        TODO("Not yet implemented")
    }
}