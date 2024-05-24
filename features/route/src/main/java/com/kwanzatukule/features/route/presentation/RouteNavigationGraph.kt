package com.kwanzatukule.features.route.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.route.presentation.RouteNavigationGraphComponent.Child
import com.kwanzatukule.features.route.presentation.entry.RouteEntryScreen
import com.kwanzatukule.features.route.presentation.list.RouteListScreen

@Composable
fun RouteNavigationGraph(
    component: RouteNavigationGraphComponent,
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets,
    topBar: @Composable () -> Unit,
) {
    val childStack by component.childStack.subscribeAsState()
    Children(
        modifier = modifier,
        stack = childStack,
        animation = stackAnimation(),
        content = {
            when (val instance = it.instance) {
                is Child.RouteEntry -> RouteEntryScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )

                is Child.RouteList -> RouteListScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = contentWindowInsets,
                    topBar = topBar,
                )
            }
        },
    )
}
