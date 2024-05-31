package com.kwanzatukule.features.customer.landing.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.cart.presentation.components.ShoppingCartBadge
import com.kwanzatukule.features.customer.home.presentation.HomeScreen
import com.kwanzatukule.features.customer.landing.presentation.LandingNavigationGraphComponent.Child

@OptIn(ExperimentalMaterial3Api::class)
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
                is Child.Home -> Scaffold(
                    modifier = modifier,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(text = title) },
                            navigationIcon = {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Open navigation menu",
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                    )
                                }
                                ShoppingCartBadge(
                                    onClick = component::navigateToShoppingCart,
                                )

                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "Open options menu",
                                    )
                                }
                            }
                        )
                    },
                ) { paddingValues ->
                    HomeScreen(
                        component = instance.component,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                }
            }
        },
    )
}
