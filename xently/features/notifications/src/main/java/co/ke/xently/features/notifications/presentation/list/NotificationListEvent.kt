package co.ke.xently.features.notifications.presentation.list

import co.ke.xently.features.notifications.presentation.utils.UiText


internal sealed interface NotificationListEvent {
    data class Error(
        val error: UiText,
        val type: co.ke.xently.features.notifications.data.domain.error.Error,
    ) : NotificationListEvent

    data class Success(val action: NotificationListAction) : NotificationListEvent
}
