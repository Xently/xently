package co.ke.xently.features.customers.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.customers.data.domain.Customer

@Entity(tableName = "customers")
data class CustomerEntity(
    val customer: Customer,
    @PrimaryKey
    val id: Long = customer.id,
)
