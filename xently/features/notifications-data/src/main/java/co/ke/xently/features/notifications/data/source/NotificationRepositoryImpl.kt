package co.ke.xently.features.notifications.data.source

import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.features.notifications.data.domain.error.DataError
import co.ke.xently.features.notifications.data.domain.error.Result
import co.ke.xently.features.notifications.data.source.local.NotificationDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: NotificationDatabase,
) : NotificationRepository {
    override suspend fun save(notification: Notification): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<Notification, DataError>> {
        TODO("Not yet implemented")
    }
}