package com.kwanzatukule.features.catalogue.presentation.productlist

import androidx.paging.PagingData
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.catalogue.domain.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ProductListComponent {
    val products: Flow<PagingData<Product>> get() = throw NotImplementedError()
    val uiState: Value<ProductListUiState> get() = throw NotImplementedError()
    val event: Flow<ProductListEvent> get() = flow { }
    fun handleBackPress()
    fun navigateToProductDetail(product: Product)
    fun addToOrRemoveFromShoppingCart(product: Product)

    class Fake(state: ProductListUiState = ProductListUiState()) : ProductListComponent {
        override val uiState: Value<ProductListUiState> = MutableValue(state)
        override fun handleBackPress() {}
        override fun navigateToProductDetail(product: Product) {}
        override fun addToOrRemoveFromShoppingCart(product: Product) {}
    }
}
