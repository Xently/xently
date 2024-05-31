package com.kwanzatukule.features.order.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.cart.presentation.list.ShoppingListScreen
import com.kwanzatukule.features.order.presentation.OrderNavigationGraphComponent.Child
import com.kwanzatukule.features.order.presentation.group.OrderGroupScreen

@Composable
fun OrderNavigationGraph(
    modifier: Modifier,
    component: OrderNavigationGraphComponent,
) {
    val childStack by component.childStack.subscribeAsState()
    Children(
        modifier = modifier,
        stack = childStack,
        animation = stackAnimation(),
        content = {
            when (val instance = it.instance) {
                is Child.OrderGroup -> OrderGroupScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )

                is Child.ShoppingList -> ShoppingListScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
    )
}
