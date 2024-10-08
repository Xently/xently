package co.ke.xently.features.shops.presentation.edit

internal sealed interface ShopEditDetailAction {
    data object ClickSave : ShopEditDetailAction
    data object ClickSaveAndAddAnother : ShopEditDetailAction
    data object ClearFieldsForNewShop : ShopEditDetailAction

    class ChangeShopName(val name: String) : ShopEditDetailAction
    class ChangeShopWebsite(val website: String) : ShopEditDetailAction
    class ChangeMerchantFirstName(val merchantFirstName: String) : ShopEditDetailAction
    class ChangeMerchantLastName(val merchantLastName: String) : ShopEditDetailAction
    class ChangeMerchantEmailAddress(val merchantEmailAddress: String) : ShopEditDetailAction
}