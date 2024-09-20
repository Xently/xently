package co.ke.xently.features.notifications.data.source.local

import co.ke.xently.libraries.pagination.data.RemoteKeyDatabase

interface NotificationDatabase : RemoteKeyDatabase {
    fun notificationDao(): NotificationDao
}