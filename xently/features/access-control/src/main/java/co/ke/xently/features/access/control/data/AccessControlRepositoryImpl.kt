package co.ke.xently.features.access.control.data

import co.ke.xently.features.access.control.BuildConfig.BASE_URL
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
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.minutes

@Singleton
internal class AccessControlRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: AccessControlDatabase,
) : AccessControlRepository() {
    override fun findAccessControl(): Flow<AccessControl> {
        suspend fun save(): AccessControl {
            Timber.tag(TAG).i("Saving access control response...")
            return httpClient.get(BASE_URL).body<AccessControl>().also {
                database.accessControlDao().save(AccessControlEntity(it))
            }
        }
        return database.accessControlDao().findFirst()
            .map { (it?.accessControl ?: save()).copyWithDefaultMissingKeys() }
            .onEmpty { emit(save().copyWithDefaultMissingKeys()) }
            .onStart {
                val refreshInterval = 1.minutes
                while (true) {
                    try {
                        emit(save().copyWithDefaultMissingKeys())
                    } catch (ex: Exception) {
                        if (ex is CancellationException) throw ex
                        emit(AccessControl())
                    }
                    Timber.tag(TAG).i("Waiting %s before another check...", refreshInterval)
                    delay(refreshInterval)
                }
            }
            .catch { emit(AccessControl()) }
    }

    companion object {
        private val TAG = AccessControlRepository::class.java.simpleName
    }
}