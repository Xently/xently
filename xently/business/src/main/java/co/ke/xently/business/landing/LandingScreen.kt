package co.ke.xently.business.landing

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.waterfall
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.window.core.layout.WindowWidthSizeClass
import co.ke.xently.business.R
import co.ke.xently.features.customers.presentation.list.CustomerListScreen
import co.ke.xently.features.notifications.presentation.list.NotificationListScreen
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.presentation.list.ProductListScreen
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackScreen
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.active.ActiveStoreScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
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
        when (currentDestination) {
            AppDestination.DASHBOARD -> ActiveStoreScreen(
                onClickSelectShop = onClickSelectShop,
                onClickSelectBranch = onClickSelectBranch,
                onClickEdit = onClickEditStore,
                onClickMoreDetails = onClickEditStore,
                onClickAddStore = onClickAddStore,
            ) {
                CenterAlignedTopAppBar(
                    navigationIcon = navigationIcon,
                    title = { Text(text = stringResource(R.string.app_name)) },
                )
            }

            AppDestination.PRODUCTS -> ProductListScreen(
                onClickEditProduct = onClickEditProduct,
                onClickAddProduct = onClickAddProduct,
            ) {
                CenterAlignedTopAppBar(
                    navigationIcon = navigationIcon,
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.topbar_title_products)) },
                )
            }

            AppDestination.CUSTOMERS -> CustomerListScreen {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    navigationIcon = navigationIcon,
                    title = { Text(text = stringResource(R.string.topbar_title_customers)) },
                )
            }

            AppDestination.NOTIFICATIONS -> NotificationListScreen {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    navigationIcon = navigationIcon,
                    title = { Text(text = stringResource(R.string.topbar_title_notifications)) },
                )
            }

            AppDestination.REVIEWS -> ReviewsAndFeedbackScreen(
                onClickViewComments = onClickViewComments,
                onClickAddNewReviewCategory = onClickAddNewReviewCategory,
            ) {
                CenterAlignedTopAppBar(
                    navigationIcon = navigationIcon,
                    title = {
                        Text(
                            text = stringResource(id = R.string.topbar_title_reviews),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.basicMarquee(),
                        )
                    },
                )
            }
        }
    }
}
