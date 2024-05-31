package com.kwanzatukule.features.delivery.home.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.dispatch.presentation.DispatchList

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalDecomposeApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun HomeScreen(
    component: HomeComponent,
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
) {
    val childPages by component.childPages.subscribeAsState()
    Scaffold(modifier = modifier, contentWindowInsets = contentWindowInsets) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PrimaryTabRow(selectedTabIndex = childPages.selectedIndex) {
                Dispatch.Status.entries.forEach { status ->
                    Tab(
                        selected = childPages.selectedIndex == status.ordinal,
                        onClick = { component.selectPage(status.ordinal) },
                        text = {
                            Text(
                                text = stringResource(status.localeName),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }
            Pages(
                modifier = Modifier.weight(1f),
                pages = component.childPages,
                onPageSelected = component::selectPage,
                scrollAnimation = PagesScrollAnimation.Default,
            ) { _, page ->
                when (page) {
                    is HomeComponent.Child.Status -> DispatchList(
                        component = page.component,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@XentlyPreview
@Composable
private fun HomeScreenPreview() {
    KwanzaTukuleTheme {
        HomeScreen(
            component = HomeComponent.Fake,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
