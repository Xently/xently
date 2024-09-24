package co.ke.xently.features.reviews.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "reviews",
    primaryKeys = ["id", "lookupKey"],
)
data class ReviewEntity(
    val review: Review,
    @ColumnInfo(index = true)
    val id: String = review.links["self"]!!.hrefWithoutContentsFrom('{'),
    @ColumnInfo(defaultValue = LookupKeyManager.DEFAULT_KEY, index = true)
    val lookupKey: String = LookupKeyManager.DEFAULT_KEY,
    @ColumnInfo(defaultValue = "32400000", index = true)
    val dateSaved: Instant = Clock.System.now(),
)
