package co.ke.xently.features.openinghours.data.source

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.openinghours.data.domain.error.DataError
import co.ke.xently.features.openinghours.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface OpeningHourRepository {
    suspend fun save(shop: OpeningHour): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<OpeningHour, DataError>>
}
