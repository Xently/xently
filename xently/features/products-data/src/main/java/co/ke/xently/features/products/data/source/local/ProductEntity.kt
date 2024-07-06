package co.ke.xently.features.products.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.products.data.domain.Product

@Entity(tableName = "products")
data class ProductEntity(
    val product: Product,
    @PrimaryKey
    val id: Long = product.id,
)
