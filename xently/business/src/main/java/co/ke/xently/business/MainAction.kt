package co.ke.xently.business

import co.ke.xently.features.shops.data.domain.Shop

sealed interface MainAction {
    data object ClickSignOut : MainAction
    class SelectShop(val shop: Shop) : MainAction
}