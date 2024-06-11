package co.ke.xently.features.reviews.presentation.reviews

import com.aay.compose.barChart.model.BarParameters

internal data class BarGraphData(
    val xAxis: List<String>,
    val barParameters: List<BarParameters>,
)