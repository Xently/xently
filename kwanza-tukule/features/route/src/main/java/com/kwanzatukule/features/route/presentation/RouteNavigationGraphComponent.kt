package com.kwanzatukule.features.route.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.route.presentation.entry.RouteEntryComponent
import com.kwanzatukule.features.route.presentation.list.RouteListComponent
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.serialization.Serializable

interface RouteNavigationGraphComponent {
    val childStack: Value<ChildStack<Configuration, Child>> get() = throw NotImplementedError()
    fun handleBackPress()
    fun onClickRoute(route: Route)
    fun onClickRouteEntry()

    sealed class Child {
        data class RouteList(val component: RouteListComponent) : Child()
        data class RouteEntry(val component: RouteEntryComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object RouteList : Configuration()

        @Serializable
        data class RouteEntry(val route: Route?) : Configuration()
    }
}