package com.kwanzatukule

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.authentication.data.UserRepository
import com.kwanzatukule.features.authentication.presentation.AuthenticationNavigationGraphComponent
import co.ke.xently.libraries.data.auth.AuthenticationState
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.landing.data.LandingRepository
import com.kwanzatukule.features.delivery.landing.presentation.LandingNavigationGraphComponent
import com.kwanzatukule.features.delivery.landing.presentation.LandingNavigationGraphComponentImpl
import com.kwanzatukule.features.delivery.route.presentation.DispatchRouteNavigationGraphComponent
import com.kwanzatukule.features.delivery.route.presentation.DispatchRouteNavigationGraphComponentImpl
import com.kwanzatukule.features.order.data.OrderRepository
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.OrderNavigationGraphComponent
import com.kwanzatukule.features.order.presentation.OrderNavigationGraphComponentImpl
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
    private val orderRepository: OrderRepository,
    private val landingRepository: LandingRepository,
) : co.ke.xently.libraries.data.auth.AuthenticationStateManager, ComponentContext by context {
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
                    repository = landingRepository,
                    component = object : LandingNavigationGraphComponent {
                        override fun onSignInRequested() {
                            signIn()
                        }

                        override fun onSignOutRequested() {
                            signOut()
                        }

                        override fun onClickViewRoute(dispatch: Dispatch) {
                            navigation.push(Configuration.DispatchRoute(dispatch))
                        }

                        override fun onClickViewOrders(dispatch: Dispatch) {
                            navigation.push(Configuration.DispatchOrders(dispatch))
                        }
                    },
                )
            )

            is Configuration.DispatchRoute -> Child.DispatchRoute(
                dispatch = config.dispatch,
                component = DispatchRouteNavigationGraphComponentImpl(
                    context = context,
                    repository = orderRepository,
                    orderStatus = when (config.dispatch.status) {
                        Dispatch.Status.Delivered -> Order.Status.Delivered
                        Dispatch.Status.Pending -> Order.Status.Pending
                        Dispatch.Status.Cancelled -> Order.Status.Cancelled
                    },
                    component = object : DispatchRouteNavigationGraphComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }
                    },
                )
            )

            is Configuration.DispatchOrders -> Child.DispatchOrders(
                OrderNavigationGraphComponentImpl(
                    context = context,
                    repository = orderRepository,
                    component = object : OrderNavigationGraphComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }
                    },
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

    @Stable
    sealed class Child {
        data class Authentication(val component: AuthenticationNavigationGraphComponent) : Child()
        data class Landing(val component: LandingNavigationGraphComponent) : Child()
        data class DispatchOrders(val component: OrderNavigationGraphComponent) : Child()
        data class DispatchRoute(
            val dispatch: Dispatch,
            val component: DispatchRouteNavigationGraphComponent,
        ) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object Authentication : Configuration()

        @Serializable
        data object Landing : Configuration()

        @Serializable
        data class DispatchRoute(val dispatch: Dispatch) : Configuration()

        @Serializable
        data class DispatchOrders(val dispatch: Dispatch) : Configuration()
    }
}