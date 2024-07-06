package co.ke.xently.features.notification.topic.data.source

import co.ke.xently.features.notification.topic.data.domain.NotificationTopic
import co.ke.xently.features.notification.topic.data.domain.error.DataError
import co.ke.xently.features.notification.topic.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface NotificationTopicRepository {
    suspend fun save(notificationTopic: NotificationTopic): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<NotificationTopic, DataError>>
}
