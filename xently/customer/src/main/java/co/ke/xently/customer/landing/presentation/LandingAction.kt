package co.ke.xently.customer.landing.presentation

import co.ke.xently.features.shops.data.domain.Shop

sealed interface LandingAction {
    data object ClickSignOut : LandingAction
    class SelectShop(val shop: Shop) : LandingAction
}