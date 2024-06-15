package co.ke.xently.business.landing.presentation

sealed interface LandingAction {
    data object ClickSignOut : LandingAction
}