package com.kwanzatukule.features.delivery.landing.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.delivery.landing.presentation.LandingNavigationGraphComponent.Child
import com.kwanzatukule.features.delivery.landing.presentation.home.HomeScreen

@Composable
fun LandingNavigationGraph(
    component: LandingNavigationGraphComponent,
    title: String,
    modifier: Modifier,
) {
    val childStack by component.childStack.subscribeAsState()
    Children(
        modifier = modifier,
        stack = childStack,
        animation = stackAnimation(),
        content = {
            when (val instance = it.instance) {
                is Child.Home -> HomeScreen(
                    title = title,
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
    )
}
