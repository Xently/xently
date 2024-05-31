package com.kwanzatukule.features.customer.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.customer.presentation.CustomerNavigationGraphComponent.Child
import com.kwanzatukule.features.customer.presentation.entry.CustomerEntryScreen
import com.kwanzatukule.features.customer.presentation.list.CustomerListScreen

@Composable
fun CustomerNavigationGraph(
    component: CustomerNavigationGraphComponent,
    modifier: Modifier = Modifier,
) {
    val childStack by component.childStack.subscribeAsState()
    Children(
        modifier = modifier,
        stack = childStack,
        animation = stackAnimation(),
        content = {
            when (val instance = it.instance) {
                is Child.CustomerEntry -> CustomerEntryScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )

                is Child.CustomerList -> CustomerListScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
    )
}
