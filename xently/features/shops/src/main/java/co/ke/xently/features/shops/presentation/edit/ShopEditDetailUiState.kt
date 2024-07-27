package co.ke.xently.features.shops.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.merchant.data.domain.error.EmailError as MerchantEmailError
import co.ke.xently.features.merchant.data.domain.error.NameError as MerchantNameError
import co.ke.xently.features.shops.data.domain.error.NameError as ShopNameError
import co.ke.xently.features.shops.data.domain.error.WebsiteError as ShopWebsiteError

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
    val nameError: ShopNameError? = null,
    val website: String = shop.onlineShopUrl ?: "",
    val websiteError: ShopWebsiteError? = null,
    val merchantFirstName: String = merchant.firstName,
    val merchantFirstNameError: MerchantNameError? = null,
    val merchantLastName: String = merchant.lastName,
    val merchantLastNameError: MerchantNameError? = null,
    val merchantEmailAddress: String = merchant.emailAddress,
    val merchantEmailAddressError: MerchantEmailError? = null,
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
) {
    val enableSaveButton: Boolean = !isLoading && !disableFields
    val isFormValid: Boolean = nameError == null
            && websiteError == null
            && merchantFirstNameError == null
            && merchantLastNameError == null
            && merchantEmailAddressError == null
}