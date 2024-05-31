package com.kwanzatukule.features.delivery.route.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.cart.presentation.list.ShoppingListScreen
import com.kwanzatukule.features.delivery.route.presentation.DispatchRouteNavigationGraphComponent.Child
import com.kwanzatukule.features.delivery.route.presentation.route.DispatchRouteScreen

@Composable
fun DispatchRouteNavigationGraph(
    modifier: Modifier,
    component: DispatchRouteNavigationGraphComponent,
    bottomBar: @Composable () -> Unit = {},
) {
    val childStack by component.childStack.subscribeAsState()
    Children(
        modifier = modifier,
        stack = childStack,
        animation = stackAnimation(),
        content = {
            when (val instance = it.instance) {
                is Child.DispatchRoute -> DispatchRouteScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = bottomBar,
                )

                is Child.ShoppingList -> ShoppingListScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
    )
}
