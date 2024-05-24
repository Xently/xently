package com.kwanzatukule.features.catalogue.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.catalogue.presentation.CatalogueNavigationGraphComponent.Child
import com.kwanzatukule.features.catalogue.presentation.productdetail.ProductDetailScreen
import com.kwanzatukule.features.catalogue.presentation.productlist.ProductListScreen

@Composable
fun CatalogueNavigationGraph(
    modifier: Modifier,
    component: CatalogueNavigationGraphComponent,
    shoppingCartBadge: @Composable () -> Unit,
) {
    val childStack by component.childStack.subscribeAsState()
    Children(
        modifier = modifier,
        stack = childStack,
        animation = stackAnimation(),
        content = {
            when (val instance = it.instance) {
                is Child.ProductList -> ProductListScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                    shoppingCartBadge = shoppingCartBadge,
                )

                is Child.ProductDetail -> ProductDetailScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                    shoppingCartBadge = shoppingCartBadge,
                )
            }
        },
    )
}
