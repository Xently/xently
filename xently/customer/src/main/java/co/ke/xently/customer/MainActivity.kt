package co.ke.xently.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ke.xently.customer.domain.LandingScreen
import co.ke.xently.customer.domain.MoreDetailsScreen
import co.ke.xently.customer.domain.PickLocationScreen
import co.ke.xently.customer.domain.ProfileEditDetailScreen
import co.ke.xently.customer.domain.ReviewRequestScreen
import co.ke.xently.customer.domain.SettingsScreen
import co.ke.xently.customer.domain.StoreDetailScreen
import co.ke.xently.customer.landing.presentation.LandingScreen
import co.ke.xently.features.auth.domain.AuthenticationNavGraph
import co.ke.xently.features.auth.presentation.authenticationNavigation
import co.ke.xently.features.location.picker.presentation.PickLocationScreen
import co.ke.xently.features.products.presentation.list.CategoryFilterableProductListContent
import co.ke.xently.features.products.presentation.list.ProductListViewModel
import co.ke.xently.features.profile.presentation.edit.ProfileEditDetailScreen
import co.ke.xently.features.recommendations.domain.RecommendationNavGraph
import co.ke.xently.features.recommendations.presentation.recommendationNavigation
import co.ke.xently.features.reviews.SyncReviewsWorker
import co.ke.xently.features.reviews.presentation.reviewrequest.ReviewRequestScreen
import co.ke.xently.features.settings.presentation.SettingsScreen
import co.ke.xently.features.settings.presentation.SettingsViewModel
import co.ke.xently.features.stores.presentation.detail.StoreDetailScreen
import co.ke.xently.features.stores.presentation.moredetails.MoreDetailsScreen
import co.ke.xently.features.ui.core.presentation.App
import co.ke.xently.features.ui.core.presentation.EventHandler
import co.ke.xently.libraries.location.tracker.presentation.LocalLocationState
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.LocalHttpClient
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.HttpClient
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var httpClient: HttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        SyncReviewsWorker.start(applicationContext)

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
            val currentLocationState =
                mainViewModel.currentLocation.collectAsStateWithLifecycle(null)
            val navController = rememberNavController()
            val eventHandler = remember {
                object : EventHandler {
                    override fun requestAuthentication() {
                        navController.navigate(AuthenticationNavGraph)
                    }

                    override fun requestShopSelection() {
                    }

                    override fun requestStoreSelection(shop: Any?) {
                    }
                }
            }
            App(setting = themeSetting, eventHandler = eventHandler) {
                val authenticationState =
                    mainViewModel.authenticationState.collectAsStateWithLifecycle()
                CompositionLocalProvider(
                    LocalHttpClient provides httpClient,
                    LocalLocationState provides currentLocationState,
                    LocalAuthenticationState provides authenticationState,
                ) {
                    NavHost(navController = navController, startDestination = LandingScreen) {
                        composable<LandingScreen> {
                            LandingScreen(
                                viewModel = mainViewModel,
                                onClickSettings = {
                                    navController.navigate(SettingsScreen)
                                },
                                onClickFilterStores = {
                                    navController.navigate(RecommendationNavGraph)
                                },
                                onClickEditProfile = {
                                    navController.navigate(ProfileEditDetailScreen)
                                },
                                onClickStore = {
                                    navController.navigate(
                                        StoreDetailScreen(
                                            storeId = it.id,
                                            productsUrl = it.links["products"]!!.hrefWithoutQueryParamTemplates(),
                                        )
                                    )
                                },
                            )
                        }
                        authenticationNavigation(navController = navController)
                        recommendationNavigation(
                            navController = navController,
                            onClickMoreDetails = {
                                navController.navigate(MoreDetailsScreen(storeId = it.id))
                            },
                            onClickReviewStore = {
                                navController.navigate(ReviewRequestScreen(reviewCategoriesUrl = it))
                            },
                        )
                        composable<StoreDetailScreen> {
                            val viewModel = hiltViewModel<ProductListViewModel>()
                            StoreDetailScreen(
                                onClickBack = navController::navigateUp,
                                onClickMoreDetails = {
                                    navController.navigate(MoreDetailsScreen(storeId = it.id))
                                },
                                onClickReviewStore = {
                                    navController.navigate(ReviewRequestScreen(reviewCategoriesUrl = it))
                                },
                            ) {
                                CategoryFilterableProductListContent(
                                    viewModel = viewModel,
                                    modifier = Modifier.matchParentSize(),
                                )
                            }
                        }
                        composable<ProfileEditDetailScreen> {
                            ProfileEditDetailScreen(onClickBack = navController::navigateUp)
                        }
                        composable<ReviewRequestScreen> {
                            ReviewRequestScreen(onClickBack = navController::navigateUp)
                        }
                        composable<MoreDetailsScreen> {
                            MoreDetailsScreen(onClickBack = navController::navigateUp)
                        }
                        composable<PickLocationScreen> {
                            PickLocationScreen(onClickBack = navController::navigateUp)
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