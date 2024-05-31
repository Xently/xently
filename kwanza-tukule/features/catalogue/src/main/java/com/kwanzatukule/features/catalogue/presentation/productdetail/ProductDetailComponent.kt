package com.kwanzatukule.features.catalogue.presentation.productdetail

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.catalogue.domain.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ProductDetailComponent {
    val uiState: Value<ProductDetailUiState> get() = throw NotImplementedError()
    val event: Flow<ProductDetailEvent> get() = flow { }
    fun handleBackPress()
    fun addToOrRemoveFromShoppingCart(product: Product)

    open class Fake(state: ProductDetailUiState) : ProductDetailComponent {
        override val uiState: Value<ProductDetailUiState> = MutableValue(state)
        override fun handleBackPress() {}
        override fun addToOrRemoveFromShoppingCart(product: Product) {}
    }
}
