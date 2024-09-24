package co.ke.xently.features.notifications.data.source

import androidx.paging.PagingData
import co.ke.xently.features.notifications.data.domain.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotificationsUrl(): String
    fun getNotifications(url: String, filters: Any): Flow<PagingData<Notification>>
}
