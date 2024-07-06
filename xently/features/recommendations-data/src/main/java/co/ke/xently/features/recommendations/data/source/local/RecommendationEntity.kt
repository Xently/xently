package co.ke.xently.features.recommendations.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.recommendations.data.domain.RecommendationResponse

@Entity(tableName = "recommendations")
data class RecommendationEntity(
    val recommendation: RecommendationResponse,
    @PrimaryKey
    val id: Long = recommendation.store.id,
)
