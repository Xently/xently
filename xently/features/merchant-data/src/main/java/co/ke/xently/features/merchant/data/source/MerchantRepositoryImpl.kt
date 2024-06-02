package co.ke.xently.features.merchant.data.source

import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.merchant.data.domain.error.DataError
import co.ke.xently.features.merchant.data.domain.error.Result
import co.ke.xently.features.merchant.data.source.local.MerchantDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MerchantRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: MerchantDatabase,
) : MerchantRepository {
    override suspend fun save(merchant: Merchant): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<Merchant, DataError>> {
        TODO("Not yet implemented")
    }
}