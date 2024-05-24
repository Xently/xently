package com.kwanzatukule.features.customer.complaints.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.customer.complaints.presentation.CustomerComplaintNavigationGraphComponent.Child
import com.kwanzatukule.features.customer.complaints.presentation.entry.CustomerComplaintEntryScreen
import com.kwanzatukule.features.customer.complaints.presentation.list.CustomerComplaintListScreen

@Composable
fun CustomerComplaintNavigationGraph(
    component: CustomerComplaintNavigationGraphComponent,
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
                is Child.CustomerComplaintList -> CustomerComplaintListScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = contentWindowInsets,
                    topBar = topBar,
                )

                is Child.CustomerComplaintEntry -> CustomerComplaintEntryScreen(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
    )
}
