package co.ke.xently.features.openinghours.data.source

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.openinghours.data.domain.error.DataError
import co.ke.xently.features.openinghours.data.domain.error.Result
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class OpeningHourRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
) : OpeningHourRepository {
    override suspend fun save(shop: OpeningHour): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<OpeningHour, DataError>> {
        TODO("Not yet implemented")
    }
}