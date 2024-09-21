package co.ke.xently.features.recommendations.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import co.ke.xently.features.recommendations.data.domain.RecommendationResponse
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "recommendations",
    primaryKeys = ["id", "lookupKey"],
)
data class RecommendationEntity(
    val recommendation: RecommendationResponse,
    @ColumnInfo(index = true)
    val id: Long = recommendation.store.id,
    @ColumnInfo(defaultValue = LookupKeyManager.DEFAULT_KEY, index = true)
    val lookupKey: String = LookupKeyManager.DEFAULT_KEY,
    @ColumnInfo(defaultValue = "32400000", index = true)
    val dateSaved: Instant = Clock.System.now(),
)
