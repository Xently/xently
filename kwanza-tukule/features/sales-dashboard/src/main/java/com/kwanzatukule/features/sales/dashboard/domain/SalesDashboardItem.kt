package com.kwanzatukule.features.sales.dashboard.domain

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class SalesDashboardItem(
    val name: String,
    val target: Int,
    val actual: Int,
    val status: Status,
) {
    enum class Status {
        SHORT,
        AHEAD,
    }
}