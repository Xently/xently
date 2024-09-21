package co.ke.xently.features.customers.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "customers",
    primaryKeys = ["id", "lookupKey"],
)
data class CustomerEntity(
    val customer: Customer,
    @ColumnInfo(index = true)
    val id: String = customer.id,
    @ColumnInfo(defaultValue = LookupKeyManager.DEFAULT_KEY, index = true)
    val lookupKey: String = LookupKeyManager.DEFAULT_KEY,
    @ColumnInfo(defaultValue = "32400000", index = true)
    val dateSaved: Instant = Clock.System.now(),
)
