package co.ke.xently.features.notifications.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.features.notifications.data.source.local.NotificationDatabase
import co.ke.xently.features.notifications.data.source.local.NotificationEntity
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: NotificationDatabase,
    private val accessControlRepository: AccessControlRepository,
) : NotificationRepository {
    private val notificationDao = database.notificationDao()
    override suspend fun getNotifications(url: String?, filters: Any): PagedResponse<Notification> {
        val urlString = url ?: accessControlRepository.getAccessControl().myNotificationsUrl
        return httpClient.get(urlString = urlString).body<PagedResponse<Notification>>().run {
            (embedded.values.firstOrNull() ?: emptyList()).let { notifications ->
                coroutineScope {
                    launch {
                        database.withTransactionFacade {
                            notificationDao.save(notifications.map {
                                NotificationEntity(
                                    notification = it
                                )
                            })
                        }
                    }
                }
                copy(embedded = mapOf("views" to notifications))
            }
        }
    }
}