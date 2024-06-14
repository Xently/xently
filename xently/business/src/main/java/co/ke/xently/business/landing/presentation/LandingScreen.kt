package co.ke.xently.business.landing.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import co.ke.xently.business.R
import co.ke.xently.business.landing.domain.AppDestination
import co.ke.xently.business.landing.domain.Menu
import co.ke.xently.business.landing.presentation.components.LandingModalDrawerSheet
import co.ke.xently.business.landing.presentation.components.LandingScreenContent
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import kotlinx.coroutines.launch

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
    onClickAddStore: (Shop?) -> Unit,
    onClickEditStore: (Store) -> Unit,
    onClickAddProduct: () -> Unit,
    onClickEditProduct: (Product) -> Unit,
    onClickAddNewReviewCategory: () -> Unit,
    onClickViewComments: (ReviewCategory) -> Unit,
    onClickLogout: () -> Unit,
    onClickLogin: () -> Unit,
    onClickAddShop: () -> Unit,
    onClickShop: (Shop) -> Unit,
    onClickQrCode: () -> Unit,
    onClickSettings: () -> Unit,
) {
    val viewModel = hiltViewModel<LandingViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val authenticationState = LocalAuthenticationState.current

    val closeDrawer: () -> Unit = {
        scope.launch {
            if (drawerState.isOpen && !drawerState.isAnimationRunning) {
                drawerState.close()
            }
        }
    }
    BackHandler(enabled = drawerState.isOpen) {
        closeDrawer()
    }
    var selectedMenu by rememberSaveable { mutableStateOf(Menu.DASHBOARD) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.DASHBOARD) }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            LandingModalDrawerSheet(
                canAddShop = state.canAddShop,
                selectedMenu = selectedMenu,
                authenticationState = authenticationState,
                onClickLogout = onClickLogout,
                onClickLogin = onClickLogin,
                shops = { state.shops },
                onClickAddShop = { closeDrawer(); onClickAddShop() },
                onClickSelectShop = { closeDrawer(); onClickSelectShop() },
                onClickShop = { closeDrawer(); onClickShop(it) },
                onClickAddStore = { closeDrawer(); onClickAddStore(it) },
                onClickMenu = {
                    when (it) {
                        Menu.QR_CODE -> onClickQrCode()
                        Menu.SETTINGS -> onClickSettings()
                        Menu.DASHBOARD -> currentDestination = AppDestination.DASHBOARD
                        Menu.PRODUCTS -> currentDestination = AppDestination.PRODUCTS
                        Menu.CUSTOMERS -> currentDestination = AppDestination.CUSTOMERS
                        Menu.REVIEWS -> currentDestination = AppDestination.REVIEWS
                        Menu.NOTIFICATIONS -> currentDestination = AppDestination.NOTIFICATIONS
                    }
                    selectedMenu = Menu.valueOf(currentDestination.name)
                    closeDrawer()
                },
            )
        },
    ) {
        val adaptiveInfo = currentWindowAdaptiveInfo()
        val customNavSuiteType = with(adaptiveInfo) {
            if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
                NavigationSuiteType.NavigationDrawer
            } else {
                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
            }
        }

        NavigationSuiteScaffold(
            modifier = Modifier.fillMaxSize(),
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
                        onClick = {
                            currentDestination = destination
                            selectedMenu = Menu.valueOf(destination.name)
                        },
                    )
                }
            },
        ) {
            val navigationIcon: @Composable () -> Unit = {
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Notes,
                        contentDescription = stringResource(R.string.content_desc_open_navigation_drawer),
                    )
                }
            }

            LandingScreenContent(
                canAddShop = state.canAddShop,
                currentDestination = currentDestination,
                onClickSelectShop = onClickSelectShop,
                onClickSelectBranch = onClickSelectBranch,
                onClickEditStore = onClickEditStore,
                onClickAddStore = { onClickAddStore(null) },
                navigationIcon = navigationIcon,
                onClickEditProduct = onClickEditProduct,
                onClickAddProduct = onClickAddProduct,
                onClickViewComments = onClickViewComments,
                onClickAddNewReviewCategory = onClickAddNewReviewCategory,
                onClickSettingsMenu = onClickSettings,
                onClickAddShopMenu = onClickAddShop,
            )
        }
    }
}
