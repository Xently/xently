package com.kwanzatukule.features.order.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kwanzatukule.features.order.R
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val route: Route,
    val customer: Customer,
    val status: Status = Status.Pending,
    val id: String = "",
) {
    enum class Status(
        @StringRes val localeName: Int,
        val icon: ImageVector,
        val color: @Composable () -> Color,
    ) {
        Delivered(
            localeName = R.string.dispatch_status_delivered,
            icon = Icons.Default.DeliveryDining,
            color = { MaterialTheme.colorScheme.primaryContainer },
        ),
        Pending(
            localeName = R.string.dispatch_status_pending,
            icon = Icons.Default.Pending,
            color = { MaterialTheme.colorScheme.secondaryContainer },
        ),
        Cancelled(
            localeName = R.string.dispatch_status_cancelled,
            icon = Icons.Default.Cancel,
            color = { MaterialTheme.colorScheme.error },
        ),
    }
}