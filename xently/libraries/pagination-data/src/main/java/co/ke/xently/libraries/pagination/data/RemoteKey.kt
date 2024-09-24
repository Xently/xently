package co.ke.xently.libraries.pagination.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val lookupKey: String,
    @Embedded val links: PagedResponse.Links,
    val dateRecorded: Instant = Clock.System.now(),
)