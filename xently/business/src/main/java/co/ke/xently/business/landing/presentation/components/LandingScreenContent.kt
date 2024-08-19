package co.ke.xently.business.landing.presentation.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.waterfall
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.sp
import co.ke.xently.business.R
import co.ke.xently.business.landing.domain.AppDestination
import co.ke.xently.features.customers.presentation.list.CustomerListScreen
import co.ke.xently.features.notifications.presentation.list.NotificationListScreen
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.presentation.list.ActiveStoreProductListScreen
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackScreen
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.active.ActiveStoreScreen
import co.ke.xently.features.ui.core.presentation.components.DropdownMenuWithLegalRequirements

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LandingScreenContent(
    canAddShop: Boolean,
    currentDestination: AppDestination,
    onClickEditStore: (Store) -> Unit,
    onClickAddStore: () -> Unit,
    navigationIcon: @Composable () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    onClickCloneProducts: () -> Unit,
    onClickAddProduct: () -> Unit,
    onClickViewComments: (ReviewCategory) -> Unit,
    onClickAddNewReviewCategory: () -> Unit,
    onClickSettingsMenu: () -> Unit,
    onClickAddShopMenu: () -> Unit,
) {
    when (currentDestination) {
        AppDestination.DASHBOARD -> ActiveStoreScreen(
            onClickEdit = onClickEditStore,
            onClickMoreDetails = onClickEditStore,
            onClickAddStore = onClickAddStore,
        ) {
            CenterAlignedTopAppBar(
                navigationIcon = navigationIcon,
                title = {
                    Text(
                        maxLines = 1,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        text = stringResource(R.string.app_name).toUpperCase(Locale.current),
                    )
                },
                actions = {
                    Box {
                        var expanded by rememberSaveable { mutableStateOf(false) }
                        IconButton(
                            onClick = { expanded = true },
                            content = {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.content_desc_topbar_menu),
                                )
                            },
                        )

                        DropdownMenuWithLegalRequirements(
                            expanded = expanded,
                            onExpandChanged = { expanded = it },
                            preLegalRequirements = {
                                if (canAddShop) {
                                    DropdownMenuItem(
                                        onClick = {
                                            onClickAddShopMenu()
                                            expanded = false
                                        },
                                        text = { Text(stringResource(R.string.action_add_shop)) },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.AddBusiness,
                                                contentDescription = stringResource(R.string.action_add_shop),
                                            )
                                        },
                                    )
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        onClickSettingsMenu()
                                        expanded = false
                                    },
                                    text = { Text(text = stringResource(R.string.app_destination_settings)) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = stringResource(R.string.app_destination_settings),
                                        )
                                    },
                                )
                            },
                        )
                    }
                },
            )
        }

        AppDestination.PRODUCTS -> ActiveStoreProductListScreen(
            onClickAddProduct = onClickAddProduct,
            onClickEditProduct = onClickEditProduct,
            onClickCloneProducts = onClickCloneProducts,
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