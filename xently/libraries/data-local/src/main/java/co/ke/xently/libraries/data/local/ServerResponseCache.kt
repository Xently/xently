package co.ke.xently.libraries.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(tableName = "server_response_cache")
data class ServerResponseCache(
    @PrimaryKey
    val id: String,
    val lastUpdated: Instant = Clock.System.now(),
)