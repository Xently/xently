package co.ke.xently.features.shops.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "shops",
    primaryKeys = ["id", "lookupKey"],
)
data class ShopEntity(
    val shop: Shop,
    @ColumnInfo(index = true)
    val id: Long = shop.id,
    val isActivated: Boolean = false,
    @ColumnInfo(defaultValue = LookupKeyManager.DEFAULT_KEY, index = true)
    val lookupKey: String = LookupKeyManager.DEFAULT_KEY,
    @ColumnInfo(defaultValue = "32400000", index = true)
    val dateSaved: Instant = Clock.System.now(),
)
