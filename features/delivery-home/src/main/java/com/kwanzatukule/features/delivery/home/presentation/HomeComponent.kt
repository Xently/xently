package com.kwanzatukule.features.delivery.home.presentation

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.dispatch.presentation.DispatchListComponent

interface HomeComponent {
    @OptIn(ExperimentalDecomposeApi::class)
    val childPages: Value<ChildPages<*, Child>> get() = throw NotImplementedError()
    fun selectPage(index: Int) {}
    fun onSignInRequested()
    fun onSignOutRequested()
    fun onClickViewRoute(dispatch: Dispatch)
    fun onClickViewOrders(dispatch: Dispatch)

    object Fake : HomeComponent {
        override fun onSignInRequested() {}
        override fun onSignOutRequested() {}
        override fun onClickViewRoute(dispatch: Dispatch) {}
        override fun onClickViewOrders(dispatch: Dispatch) {}
    }

    sealed class Child {
        data class Status(val component: DispatchListComponent) : Child()
    }
}
