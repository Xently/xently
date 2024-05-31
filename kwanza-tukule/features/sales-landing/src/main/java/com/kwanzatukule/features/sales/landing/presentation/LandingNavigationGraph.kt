package com.kwanzatukule.features.sales.landing.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.cart.presentation.components.ShoppingCartBadge
import com.kwanzatukule.features.customer.complaints.presentation.CustomerComplaintNavigationGraph
import com.kwanzatukule.features.customer.home.presentation.HomeScreen
import com.kwanzatukule.features.sales.customer.onboarding.presentation.CustomerOnboardingNavigationGraph
import com.kwanzatukule.features.sales.dashboard.presentation.SalesDashboardScreen
import com.kwanzatukule.features.sales.landing.domain.Page
import com.kwanzatukule.features.sales.landing.presentation.LandingNavigationGraphComponent.Child

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalDecomposeApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun LandingNavigationGraph(
    component: LandingNavigationGraphComponent,
    title: String,
    modifier: Modifier,
) {
    val childPages by component.childPages.subscribeAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title, modifier = Modifier.basicMarquee()) },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Open navigation menu",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                    ShoppingCartBadge(
                        onClick = component::navigateToShoppingCart,
                    )

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Open options menu",
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                Page.entries.forEach { page ->
                    NavigationBarItem(
                        icon = { Icon(page.icon, contentDescription = stringResource(page.title)) },
                        label = { Text(stringResource(page.title)) },
                        selected = childPages.selectedIndex == page.ordinal,
                        onClick = { component.selectPage(page.ordinal) },
                    )
                }
            }
        },
    ) { paddingValues ->
        Pages(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            pages = component.childPages,
            onPageSelected = component::selectPage,
            scrollAnimation = PagesScrollAnimation.Default,
        ) { _, page ->
            when (page) {
                is Child.Dashboard -> SalesDashboardScreen(
                    component = page.component,
                    modifier = Modifier.fillMaxSize(),
                )

                is Child.Catalogue -> HomeScreen(
                    component = page.component,
                    modifier = Modifier.fillMaxSize(),
                )

                is Child.CustomerOnboarding -> CustomerOnboardingNavigationGraph(
                    component = page.component,
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.waterfall,
                )

                is Child.CustomerComplaint -> CustomerComplaintNavigationGraph(
                    component = page.component,
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.waterfall,
                )
            }
        }
    }
}
