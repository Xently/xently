package co.ke.xently.features.reviewcategory.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "review_categories",
    primaryKeys = ["name", "lookupKey"],
)
data class ReviewCategoryEntity(
    val reviewCategory: ReviewCategory,
    @ColumnInfo(index = true)
    val name: String = reviewCategory.name,
    @ColumnInfo(defaultValue = LookupKeyManager.DEFAULT_KEY, index = true)
    val lookupKey: String = LookupKeyManager.DEFAULT_KEY,
    @ColumnInfo(defaultValue = "32400000", index = true)
    val dateSaved: Instant = Clock.System.now(),
)
