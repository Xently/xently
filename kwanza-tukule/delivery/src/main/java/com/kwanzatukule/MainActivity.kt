package com.kwanzatukule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import co.ke.xently.libraries.data.auth.AuthenticationState
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.openUrl
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.authentication.presentation.AuthenticationNavigationGraph
import com.kwanzatukule.features.core.presentation.App
import com.kwanzatukule.features.delivery.dispatch.presentation.components.DispatchRouteBottomBarCard
import com.kwanzatukule.features.delivery.landing.presentation.LandingNavigationGraph
import com.kwanzatukule.features.delivery.route.presentation.DispatchRouteNavigationGraph
import com.kwanzatukule.features.order.domain.error.DataError
import com.kwanzatukule.features.order.presentation.OrderNavigationGraph
import com.kwanzatukule.features.order.presentation.list.OrderListEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var root: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App {
                val authenticationState by root.authenticationState.collectAsState(
                    AuthenticationState()
                )
                CompositionLocalProvider(LocalAuthenticationState provides authenticationState) {
                    val childStack by root.childStack.subscribeAsState()
                    Children(stack = childStack, animation = stackAnimation(scale())) { child ->
                        when (val instance = child.instance) {
                            is RootComponent.Child.Landing -> LandingNavigationGraph(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                                title = stringResource(R.string.app_name),
                            )

                            is RootComponent.Child.Authentication -> AuthenticationNavigationGraph(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                            )

                            is RootComponent.Child.DispatchRoute -> DispatchRouteNavigationGraph(
                                component = instance.component,
                                modifier = Modifier.fillMaxSize(),
                                bottomBar = {
                                    val snackbarHostState = remember { SnackbarHostState() }

                                    val context = LocalContext.current

                                    LaunchedEffect(Unit) {
                                        instance.component.event.collect {
                                            when (it) {
                                                is OrderListEvent.Error -> {
                                                    val result = snackbarHostState.showSnackbar(
                                                        it.error.asString(context = context),
                                                        duration = SnackbarDuration.Long,
                                                        actionLabel = if (it.type is DataError.Network) "Retry" else null,
                                                    )

                                                    when (result) {
                                                        SnackbarResult.Dismissed -> {

                                                        }

                                                        SnackbarResult.ActionPerformed -> {

                                                        }
                                                    }
                                                }

                                                is OrderListEvent.FindNavigableLocations -> {
                                                    openUrl(url = it.directionNavigation.getGoogleMapsDirectionUrl())
                                                }
                                            }
                                        }
                                    }
                                    DispatchRouteBottomBarCard(
                                        dispatch = instance.dispatch,
                                        onClickStartJourney = { instance.component.findNavigableLocations() },
                                    )
                                },
                            )

                            is RootComponent.Child.DispatchOrders -> OrderNavigationGraph(
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
