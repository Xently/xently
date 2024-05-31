package com.kwanzatukule.features.order.presentation.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.list.OrderList

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalDecomposeApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun OrderGroupScreen(
    component: OrderGroupComponent,
    modifier: Modifier = Modifier,
) {
    val childPages by component.childPages.subscribeAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Dispatch orders") },
                navigationIcon = {
                    IconButton(onClick = component::handleBackPress) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            PrimaryTabRow(selectedTabIndex = childPages.selectedIndex) {
                Order.Status.entries.forEach { status ->
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
                    is OrderGroupComponent.Child.Status -> OrderList(
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
private fun OrderGroupScreenPreview() {
    KwanzaTukuleTheme {
        OrderGroupScreen(
            component = OrderGroupComponent.Fake,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
