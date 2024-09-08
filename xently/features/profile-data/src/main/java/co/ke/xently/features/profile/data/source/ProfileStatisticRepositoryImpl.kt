package co.ke.xently.features.profile.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.profile.data.domain.ProfileStatistic
import co.ke.xently.features.profile.data.domain.error.Error
import co.ke.xently.features.profile.data.domain.error.Result
import co.ke.xently.features.profile.data.source.local.ProfileStatisticDatabase
import co.ke.xently.features.profile.data.source.local.ProfileStatisticEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.minutes

@Singleton
internal class ProfileStatisticRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ProfileStatisticDatabase,
    private val accessControlRepository: AccessControlRepository,
) : ProfileStatisticRepository {
    private val profileStatisticDao = database.profileStatisticDao()
    override suspend fun findStatisticById(id: Int): Flow<Result<ProfileStatistic, Error>> {
        suspend fun save(): ProfileStatistic {
            val profileStatistics = profileStatisticDao.get()?.takeIf {
                (Clock.System.now() - it.lastUpdated).inWholeMilliseconds < REFRESH_INTERVAL.inWholeMilliseconds
            }

            if (profileStatistics != null) return profileStatistics.statistic

            Timber.tag(TAG).i("Saving my statistics response...")
            val accessControl = accessControlRepository.getAccessControl()
            val urlString = accessControl.myProfileStatisticsUrl
            return try {
                httpClient.get(urlString = urlString).body<ProfileStatistic>().also {
                    database.withTransactionFacade {
                        profileStatisticDao.save(ProfileStatisticEntity(it))
                    }
                }
            } catch (ex: Exception) {
                coroutineContext.ensureActive()
                Timber.tag(TAG).e(ex, "Failed to get my statistics")
                ProfileStatistic()
            }
        }
        return profileStatisticDao.find()
            .map { it?.statistic ?: save() }
            .onEmpty { emit(save()) }
            .onStart {
                while (true) {
                    emit(save())
                    Timber.tag(TAG).i("Waiting %s before another check...", REFRESH_INTERVAL)
                    delay(REFRESH_INTERVAL)
                }
            }
            .catch { emit(ProfileStatistic()) }
            .map { Result.Success(it) }
    }

    companion object {
        private val REFRESH_INTERVAL = 5.minutes
        private val TAG = ProfileStatisticRepository::class.java.simpleName
    }
}