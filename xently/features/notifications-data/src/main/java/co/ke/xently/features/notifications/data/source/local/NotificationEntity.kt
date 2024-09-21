package co.ke.xently.features.notifications.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "notifications",
    primaryKeys = ["id", "lookupKey"],
)
data class NotificationEntity(
    val notification: Notification,
    @ColumnInfo(index = true)
    val id: Long = notification.id,
    @ColumnInfo(defaultValue = LookupKeyManager.DEFAULT_KEY, index = true)
    val lookupKey: String = LookupKeyManager.DEFAULT_KEY,
    @ColumnInfo(defaultValue = "32400000", index = true)
    val dateSaved: Instant = Clock.System.now(),
)
