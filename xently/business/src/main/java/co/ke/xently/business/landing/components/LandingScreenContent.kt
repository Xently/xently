package co.ke.xently.business.landing.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.waterfall
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import co.ke.xently.business.R
import co.ke.xently.business.landing.AppDestination
import co.ke.xently.features.customers.presentation.list.CustomerListScreen
import co.ke.xently.features.notifications.presentation.list.NotificationListScreen
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.presentation.list.ProductListScreen
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackScreen
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.active.ActiveStoreScreen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LandingScreenContent(
    currentDestination: AppDestination,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
    onClickEditStore: (Store) -> Unit,
    onClickAddStore: () -> Unit,
    navigationIcon: @Composable () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    onClickAddProduct: () -> Unit,
    onClickViewComments: (ReviewCategory) -> Unit,
    onClickAddNewReviewCategory: () -> Unit,
) {
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