package com.kwanzatukule.features.sales.landing.presentation

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.customer.complaints.presentation.CustomerComplaintNavigationGraphComponent
import com.kwanzatukule.features.customer.home.presentation.HomeComponent
import com.kwanzatukule.features.sales.customer.onboarding.presentation.CustomerOnboardingNavigationGraphComponent
import com.kwanzatukule.features.sales.dashboard.presentation.SalesDashboardComponent
import com.kwanzatukule.libraries.data.route.domain.Route

interface LandingNavigationGraphComponent {
    @OptIn(ExperimentalDecomposeApi::class)
    val childPages: Value<ChildPages<*, Child>> get() = throw NotImplementedError()
    fun selectPage(index: Int) {}
    fun onSignInRequested()
    fun onSignOutRequested()
    fun navigateToCatalogue(category: Category?)
    fun navigateToProductDetail(product: Product)
    fun navigateToShoppingCart()
    fun onClickRoute(route: Route)
    fun onClickRouteEntry()

    sealed class Child {
        data class Dashboard(val component: SalesDashboardComponent) : Child()
        data class Catalogue(val component: HomeComponent) : Child()
        data class CustomerOnboarding(val component: CustomerOnboardingNavigationGraphComponent) :
            Child()

        data class CustomerComplaint(val component: CustomerComplaintNavigationGraphComponent) :
            Child()
    }
}
