package co.ke.xently.features.notification.topic.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface NotificationTopicDatabase : TransactionFacadeDatabase {
    fun notificationTopicDao(): NotificationTopicDao
}