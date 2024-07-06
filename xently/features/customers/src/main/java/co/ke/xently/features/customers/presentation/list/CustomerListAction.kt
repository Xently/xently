package co.ke.xently.features.customers.presentation.list

internal sealed interface CustomerListAction {
    class ChangeQuery(val query: String) : CustomerListAction
    class Search(val query: String) : CustomerListAction
}