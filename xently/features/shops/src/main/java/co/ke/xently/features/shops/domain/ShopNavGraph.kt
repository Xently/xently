package co.ke.xently.features.shops.domain

import kotlinx.serialization.Serializable

@Serializable
data object ShopNavGraph {
    @Serializable
    data object SelectShop

    @Serializable
    data object EditShop
}