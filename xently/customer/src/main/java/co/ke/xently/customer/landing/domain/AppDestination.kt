package co.ke.xently.customer.landing.domain

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import co.ke.xently.customer.R

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
    SCOREBOARD(
        R.string.app_destination_scoreboard,
        Icons.Default.Explore,
        R.string.app_destination_scoreboard
    ),
    NOTIFICATIONS(
        R.string.app_destination_notifications,
        Icons.Default.Notifications,
        R.string.app_destination_notifications
    ),
    PROFILE(
        R.string.app_destination_profile,
        Icons.Default.Person,
        R.string.app_destination_profile
    ),
}