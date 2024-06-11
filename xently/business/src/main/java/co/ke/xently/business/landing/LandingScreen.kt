package co.ke.xently.business.landing

import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.Icon
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
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.presentation.list.ProductListScreen
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackScreen
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.active.ActiveStoreScreen

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
                item(
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = stringResource(destination.contentDescription),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(destination.label),
                            modifier = Modifier.basicMarquee(),
                        )
                    },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination },
                    alwaysShowLabel = adaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT,
                )
            }
        },
    ) {
        when (currentDestination) {
            AppDestination.DASHBOARD -> ActiveStoreScreen(
                onClickBack = onClickBack,
                onClickSelectShop = onClickSelectShop,
                onClickSelectBranch = onClickSelectBranch,
                onClickEdit = onClickEditStore,
                onClickMoreDetails = onClickEditStore,
                onClickAddStore = onClickAddStore,
            )

            AppDestination.PRODUCTS -> ProductListScreen(
                onClickBack = onClickBack,
                onClickAddProduct = onClickAddProduct,
                onClickEditProduct = onClickEditProduct,
            )

            AppDestination.CUSTOMERS -> Text(text = "Shopping")
            AppDestination.NOTIFICATIONS -> Text(text = "Profile")
            AppDestination.REVIEWS -> ReviewsAndFeedbackScreen(
                onClickBack = onClickBack,
                onClickViewComments = onClickViewComments,
                onClickAddNewReviewCategory = onClickAddNewReviewCategory,
            )
        }
    }
}
