package com.kwanzatukule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.authentication.presentation.AuthenticationNavigationGraph
import com.kwanzatukule.features.core.domain.models.AuthenticationState
import com.kwanzatukule.features.core.presentation.App
import com.kwanzatukule.features.core.presentation.LocalAuthenticationState
import com.kwanzatukule.features.delivery.landing.presentation.LandingNavigationGraph
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
                        }
                    }
                }
            }
        }
    }
}
