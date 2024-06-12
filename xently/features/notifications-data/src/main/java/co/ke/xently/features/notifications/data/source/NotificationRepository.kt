package co.ke.xently.features.notifications.data.source

import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.libraries.pagination.data.PagedResponse

interface NotificationRepository {
    suspend fun getNotifications(url: String?, filters: Any): PagedResponse<Notification>
}
