package co.ke.xently.features.reviews.presentation.reviews

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.presentation.utils.UiText
import com.aay.compose.barChart.model.BarParameters

sealed interface StatisticsResponse {
    data object Loading : StatisticsResponse

    data class Success(
        val data: ReviewCategory.Statistics,
        val barGraphData: BarGraphData,
    ) : StatisticsResponse {
        data class BarGraphData(
            val xAxis: List<String>,
            val barParameters: List<BarParameters>,
        )
    }

    data class Failure(
        val error: UiText,
        val type: co.ke.xently.features.reviews.data.domain.error.Error,
    ) : StatisticsResponse
}