package co.ke.xently.features.notifications.data.source

import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.features.notifications.data.domain.error.DataError
import co.ke.xently.features.notifications.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun save(notification: Notification): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<Notification, DataError>>
}
