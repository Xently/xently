package co.ke.xently.features.profile.data.source

import co.ke.xently.features.profile.data.domain.ProfileStatistic
import co.ke.xently.features.profile.data.domain.error.Error
import co.ke.xently.features.profile.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ProfileStatisticRepository {
    suspend fun findStatisticById(id: Int = 1): Flow<Result<ProfileStatistic, Error>>
}
