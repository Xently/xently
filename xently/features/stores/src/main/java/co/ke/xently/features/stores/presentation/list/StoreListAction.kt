package co.ke.xently.features.stores.presentation.list

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.stores.data.domain.Store

internal sealed interface StoreListAction {
    class SelectCategory(val category: StoreCategory) : StoreListAction
    class RemoveCategory(val category: StoreCategory) : StoreListAction
    class ChangeQuery(val query: String) : StoreListAction
    class Search(val query: String) : StoreListAction
    class DeleteStore(val store: Store) : StoreListAction
    class SelectStore(val store: Store) : StoreListAction
}