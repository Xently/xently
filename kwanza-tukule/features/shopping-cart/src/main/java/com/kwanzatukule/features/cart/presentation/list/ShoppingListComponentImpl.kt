package com.kwanzatukule.features.cart.presentation.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.libraries.pagination.data.XentlyPagingSource
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.cart.data.ShoppingCartRepository
import com.kwanzatukule.features.cart.domain.ShoppingCart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class ShoppingListComponentImpl(
    context: ComponentContext,
    component: ShoppingListComponent,
    private val repository: ShoppingCartRepository,
) : ShoppingListComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val _uiState = MutableValue(ShoppingListUiState())
    override val uiState: Value<ShoppingListUiState> = _uiState

    private val _event = Channel<ShoppingListEvent>()
    override val event: Flow<ShoppingListEvent> = _event.receiveAsFlow()

    override val shoppingList: Flow<PagingData<ShoppingCart.Item>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        XentlyPagingSource { url ->
            repository.getShoppingList(url = url)
        }
    }.flow.cachedIn(componentScope)
}