package co.ke.xently.customer.landing.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import co.ke.xently.customer.R

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
    SCOREBOARD(
        icon = Icons.Default.Explore,
        title = R.string.app_destination_scoreboard,
    ),
    SETTINGS(
        icon = Icons.Default.Settings,
        title = R.string.app_destination_settings,
        isSelectable = false,
    ),
    NOTIFICATIONS(
        icon = Icons.Default.Notifications,
        title = R.string.app_destination_notifications,
    ),
    PROFILE(
        icon = Icons.Default.Person,
        R.string.app_destination_profile,
    ),
}