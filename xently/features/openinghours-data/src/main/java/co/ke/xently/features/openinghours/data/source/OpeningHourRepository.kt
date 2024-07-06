package co.ke.xently.features.openinghours.data.source

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.openinghours.data.domain.error.Error
import co.ke.xently.features.openinghours.data.domain.error.Result

interface OpeningHourRepository {
    suspend fun save(hour: OpeningHour): Result<OpeningHour, Error>
}
