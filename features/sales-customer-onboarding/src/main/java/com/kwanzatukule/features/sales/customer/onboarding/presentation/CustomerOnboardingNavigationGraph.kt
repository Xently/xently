package com.kwanzatukule.features.sales.customer.onboarding.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.customer.presentation.CustomerNavigationGraph
import com.kwanzatukule.features.route.presentation.RouteNavigationGraph
import com.kwanzatukule.features.sales.customer.onboarding.presentation.CustomerOnboardingNavigationGraphComponent.Child

@Composable
fun CustomerOnboardingNavigationGraph(
    component: CustomerOnboardingNavigationGraphComponent,
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    topBar: @Composable () -> Unit = {},
) {
    val childStack by component.childStack.subscribeAsState()
    Children(
        modifier = modifier,
        stack = childStack,
        animation = stackAnimation(),
        content = {
            when (val instance = it.instance) {
                is Child.CustomerList -> CustomerNavigationGraph(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )

                is Child.RouteList -> RouteNavigationGraph(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = contentWindowInsets,
                    topBar = topBar,
                )
            }
        },
    )
}