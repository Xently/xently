package co.ke.xently.features.notifications.presentation.list

internal sealed interface NotificationListAction {
    class ChangeQuery(val query: String) : NotificationListAction
    class Search(val query: String) : NotificationListAction
}