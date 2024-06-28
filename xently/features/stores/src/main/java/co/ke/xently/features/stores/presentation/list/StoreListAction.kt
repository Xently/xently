package co.ke.xently.features.stores.presentation.list

internal sealed interface StoreListAction {
    class ChangeQuery(val query: String) : StoreListAction
    class Search(val query: String) : StoreListAction
}