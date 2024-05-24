package com.kwanzatukule.features.catalogue.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.catalogue.data.CatalogueRepository
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.catalogue.presentation.CatalogueNavigationGraphComponent.Child
import com.kwanzatukule.features.catalogue.presentation.CatalogueNavigationGraphComponent.Configuration
import com.kwanzatukule.features.catalogue.presentation.productdetail.ProductDetailComponent
import com.kwanzatukule.features.catalogue.presentation.productdetail.ProductDetailComponentImpl
import com.kwanzatukule.features.catalogue.presentation.productlist.ProductListComponent
import com.kwanzatukule.features.catalogue.presentation.productlist.ProductListComponentImpl

class CatalogueNavigationGraphComponentImpl(
    context: ComponentContext,
    private val screen: NavigationScreen,
    private val repository: CatalogueRepository,
    component: CatalogueNavigationGraphComponent,
) : CatalogueNavigationGraphComponent by component, ComponentContext by context {
    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        handleBackButton = true,
        childFactory = ::createChild,
        initialConfiguration = when (screen) {
            is NavigationScreen.Catalogue -> Configuration.ProductList(screen.category)
            is NavigationScreen.ProductDetail -> Configuration.ProductDetail(screen.product)
        },
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            is Configuration.ProductList -> Child.ProductList(
                component = ProductListComponentImpl(
                    context = context,
                    repository = repository,
                    category = config.category,
                    component = object : ProductListComponent {
                        override fun handleBackPress() {
                            this@CatalogueNavigationGraphComponentImpl.handleBackPress()
                        }

                        override fun navigateToProductDetail(product: Product) {
                            navigation.push(Configuration.ProductDetail(product))
                        }

                        override fun addToOrRemoveFromShoppingCart(product: Product) {
                            this@CatalogueNavigationGraphComponentImpl.addToOrRemoveFromShoppingCart(
                                product
                            )
                        }
                    },
                ),
            )

            is Configuration.ProductDetail -> Child.ProductDetail(
                component = ProductDetailComponentImpl(
                    context = context,
                    repository = repository,
                    product = config.product,
                    component = object : ProductDetailComponent {
                        override fun handleBackPress() {
                            when (screen) {
                                is NavigationScreen.Catalogue -> {
                                    navigation.pop()
                                }

                                is NavigationScreen.ProductDetail -> {
                                    this@CatalogueNavigationGraphComponentImpl.handleBackPress()
                                }
                            }
                        }

                        override fun addToOrRemoveFromShoppingCart(product: Product) {
                            this@CatalogueNavigationGraphComponentImpl.addToOrRemoveFromShoppingCart(
                                product
                            )
                        }
                    },
                ),
            )
        }
    }
}