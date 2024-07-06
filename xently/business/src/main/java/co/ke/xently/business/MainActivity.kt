package co.ke.xently.business

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ke.xently.business.domain.EditProductScreen
import co.ke.xently.business.domain.EditStoreReviewCategoryScreen
import co.ke.xently.business.domain.EditStoreScreen
import co.ke.xently.business.domain.LandingScreen
import co.ke.xently.business.domain.PickLocation
import co.ke.xently.business.domain.ReviewCommentListScreen
import co.ke.xently.business.domain.SelectShopScreen
import co.ke.xently.business.domain.SelectStoreScreen
import co.ke.xently.business.domain.SettingsScreen
import co.ke.xently.business.landing.presentation.LandingScreen
import co.ke.xently.features.auth.domain.AuthenticationNavGraph
import co.ke.xently.features.auth.presentation.authenticationNavigation
import co.ke.xently.features.products.presentation.edit.ProductEditDetailScreen
import co.ke.xently.features.reviewcategory.presentation.edit.ReviewCategoryEditDetailScreen
import co.ke.xently.features.reviews.presentation.comments.ReviewCommentListScreen
import co.ke.xently.features.settings.presentation.SettingsScreen
import co.ke.xently.features.settings.presentation.SettingsViewModel
import co.ke.xently.features.shops.domain.ShopNavGraph
import co.ke.xently.features.shops.presentation.edit.ShopEditDetailScreen
import co.ke.xently.features.shops.presentation.list.ShopListScreen
import co.ke.xently.features.stores.presentation.edit.StoreEditDetailScreen
import co.ke.xently.features.stores.presentation.list.selection.StoreSelectionListScreen
import co.ke.xently.features.location.picker.presentation.PickLocationScreen
import co.ke.xently.features.ui.core.presentation.App
import co.ke.xently.features.ui.core.presentation.EventHandler
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600
                }
            )
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
        enableEdgeToEdge()
        setContent {
            val mainViewModel = hiltViewModel<MainViewModel>()
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val themeSetting by settingsViewModel.currentThemeSetting.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            val eventHandler = remember {
                object : EventHandler {
                    override fun requestAuthentication() {
                        navController.navigate(AuthenticationNavGraph)
                    }

                    override fun requestShopSelection() {
                        navController.navigate(SelectShopScreen)
                    }

                    override fun requestStoreSelection(shop: Any?) {
                        navController.navigate(SelectStoreScreen)
                    }
                }
            }
            App(setting = themeSetting, eventHandler = eventHandler) {
                val authenticationState =
                    mainViewModel.authenticationState.collectAsStateWithLifecycle()

                CompositionLocalProvider(LocalAuthenticationState provides authenticationState) {
                    NavHost(navController = navController, startDestination = LandingScreen) {
                        composable<LandingScreen> {
                            LandingScreen(
                                viewModel = mainViewModel,
                                onClickAddStore = {
                                    navController.navigate(
                                        EditStoreScreen(
                                            addStoreUrl = it?.links?.get("add-store")
                                                ?.hrefWithoutQueryParamTemplates(),
                                        )
                                    )
                                },
                                onClickEditStore = {
                                    navController.navigate(EditStoreScreen(storeId = it.id))
                                },
                                onClickAddProduct = {
                                    navController.navigate(EditProductScreen())
                                },
                                onClickEditProduct = {
                                    navController.navigate(EditProductScreen(productId = it.id))
                                },
                                onClickAddNewReviewCategory = {
                                    navController.navigate(EditStoreReviewCategoryScreen)
                                },
                                onClickViewComments = {
                                    navController.navigate(ReviewCommentListScreen(it.name))
                                },
                                onClickAddShop = {
                                    navController.navigate(ShopNavGraph.EditShop)
                                },
                                onClickQrCode = {
                                    /*TODO*/
                                },
                                onClickSettings = {
                                    navController.navigate(SettingsScreen)
                                },
                            )
                        }
                        authenticationNavigation(navController = navController)
                        composable<EditProductScreen> {
                            ProductEditDetailScreen(onClickBack = navController::navigateUp)
                        }
                        composable<EditStoreScreen> {
                            StoreEditDetailScreen(onClickBack = navController::navigateUp)
                        }
                        composable<PickLocation> {
                            PickLocationScreen(onClickBack = navController::navigateUp)
                        }
                        composable<SelectShopScreen> {
                            ShopListScreen(
                                onClickBack = navController::navigateUp,
                                onClickAddShop = {
                                    navController.navigate(ShopNavGraph.EditShop)
                                },
                                onClickEditShop = {
                                    navController.navigate(ShopNavGraph.EditShop)
                                },
                            )
                        }
                        composable<SelectStoreScreen> {
                            StoreSelectionListScreen(
                                onClickBack = navController::navigateUp,
                                onClickAddStore = {
                                    navController.navigate(EditStoreScreen())
                                },
                                onClickEditStore = {
                                    navController.navigate(EditStoreScreen(storeId = it.id))
                                },
                                onStoreSelected = {
                                    navController.popBackStack(LandingScreen, inclusive = false)
                                },
                            )
                        }
                        composable<ShopNavGraph.EditShop> {
                            ShopEditDetailScreen(onClickBack = navController::navigateUp)
                        }
                        composable<EditStoreReviewCategoryScreen> {
                            ReviewCategoryEditDetailScreen(onClickBack = navController::navigateUp)
                        }
                        composable<ReviewCommentListScreen> {
                            ReviewCommentListScreen(onClickBack = navController::navigateUp)
                        }
                        composable<SettingsScreen> {
                            SettingsScreen(onClickBack = navController::navigateUp)
                        }
                    }
                }
            }
        }
    }
}