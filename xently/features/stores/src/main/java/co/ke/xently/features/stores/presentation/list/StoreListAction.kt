package co.ke.xently.features.stores.presentation.list

import co.ke.xently.features.stores.data.domain.Store

internal sealed interface StoreListAction {
    class ChangeQuery(val query: String) : StoreListAction
    class Search(val query: String) : StoreListAction
    class ToggleBookmark(val store: Store) : StoreListAction
}