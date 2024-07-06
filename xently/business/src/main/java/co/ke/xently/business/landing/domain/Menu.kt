package co.ke.xently.business.landing.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.ui.graphics.vector.ImageVector
import co.ke.xently.business.R

enum class Menu(
    val icon: ImageVector,
    @StringRes
    val title: Int,
    val isSelectable: Boolean = true,
) {
    DASHBOARD(
        icon = Icons.Default.Dashboard,
        title = R.string.app_destination_dashboard,
    ),
    PRODUCTS(
        icon = Icons.Default.Description,
        title = R.string.app_destination_products,
    ),
    CUSTOMERS(
        icon = Icons.Default.Groups,
        title = R.string.app_destination_customers,
    ),
    QR_CODE(
        icon = Icons.Default.QrCodeScanner,
        title = R.string.app_destination_qr_code,
        isSelectable = false,
    ),
    REVIEWS(
        icon = Icons.Default.StarRate,
        R.string.app_destination_reviews,
    ),
    SETTINGS(
        icon = Icons.Default.Settings,
        title = R.string.app_destination_settings,
        isSelectable = false,
    ),
    NOTIFICATIONS(
        icon = Icons.Default.Notifications,
        title = R.string.app_destination_notifications,
    )
}