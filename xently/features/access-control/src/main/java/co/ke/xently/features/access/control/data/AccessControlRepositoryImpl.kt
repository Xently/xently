package co.ke.xently.features.access.control.data

import co.ke.xently.features.access.control.data.local.AccessControlDatabase
import co.ke.xently.features.access.control.data.local.AccessControlEntity
import co.ke.xently.features.access.control.domain.AccessControl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.minutes

@Singleton
internal class AccessControlRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    database: AccessControlDatabase,
) : AccessControlRepository() {
    private val accessControlDao = database.accessControlDao()

    override fun findAccessControl(): Flow<AccessControl> {
        suspend fun save(): AccessControl {
            val accessControl = accessControlDao.first()?.takeIf {
                (Clock.System.now() - it.lastUpdated).inWholeMilliseconds < REFRESH_INTERVAL.inWholeMilliseconds
            }

            if (accessControl != null) return accessControl.accessControl

            Timber.tag(TAG).i("Saving access control response...")
            return httpClient.get("/api/v1")
                .body<AccessControl>().also {
                accessControlDao
                    .save(AccessControlEntity(it.copyWithDefaultMissingKeys()))
            }
        }
        return accessControlDao.findFirst()
            .map { it?.accessControl ?: save() }
            .onEmpty { emit(save()) }
            .onStart {
                while (true) {
                    try {
                        emit(save())
                    } catch (ex: Exception) {
                        if (ex is CancellationException) throw ex
                        emit(AccessControl())
                    }
                    Timber.tag(TAG).i("Waiting %s before another check...", REFRESH_INTERVAL)
                    delay(REFRESH_INTERVAL)
                }
            }
            .catch { emit(AccessControl()) }
    }

    companion object {
        private val REFRESH_INTERVAL = 1.minutes
        private val TAG = AccessControlRepository::class.java.simpleName
    }
}