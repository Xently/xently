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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import co.ke.xently.libraries.data.auth.AuthenticationState
import com.kwanzatukule.features.core.presentation.App
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import com.kwanzatukule.features.customer.presentation.CustomerNavigationGraph
import com.kwanzatukule.features.customer.presentation.list.LocalCanViewMissedOpportunities
import com.kwanzatukule.features.order.presentation.summary.OrderSummaryScreen
import com.kwanzatukule.features.route.presentation.entry.RouteEntryScreen
import com.kwanzatukule.features.sales.customer.onboarding.presentation.CustomerOnboardingNavigationGraph
import com.kwanzatukule.features.sales.landing.presentation.LandingNavigationGraph
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
                    LocalCanViewMissedOpportunities provides true,
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

                            is Child.SelectRoute -> CustomerOnboardingNavigationGraph(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    TopAppBar(
                                        navigationIcon = {
                                            IconButton(onClick = instance.component::handleBackPress) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = "Go back"
                                                )
                                            }
                                        },
                                        title = { Text(text = "Routes") },
                                    )
                                },
                            )

                            is Child.CustomerList -> CustomerNavigationGraph(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                            )

                            is Child.RouteEntry -> RouteEntryScreen(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                            )

                            is Child.OrderSummary -> OrderSummaryScreen(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}
