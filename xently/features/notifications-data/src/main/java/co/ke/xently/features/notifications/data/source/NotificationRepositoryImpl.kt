package co.ke.xently.features.notifications.data.source

import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.features.notifications.data.source.local.NotificationDatabase
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
internal class NotificationRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: NotificationDatabase,
) : NotificationRepository {
    override suspend fun getNotifications(url: String?, filters: Any): PagedResponse<Notification> {
        val notifications = List(20) {
            Notification(
                id = 1L + it,
                timeSent = Clock.System.now(),
                message = Notification.Message(
                    title = "Notification title",
                    message = "New deal 50% off on all meals at the new Imara Daima Hotel",
                ),
            )
        }

        delay(Random.nextLong(1_000, 5_000))
        return PagedResponse(embedded = mapOf("views" to notifications))
    }
}