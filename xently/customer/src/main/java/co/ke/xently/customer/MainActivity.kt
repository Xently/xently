package co.ke.xently.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ke.xently.customer.domain.LandingScreen
import co.ke.xently.customer.domain.PickLocation
import co.ke.xently.customer.domain.SettingsScreen
import co.ke.xently.customer.landing.presentation.LandingScreen
import co.ke.xently.features.auth.domain.AuthenticationNavGraph
import co.ke.xently.features.auth.presentation.authenticationNavigation
import co.ke.xently.features.settings.presentation.SettingsScreen
import co.ke.xently.features.settings.presentation.SettingsViewModel
import co.ke.xently.features.stores.presentation.locationpickup.PickStoreLocationScreen
import co.ke.xently.features.ui.core.presentation.App
import co.ke.xently.features.ui.core.presentation.EventHandler
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
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val themeSetting by settingsViewModel.currentThemeSetting.collectAsStateWithLifecycle()
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
                NavHost(navController = navController, startDestination = LandingScreen) {
                    composable<LandingScreen> {
                        LandingScreen(
                            onClickSettings = {
                                navController.navigate(SettingsScreen)
                            },
                        )
                    }
                    authenticationNavigation(navController = navController)
                    composable<PickLocation> {
                        PickStoreLocationScreen(onClickBack = navController::navigateUp)
                    }
                    composable<SettingsScreen> {
                        SettingsScreen(onClickBack = navController::navigateUp)
                    }
                }
            }
        }
    }
}