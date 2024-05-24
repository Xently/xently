package com.kwanzatukule.features.delivery.landing.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.delivery.landing.presentation.home.HomeComponent
import kotlinx.serialization.Serializable

interface LandingNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()
    fun onSignInRequested()
    fun onSignOutRequested()

    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object Home : Configuration()
    }
}
