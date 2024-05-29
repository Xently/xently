package com.kwanzatukule.features.delivery.landing.presentation

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.home.presentation.HomeComponent
import com.kwanzatukule.features.delivery.profile.presentation.ProfileComponent

interface LandingNavigationGraphComponent {
    @OptIn(ExperimentalDecomposeApi::class)
    val childPages: Value<ChildPages<*, Child>> get() = throw NotImplementedError()
    fun selectPage(index: Int) {}
    fun onSignInRequested()
    fun onSignOutRequested()
    fun onClickViewRoute(dispatch: Dispatch)
    fun onClickViewOrders(dispatch: Dispatch)

    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
        data class Profile(val component: ProfileComponent) : Child()
    }
}
