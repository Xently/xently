package com.kwanzatukule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import co.ke.xently.libraries.data.auth.AuthenticationState
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.RootComponent.Child
import com.kwanzatukule.features.authentication.presentation.AuthenticationNavigationGraph
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.cart.presentation.LocalShoppingCartState
import com.kwanzatukule.features.cart.presentation.ShoppingCartNavigationGraph
import com.kwanzatukule.features.cart.presentation.components.ShoppingCartBadge
import com.kwanzatukule.features.catalogue.presentation.CatalogueNavigationGraph
import com.kwanzatukule.features.core.presentation.App
import com.kwanzatukule.features.customer.landing.presentation.LandingNavigationGraph
import com.kwanzatukule.features.order.presentation.summary.LocalCanUpdateOrderSummaryCustomer
import com.kwanzatukule.features.order.presentation.summary.OrderSummaryScreen
import com.kwanzatukule.features.route.presentation.list.LocalCanAddRoute
import com.kwanzatukule.features.route.presentation.list.RouteListScreen
import com.kwanzatukule.features.route.presentation.list.components.LocalCanViewRouteSummary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var root: RootComponent

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App {
                val authenticationState by root.authenticationState.collectAsState(
                    AuthenticationState()
                )
                val shoppingCart = root.shoppingCart.collectAsState(ShoppingCart(emptyList()))
                CompositionLocalProvider(
                    LocalShoppingCartState provides shoppingCart,
                    LocalAuthenticationState provides authenticationState,
                    LocalCanAddRoute provides false,
                    LocalCanViewRouteSummary provides false,
                    LocalCanUpdateOrderSummaryCustomer provides false,
                ) {
                    val childStack by root.childStack.subscribeAsState()
                    Children(stack = childStack, animation = stackAnimation(scale())) { child ->
                        when (val instance = child.instance) {
                            is Child.Landing -> LandingNavigationGraph(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                                title = stringResource(R.string.app_name),
                            )

                            is Child.Authentication -> AuthenticationNavigationGraph(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                            )

                            is Child.Catalogue -> CatalogueNavigationGraph(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                                shoppingCartBadge = {
                                    ShoppingCartBadge(onClick = instance.component::onClickShoppingCart)
                                },
                            )

                            is Child.ShoppingCart -> ShoppingCartNavigationGraph(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                            )

                            is Child.OrderSummary -> OrderSummaryScreen(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                            )

                            is Child.SelectRoute -> RouteListScreen(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                                contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
                                topBar = {
                                    TopAppBar(
                                        navigationIcon = {
                                            IconButton(onClick = root::handleBackPress) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = "Go back"
                                                )
                                            }
                                        },
                                        title = { Text(text = "Select route") },
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
