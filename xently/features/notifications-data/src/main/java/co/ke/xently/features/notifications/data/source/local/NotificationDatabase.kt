package co.ke.xently.features.notifications.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface NotificationDatabase : TransactionFacadeDatabase {
    fun notificationDao(): NotificationDao
}