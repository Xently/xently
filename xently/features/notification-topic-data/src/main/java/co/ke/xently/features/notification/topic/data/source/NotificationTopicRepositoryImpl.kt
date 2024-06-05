package co.ke.xently.features.notification.topic.data.source

import co.ke.xently.features.notification.topic.data.domain.NotificationTopic
import co.ke.xently.features.notification.topic.data.domain.error.DataError
import co.ke.xently.features.notification.topic.data.domain.error.Result
import co.ke.xently.features.notification.topic.data.source.local.NotificationTopicDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationTopicRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: NotificationTopicDatabase,
) : NotificationTopicRepository {
    override suspend fun save(notificationTopic: NotificationTopic): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<NotificationTopic, DataError>> {
        TODO("Not yet implemented")
    }
}