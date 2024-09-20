package co.ke.xently.features.stores.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "stores",
    primaryKeys = ["id", "lookupKey"],
)
data class StoreEntity(
    val store: Store,
    val id: Long = store.id,
    val isActivated: Boolean = false,
    @ColumnInfo(defaultValue = LookupKeyManager.DEFAULT_KEY)
    val lookupKey: String = LookupKeyManager.DEFAULT_KEY,
    @ColumnInfo(defaultValue = "32400000")
    val dateSaved: Instant = Clock.System.now(),
)
