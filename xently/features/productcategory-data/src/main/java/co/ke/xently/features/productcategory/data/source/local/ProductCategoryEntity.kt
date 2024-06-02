package co.ke.xently.features.productcategory.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.productcategory.data.domain.ProductCategory

@Entity(tableName = "product_categories")
data class ProductCategoryEntity(
    val productCategory: ProductCategory,
    @PrimaryKey
    val name: String = productCategory.name,
)
