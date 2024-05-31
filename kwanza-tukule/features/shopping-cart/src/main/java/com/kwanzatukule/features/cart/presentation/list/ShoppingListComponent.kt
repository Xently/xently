package com.kwanzatukule.features.cart.presentation.list

import androidx.paging.PagingData
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.cart.domain.ShoppingCart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

interface ShoppingListComponent {
    val shoppingList: Flow<PagingData<ShoppingCart.Item>> get() = throw NotImplementedError()
    val uiState: Value<ShoppingListUiState> get() = throw NotImplementedError()
    val event: Flow<ShoppingListEvent> get() = flow { }
    fun handleBackPress()

    data class Fake(
        val state: ShoppingListUiState,
        @Suppress("PropertyName") val _shoppingList: PagingData<ShoppingCart.Item>,
    ) : ShoppingListComponent {
        override val uiState = MutableValue(state)
        override val shoppingList = flowOf(_shoppingList)
        override fun handleBackPress() {}
    }
}
