package co.ke.xently.features.stores.presentation.list.selection

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.stores.data.domain.Store

internal sealed interface StoreSelectionListAction {
    class SelectCategory(val category: StoreCategory) : StoreSelectionListAction
    class RemoveCategory(val category: StoreCategory) : StoreSelectionListAction
    class ChangeQuery(val query: String) : StoreSelectionListAction
    class Search(val query: String) : StoreSelectionListAction
    class DeleteStore(val store: Store) : StoreSelectionListAction
    class SelectStore(val store: Store) : StoreSelectionListAction
}