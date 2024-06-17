package co.ke.xently.business

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ke.xently.business.domain.InitialStoreSelectionRoute
import co.ke.xently.business.domain.InitialStoreSelectionRoute.SelectShop
import co.ke.xently.business.domain.InitialStoreSelectionRoute.SelectStore
import co.ke.xently.business.domain.ReviewCommentListScreen
import co.ke.xently.business.domain.SettingsScreen
import co.ke.xently.business.landing.domain.EditStoreReviewCategoryScreen
import co.ke.xently.business.landing.domain.LandingScreen
import co.ke.xently.business.landing.presentation.LandingScreen
import co.ke.xently.features.auth.domain.AuthenticationNavGraph
import co.ke.xently.features.auth.presentation.authenticationNavigation
import co.ke.xently.features.products.domain.EditProductNavGraph
import co.ke.xently.features.products.presentation.editProductNavigation
import co.ke.xently.features.reviewcategory.presentation.edit.ReviewCategoryEditDetailScreen
import co.ke.xently.features.reviews.presentation.comments.ReviewCommentListScreen
import co.ke.xently.features.settings.presentation.SettingsScreen
import co.ke.xently.features.settings.presentation.SettingsViewModel
import co.ke.xently.features.shops.domain.ShopNavGraph
import co.ke.xently.features.shops.presentation.edit.ShopEditDetailScreen
import co.ke.xently.features.shops.presentation.list.ShopListScreen
import co.ke.xently.features.stores.domain.EditStoreNavGraph
import co.ke.xently.features.stores.presentation.editStoreNavigation
import co.ke.xently.features.stores.presentation.list.StoreListScreen
import co.ke.xently.features.ui.core.presentation.App
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val themeSetting by settingsViewModel.currentThemeSetting.collectAsStateWithLifecycle()

            App(setting = themeSetting) {
                val navController = rememberNavController()
                var initialStoreSelectionRoute: InitialStoreSelectionRoute? by remember {
                    mutableStateOf(null)
                }
                NavHost(navController = navController, startDestination = LandingScreen) {
                    composable<LandingScreen> {
                        LandingScreen(
                            onClickSelectShop = {
                                navController.navigate(SelectShop)
                            },
                            onClickSelectBranch = {
                                navController.navigate(SelectStore)
                            },
                            onClickAddStore = {
                                navController.navigate(EditStoreNavGraph)
                            },
                            onClickEditStore = {
                                navController.navigate(EditStoreNavGraph)
                            },
                            onClickAddProduct = {
                                navController.navigate(EditProductNavGraph)
                            },
                            onClickEditProduct = {
                                navController.navigate(EditProductNavGraph)
                            },
                            onClickAddNewReviewCategory = {
                                navController.navigate(EditStoreReviewCategoryScreen)
                            },
                            onClickViewComments = {
                                navController.navigate(ReviewCommentListScreen(it.name))
                            },
                            onClickLogin = {
                                navController.navigate(AuthenticationNavGraph)
                            },
                            onClickAddShop = {
                                navController.navigate(ShopNavGraph.EditShop)
                            },
                            onClickShop = {
                                navController.navigate(SelectStore)
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
                    editStoreNavigation(navController = navController)
                    editProductNavigation(navController = navController)
                    composable<SelectShop> {
                        LaunchedEffect(Unit) {
                            if (initialStoreSelectionRoute == null) {
                                initialStoreSelectionRoute = SelectShop
                            }
                        }
                        ShopListScreen(
                            onClickBack = navController::navigateUp,
                            onClickAddShop = {
                                navController.navigate(ShopNavGraph.EditShop)
                            },
                            onClickEditShop = {
                                navController.navigate(ShopNavGraph.EditShop)
                            },
                            onShopSelected = {
                                navController.navigate(SelectStore)
                            },
                        )
                    }
                    composable<SelectStore> {
                        LaunchedEffect(Unit) {
                            if (initialStoreSelectionRoute == null) {
                                initialStoreSelectionRoute = SelectStore
                            }
                        }
                        StoreListScreen(
                            onClickBack = navController::navigateUp,
                            onClickAddStore = {
                                navController.navigate(EditStoreNavGraph)
                            },
                            onClickEditStore = {
                                navController.navigate(EditStoreNavGraph)
                            },
                            onStoreSelected = {
                                initialStoreSelectionRoute?.let {
                                    navController.popBackStack(
                                        it,
                                        inclusive = true
                                    )
                                }
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