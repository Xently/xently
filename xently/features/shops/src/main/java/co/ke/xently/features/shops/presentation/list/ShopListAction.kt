package co.ke.xently.features.shops.presentation.list

import co.ke.xently.features.shops.data.domain.Shop

internal sealed interface ShopListAction {
    class ChangeQuery(val query: String) : ShopListAction
    class Search(val query: String) : ShopListAction
    class DeleteShop(val shop: Shop) : ShopListAction
    class SelectShop(val shop: Shop) : ShopListAction
}