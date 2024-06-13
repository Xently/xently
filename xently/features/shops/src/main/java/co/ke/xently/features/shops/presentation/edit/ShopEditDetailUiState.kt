package co.ke.xently.features.shops.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.shops.data.domain.Shop

@Stable
data class ShopEditDetailUiState(
    val categoryName: String = "",
    val shop: Shop = Shop(id = -1, name = ""),
    val merchant: Merchant = Merchant(
        id = -1,
        firstName = "",
        lastName = "",
        emailAddress = "",
    ),
    val name: String = shop.name,
    val website: String = shop.onlineShopUrl ?: "",
    val merchantFirstName: String = merchant.firstName,
    val merchantLastName: String = merchant.lastName,
    val merchantEmailAddress: String = merchant.emailAddress,
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
)