package co.ke.xently.customer

sealed interface MainAction {
    data object ClickSignOut : MainAction
}