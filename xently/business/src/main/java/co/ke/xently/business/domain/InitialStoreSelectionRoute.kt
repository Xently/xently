package co.ke.xently.business.domain

import kotlinx.serialization.Serializable


sealed interface InitialStoreSelectionRoute {
    @Serializable
    data object SelectShop : InitialStoreSelectionRoute

    @Serializable
    data object SelectStore : InitialStoreSelectionRoute
}