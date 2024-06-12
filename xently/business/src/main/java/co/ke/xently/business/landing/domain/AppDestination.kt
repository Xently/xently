package co.ke.xently.business.landing.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.ui.graphics.vector.ImageVector
import co.ke.xently.business.R

enum class AppDestination(
    @StringRes
    val label: Int,
    val icon: ImageVector,
    @StringRes
    val contentDescription: Int,
) {
    DASHBOARD(
        R.string.app_destination_dashboard,
        Icons.Default.Dashboard,
        R.string.app_destination_dashboard
    ),
    PRODUCTS(
        R.string.app_destination_products,
        Icons.Default.Description,
        R.string.app_destination_products
    ),
    CUSTOMERS(
        R.string.app_destination_customers,
        Icons.Default.Groups,
        R.string.app_destination_customers
    ),
    NOTIFICATIONS(
        R.string.app_destination_notifications,
        Icons.Default.Notifications,
        R.string.app_destination_notifications
    ),
    REVIEWS(
        R.string.app_destination_reviews,
        Icons.Default.Reviews,
        R.string.app_destination_reviews
    ),
}