package com.kwanzatukule.features.sales.landing.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.customer.complaints.presentation.CustomerComplaintNavigationGraphComponent
import com.kwanzatukule.features.customer.complaints.presentation.CustomerComplaintNavigationGraphComponentImpl
import com.kwanzatukule.features.customer.home.presentation.HomeComponent
import com.kwanzatukule.features.customer.home.presentation.HomeComponentImpl
import com.kwanzatukule.features.sales.customer.onboarding.presentation.CustomerOnboardingNavigationGraphComponent
import com.kwanzatukule.features.sales.customer.onboarding.presentation.CustomerOnboardingNavigationGraphComponentImpl
import com.kwanzatukule.features.sales.dashboard.domain.SalesDashboardItem
import com.kwanzatukule.features.sales.dashboard.presentation.SalesDashboardComponent
import com.kwanzatukule.features.sales.dashboard.presentation.SalesDashboardComponentImpl
import com.kwanzatukule.features.sales.landing.data.LandingRepository
import com.kwanzatukule.features.sales.landing.domain.Page
import com.kwanzatukule.features.sales.landing.presentation.LandingNavigationGraphComponent.Child
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
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
            Page.Dashboard -> Child.Dashboard(
                component = SalesDashboardComponentImpl(
                    context = context,
                    repository = repository,
                    component = object : SalesDashboardComponent {
                        override fun onItemClicked(dashboardItem: SalesDashboardItem) {

                        }
                    },
                ),
            )

            Page.Catalogue -> Child.Catalogue(
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

                        override fun navigateToCatalogue(category: Category?) {
                            this@LandingNavigationGraphComponentImpl.navigateToCatalogue(category)
                        }

                        override fun navigateToProductDetail(product: Product) {
                            this@LandingNavigationGraphComponentImpl.navigateToProductDetail(
                                product
                            )
                        }

                        override fun navigateToShoppingCart() {
                            this@LandingNavigationGraphComponentImpl.navigateToShoppingCart()
                        }
                    }
                ),
            )

            Page.Routes -> Child.CustomerOnboarding(
                component = CustomerOnboardingNavigationGraphComponentImpl(
                    context = context,
                    component = object : CustomerOnboardingNavigationGraphComponent {
                        override fun handleBackPress() {
                            TODO("Not yet implemented")
                        }

                        override fun onClickRoute(route: Route) {
                            this@LandingNavigationGraphComponentImpl.onClickRoute(route)
                        }

                        override fun onClickRouteEntry() {
                            this@LandingNavigationGraphComponentImpl.onClickRouteEntry()
                        }

                        override fun onClickCustomer(route: Route, customer: Customer) {
                            // not necessary for now
                        }
                    },
                    navigateInIsolation = false,
                    repository = repository
                ),
            )

            Page.Complaints -> Child.CustomerComplaint(
                component = CustomerComplaintNavigationGraphComponentImpl(
                    context = context,
                    repository = repository,
                    navigateInIsolation = false,
                    customer = null,
                    component = object : CustomerComplaintNavigationGraphComponent {
                        override fun handleBackPress() {
                            TODO("Not yet implemented")
                        }
                    }
                ),
            )
        }
    }
}