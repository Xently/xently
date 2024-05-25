package com.kwanzatukule

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.authentication.data.UserRepository
import com.kwanzatukule.features.authentication.presentation.AuthenticationNavigationGraphComponent
import com.kwanzatukule.features.cart.data.ShoppingCartRepository
import com.kwanzatukule.features.cart.presentation.ShoppingCartNavigationGraphComponent
import com.kwanzatukule.features.cart.presentation.ShoppingCartNavigationGraphComponentImpl
import com.kwanzatukule.features.catalogue.data.CatalogueRepository
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.catalogue.presentation.CatalogueNavigationGraphComponent
import com.kwanzatukule.features.catalogue.presentation.CatalogueNavigationGraphComponentImpl
import com.kwanzatukule.features.catalogue.presentation.NavigationScreen
import com.kwanzatukule.features.core.domain.AuthenticationStateManager
import com.kwanzatukule.features.core.domain.models.AuthenticationState
import com.kwanzatukule.features.customer.landing.data.LandingRepository
import com.kwanzatukule.features.customer.landing.presentation.LandingNavigationGraphComponent
import com.kwanzatukule.features.customer.landing.presentation.LandingNavigationGraphComponentImpl
import com.kwanzatukule.features.order.data.OrderRepository
import com.kwanzatukule.features.order.presentation.OrderSummaryComponent
import com.kwanzatukule.features.order.presentation.OrderSummaryComponentImpl
import com.kwanzatukule.features.route.presentation.list.RouteListComponent
import com.kwanzatukule.features.route.presentation.list.RouteListComponentImpl
import com.kwanzatukule.libraries.data.customer.data.CustomerRepository
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.data.RouteRepository
import com.kwanzatukule.libraries.data.route.domain.Route
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import timber.log.Timber
import javax.inject.Inject


@ActivityScoped
class RootComponent @Inject constructor(
    context: ComponentContext,
    private val userRepository: UserRepository,
    private val landingRepository: LandingRepository,
    private val catalogueRepository: CatalogueRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val orderRepository: OrderRepository,
    private val routeRepository: RouteRepository,
    private val customerRepository: CustomerRepository,
) : AuthenticationStateManager, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val isSignOutInProgress = MutableStateFlow(false)

    val authenticationState = isSignOutInProgress
        .combine(userRepository.getCurrentUser()) { signingOut, currentUser ->
            AuthenticationState(
                currentUser = currentUser,
                isSignOutInProgress = signingOut,
            )
        }
        .onEach { Timber.tag("RootComponent").d("Authentication state: %s", it) }
        .shareIn(
            componentScope,
            SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 5_000,
                replayExpirationMillis = 5_000,
            ),
        )
    val shoppingCart = shoppingCartRepository.getShoppingCart().shareIn(
        componentScope,
        SharingStarted.WhileSubscribed(
            stopTimeoutMillis = 5_000,
            replayExpirationMillis = 5_000,
        ),
    )

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val navigation = StackNavigation<Configuration>()
    val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        handleBackButton = true,
        childFactory = ::createChild,
        initialConfiguration = Configuration.Landing,
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            Configuration.Authentication -> Child.Authentication(
                AuthenticationNavigationGraphComponent(
                    context = context,
                    repository = userRepository,
                    onBackPress = { navigation.pop() },
                )
            )

            Configuration.Landing -> Child.Landing(
                LandingNavigationGraphComponentImpl(
                    context = context,
                    landingRepository = landingRepository,
                    component = object : LandingNavigationGraphComponent {
                        override fun onSignInRequested() {
                            signIn()
                        }

                        override fun onSignOutRequested() {
                            signOut()
                        }

                        override fun navigateToCatalogue(category: Category?) {
                            val screen = NavigationScreen.Catalogue(category)
                            navigation.push(Configuration.Catalogue(screen))
                        }

                        override fun navigateToProductDetail(product: Product) {
                            val screen = NavigationScreen.ProductDetail(product)
                            navigation.push(Configuration.Catalogue(screen))
                        }

                        override fun navigateToShoppingCart() {
                            navigation.pushNew(Configuration.ShoppingCart)
                        }
                    },
                )
            )

            is Configuration.Catalogue -> Child.Catalogue(
                CatalogueNavigationGraphComponentImpl(
                    context = context,
                    screen = config.screen,
                    repository = catalogueRepository,
                    component = object : CatalogueNavigationGraphComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }

                        override fun onClickShoppingCart() {
                            navigation.push(Configuration.ShoppingCart)
                        }

                        override fun addToOrRemoveFromShoppingCart(product: Product) {
                            componentScope.launch {
                                shoppingCartRepository.addToOrRemoveFromShoppingCart(product)
                            }
                        }
                    },
                )
            )

            Configuration.ShoppingCart -> Child.ShoppingCart(
                ShoppingCartNavigationGraphComponentImpl(
                    context = context,
                    repository = shoppingCartRepository,
                    component = object : ShoppingCartNavigationGraphComponent {
                        override fun onBackPress() {
                            navigation.pop()
                        }

                        override fun handleCheckout() {
                            navigation.push(Configuration.SelectRoute)
                        }
                    },
                )
            )

            is Configuration.OrderSummary -> Child.OrderSummary(
                component = OrderSummaryComponentImpl(
                    context = context,
                    route = config.route,
                    customer = config.customer,
                    repository = orderRepository,
                    component = object : OrderSummaryComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }

                        override fun onOrderPlaced() {
                            navigation.popWhile {
                                it !is Configuration.Landing
                            }
                        }

                        override fun onClickUpdateRoute() {
                            navigation.popWhile {
                                it !is Configuration.SelectRoute
                            }
                        }

                        override fun onClickUpdateCustomer() {
                            TODO("Not yet implemented")
                        }

                        override fun onClickUpdateShoppingCart() {
                            navigation.popWhile {
                                it !is Configuration.ShoppingCart
                            }
                        }
                    },
                )
            )

            Configuration.SelectRoute -> Child.SelectRoute(
                RouteListComponentImpl(
                    context = context,
                    component = object : RouteListComponent {
                        override fun onClickRouteEntry() {
                            TODO("Not yet implemented")
                        }

                        override fun onClickRoute(route: Route) {
                            componentScope.launch {
                                navigation.push(
                                    Configuration.OrderSummary(
                                        route,
                                        customer = customerRepository.getMyCustomer(),
                                    )
                                )
                            }
                        }
                    },
                    repository = routeRepository
                )
            )
        }
    }

    override fun signIn() {
        navigation.push(Configuration.Authentication)
    }

    override fun signOut() {
        componentScope.launch {
            isSignOutInProgress.update {
                true
            }
            userRepository.signOut()
        }.invokeOnCompletion {
            isSignOutInProgress.update {
                false
            }
        }
    }

    fun handleBackPress() {
        navigation.pop()
    }

    @Stable
    sealed class Child {
        data class Authentication(val component: AuthenticationNavigationGraphComponent) : Child()
        data class Landing(val component: LandingNavigationGraphComponent) : Child()
        data class ShoppingCart(val component: ShoppingCartNavigationGraphComponent) : Child()
        data class Catalogue(val component: CatalogueNavigationGraphComponent) : Child()
        data class SelectRoute(val component: RouteListComponent) : Child()
        data class OrderSummary(val component: OrderSummaryComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object Authentication : Configuration()

        @Serializable
        data object Landing : Configuration()

        @Serializable
        data object ShoppingCart : Configuration()

        @Serializable
        data class Catalogue(val screen: NavigationScreen) : Configuration()

        @Serializable
        data object SelectRoute : Configuration()

        @Serializable
        data class OrderSummary(val route: Route, val customer: Customer) : Configuration()
    }
}