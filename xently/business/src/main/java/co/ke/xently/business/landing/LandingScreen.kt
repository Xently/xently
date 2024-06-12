package co.ke.xently.business.landing

import androidx.compose.foundation.basicMarquee
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.window.core.layout.WindowWidthSizeClass
import co.ke.xently.business.landing.components.LandingScreenContent
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.stores.data.domain.Store

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
    onClickAddStore: () -> Unit,
    onClickEditStore: (Store) -> Unit,
    onClickAddProduct: () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    onClickAddNewReviewCategory: () -> Unit,
    onClickViewComments: (ReviewCategory) -> Unit,
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.DASHBOARD) }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val customNavSuiteType = with(adaptiveInfo) {
        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
            NavigationSuiteType.NavigationDrawer
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }

    NavigationSuiteScaffold(
        modifier = modifier,
        layoutType = customNavSuiteType,
        navigationSuiteItems = {
            AppDestination.entries.forEach { destination ->
                val showLabel =
                    adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
                            && customNavSuiteType == NavigationSuiteType.NavigationBar
                item(
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = stringResource(destination.contentDescription),
                        )
                    },
                    label = if (showLabel) null else {
                        {
                            Text(
                                text = stringResource(destination.label),
                                modifier = Modifier.basicMarquee(),
                            )
                        }
                    },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination },
                )
            }
        },
    ) {
        val navigationIcon: @Composable () -> Unit = {
            IconButton(
                onClick = {
                    /*scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }*/
                },
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Notes,
                    contentDescription = null,
                )
            }
        }
        LandingScreenContent(
            currentDestination = currentDestination,
            onClickSelectShop = onClickSelectShop,
            onClickSelectBranch = onClickSelectBranch,
            onClickEditStore = onClickEditStore,
            onClickAddStore = onClickAddStore,
            navigationIcon = navigationIcon,
            onClickEditProduct = onClickEditProduct,
            onClickAddProduct = onClickAddProduct,
            onClickViewComments = onClickViewComments,
            onClickAddNewReviewCategory = onClickAddNewReviewCategory,
        )
    }
}
