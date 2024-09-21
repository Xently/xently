package co.ke.xently.features.products.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "products",
    primaryKeys = ["id", "lookupKey"],
)
data class ProductEntity(
    val product: Product,
    @ColumnInfo(index = true)
    val id: Long = product.id,
    @ColumnInfo(defaultValue = LookupKeyManager.DEFAULT_KEY, index = true)
    val lookupKey: String = LookupKeyManager.DEFAULT_KEY,
    @ColumnInfo(defaultValue = "32400000", index = true)
    val dateSaved: Instant = Clock.System.now(),
)
