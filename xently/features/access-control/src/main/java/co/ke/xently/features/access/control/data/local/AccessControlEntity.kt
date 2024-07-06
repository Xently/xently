package co.ke.xently.features.access.control.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.access.control.domain.AccessControl
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(tableName = "access_controls")
data class AccessControlEntity(
    val accessControl: AccessControl,
    @PrimaryKey
    val id: Long = 1,
    val lastUpdated: Instant = Clock.System.now(),
)
