package com.kwanzatukule.features.delivery.landing.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.delivery.home.presentation.HomeScreen
import com.kwanzatukule.features.delivery.landing.domain.Page
import com.kwanzatukule.features.delivery.landing.presentation.LandingNavigationGraphComponent.Child
import com.kwanzatukule.features.delivery.profile.presentation.ProfileScreen

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
            TopAppBar(
                title = { Text(text = title, modifier = Modifier.basicMarquee()) },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }

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
                is Child.Home -> HomeScreen(
                    component = page.component,
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.waterfall,
                )

                is Child.Profile -> ProfileScreen(
                    component = page.component,
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.waterfall,
                )
            }
        }
    }
}
