package co.ke.xently.features.stores.presentation.list

import co.ke.xently.features.stores.data.domain.Store

internal sealed interface StoreListAction {
    data object FetchStoresFromActivatedShop : StoreListAction

    class ChangeQuery(val query: String) : StoreListAction
    class Search(val query: String) : StoreListAction
    class DeleteStore(val store: Store) : StoreListAction
    class SelectStore(val store: Store) : StoreListAction
}