package com.kwanzatukule.features.delivery.home.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.dispatch.presentation.DispatchListComponent
import com.kwanzatukule.features.delivery.dispatch.presentation.DispatchListComponentImpl
import com.kwanzatukule.features.delivery.home.data.HomeRepository
import com.kwanzatukule.features.delivery.home.presentation.HomeComponent.Child
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.serialization.serializer

@OptIn(ExperimentalDecomposeApi::class)
class HomeComponentImpl(
    context: ComponentContext,
    component: HomeComponent,
    private val repository: HomeRepository,
) : HomeComponent by component, ComponentContext by context {
    private val componentScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    private val navigation = PagesNavigation<Dispatch.Status>()
    override val childPages: Value<ChildPages<*, Child>> = childPages(
        source = navigation,
        serializer = serializer<Dispatch.Status>(),
        initialPages = {
            Pages(
                items = Dispatch.Status.entries,
                selectedIndex = Dispatch.Status.Pending.ordinal,
            )
        },
        childFactory = ::createChild,
    )

    override fun selectPage(index: Int) {
        navigation.select(index = index)
    }

    private fun createChild(config: Dispatch.Status, context: ComponentContext): Child {
        return Child.Status(
            component = DispatchListComponentImpl(
                context = context,
                repository = repository,
                status = config,
                component = object : DispatchListComponent {
                    override fun onClickViewRoute(dispatch: Dispatch) {
                        this@HomeComponentImpl.onClickViewRoute(dispatch)
                    }

                    override fun onClickViewOrders(dispatch: Dispatch) {
                        this@HomeComponentImpl.onClickViewOrders(dispatch)
                    }
                },
            ),
        )
    }
}