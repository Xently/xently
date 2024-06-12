package co.ke.xently.features.notifications.presentation.list

import androidx.compose.runtime.Stable
import co.ke.xently.features.notifications.data.domain.Notification

@Stable
internal data class NotificationListUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
    val currentUserRanking: Notification? = null,
)