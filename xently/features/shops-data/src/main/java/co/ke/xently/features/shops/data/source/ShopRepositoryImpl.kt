package co.ke.xently.features.shops.data.source

import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.error.DataError
import co.ke.xently.features.shops.data.domain.error.Result
import co.ke.xently.features.shops.data.source.local.ShopDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ShopRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ShopDatabase,
) : ShopRepository {
    override suspend fun save(shop: Shop): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<Shop, DataError>> {
        TODO("Not yet implemented")
    }
}