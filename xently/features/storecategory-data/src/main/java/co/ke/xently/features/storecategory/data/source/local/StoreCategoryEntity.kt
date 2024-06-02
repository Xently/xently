package co.ke.xently.features.storecategory.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.storecategory.data.domain.StoreCategory

@Entity(tableName = "store_categories")
data class StoreCategoryEntity(
    val storeCategory: StoreCategory,
    @PrimaryKey
    val name: String = storeCategory.name,
)
