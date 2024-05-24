package com.kwanzatukule.features.catalogue.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.catalogue.presentation.productdetail.ProductDetailComponent
import com.kwanzatukule.features.catalogue.presentation.productlist.ProductListComponent
import kotlinx.serialization.Serializable

interface CatalogueNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()
    fun handleBackPress()
    fun addToOrRemoveFromShoppingCart(product: Product)
    fun onClickShoppingCart()

    sealed class Child {
        data class ProductList(val component: ProductListComponent) : Child()
        data class ProductDetail(val component: ProductDetailComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data class ProductList(val category: Category?) : Configuration()

        @Serializable
        data class ProductDetail(val product: Product) : Configuration()
    }
}
