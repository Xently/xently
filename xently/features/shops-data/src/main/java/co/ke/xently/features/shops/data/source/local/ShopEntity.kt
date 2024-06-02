package co.ke.xently.features.shops.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.shops.data.domain.Shop

@Entity(tableName = "shops")
data class ShopEntity(
    val shop: Shop,
    @PrimaryKey
    val id: Long = shop.id,
)
