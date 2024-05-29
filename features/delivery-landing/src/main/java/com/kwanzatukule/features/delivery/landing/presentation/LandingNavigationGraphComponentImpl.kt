package com.kwanzatukule.features.delivery.landing.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.home.presentation.HomeComponent
import com.kwanzatukule.features.delivery.home.presentation.HomeComponentImpl
import com.kwanzatukule.features.delivery.landing.data.LandingRepository
import com.kwanzatukule.features.delivery.landing.domain.Page
import com.kwanzatukule.features.delivery.landing.presentation.LandingNavigationGraphComponent.Child
import com.kwanzatukule.features.delivery.profile.presentation.ProfileComponent
import com.kwanzatukule.features.delivery.profile.presentation.ProfileComponentImpl
import kotlinx.serialization.serializer

@OptIn(ExperimentalDecomposeApi::class)
class LandingNavigationGraphComponentImpl(
    context: ComponentContext,
    component: LandingNavigationGraphComponent,
    private val repository: LandingRepository,
) : LandingNavigationGraphComponent by component, ComponentContext by context {
    private val navigation = PagesNavigation<Page>()
    override val childPages: Value<ChildPages<*, Child>> = childPages(
        source = navigation,
        serializer = serializer<Page>(),
        initialPages = {
            Pages(
                items = Page.entries,
                selectedIndex = Page.entries.first().ordinal,
            )
        },
        childFactory = ::createChild,
    )

    override fun selectPage(index: Int) {
        navigation.select(index = index)
    }

    private fun createChild(config: Page, context: ComponentContext): Child {
        return when (config) {
            Page.Home -> Child.Home(
                component = HomeComponentImpl(
                    context = context,
                    repository = repository,
                    component = object : HomeComponent {
                        override fun onSignInRequested() {
                            this@LandingNavigationGraphComponentImpl.onSignInRequested()
                        }

                        override fun onSignOutRequested() {
                            this@LandingNavigationGraphComponentImpl.onSignOutRequested()
                        }

                        override fun onClickViewRoute(dispatch: Dispatch) {
                            this@LandingNavigationGraphComponentImpl.onClickViewRoute(dispatch)
                        }

                        override fun onClickViewOrders(dispatch: Dispatch) {
                            this@LandingNavigationGraphComponentImpl.onClickViewOrders(dispatch)
                        }
                    },
                ),
            )

            Page.Profile -> Child.Profile(
                component = ProfileComponentImpl(
                    context = context,
                    component = object : ProfileComponent {
                        override fun onSignInRequested() {
                            this@LandingNavigationGraphComponentImpl.onSignInRequested()
                        }

                        override fun onSignOutRequested() {
                            this@LandingNavigationGraphComponentImpl.onSignOutRequested()
                        }
                    },
                ),
            )
        }
    }
}