package com.kwanzatukule.features.route.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.route.presentation.RouteNavigationGraphComponent.Child
import com.kwanzatukule.features.route.presentation.RouteNavigationGraphComponent.Configuration
import com.kwanzatukule.features.route.presentation.entry.RouteEntryComponent
import com.kwanzatukule.features.route.presentation.entry.RouteEntryComponentImpl
import com.kwanzatukule.features.route.presentation.list.RouteListComponent
import com.kwanzatukule.features.route.presentation.list.RouteListComponentImpl
import com.kwanzatukule.libraries.data.route.data.RouteRepository
import com.kwanzatukule.libraries.data.route.domain.Route

class RouteNavigationGraphComponentImpl(
    context: ComponentContext,
    component: RouteNavigationGraphComponent,
    private val repository: RouteRepository,
    private val navigateInIsolation: Boolean,
) : RouteNavigationGraphComponent by component, ComponentContext by context {
    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<Configuration, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        handleBackButton = true,
        childFactory = ::createChild,
        initialConfiguration = Configuration.RouteList,
    )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            is Configuration.RouteList -> Child.RouteList(
                component = RouteListComponentImpl(
                    context = context,
                    repository = repository,
                    component = object : RouteListComponent {
                        override fun onClickRouteEntry() {
                            if (navigateInIsolation) {
                                navigation.push(Configuration.RouteEntry(route = null))
                            } else {
                                this@RouteNavigationGraphComponentImpl.onClickRouteEntry()
                            }
                        }

                        override fun onClickRoute(route: Route) {
                            this@RouteNavigationGraphComponentImpl.onClickRoute(route)
                        }
                    },
                ),
            )

            is Configuration.RouteEntry -> Child.RouteEntry(
                component = RouteEntryComponentImpl(
                    context = context,
                    repository = repository,
                    component = object : RouteEntryComponent {
                        override fun handleBackPress() {
                            navigation.pop()
                        }
                    },
                ),
            )
        }
    }
}