package co.ke.xently.features.stores.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.stores.data.domain.Store

@Entity(tableName = "stores")
data class StoreEntity(
    val store: Store,
    @PrimaryKey
    val id: Long = store.id,
    val isActivated: Boolean = false,
)
