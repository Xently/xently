package co.ke.xently.features.shops.data.source

import kotlinx.serialization.Serializable

@Serializable
internal data class ShopAndMerchantSaveRequest(val shop: Shop, val merchant: Merchant) {
    @Serializable
    data class Shop(val name: String, val onlineShopUrl: String?)

    @Serializable
    data class Merchant(val firstName: String, val lastName: String, val emailAddress: String)
}