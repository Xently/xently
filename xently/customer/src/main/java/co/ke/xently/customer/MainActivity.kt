package co.ke.xently.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ke.xently.customer.landing.LandingScreen
import co.ke.xently.customer.landing.domain.LandingScreen
import co.ke.xently.features.auth.domain.AuthenticationNavGraph
import co.ke.xently.features.auth.presentation.authenticationNavigation
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
            val navController = rememberNavController()
            val eventHandler = remember {
                object : EventHandler {
                    override fun requestAuthentication() {}

                    override fun requestShopSelection() {}

                    override fun requestStoreSelection(shop: Any?) {}
                }
            }
            App(eventHandler = eventHandler) {
                NavHost(navController = navController, startDestination = AuthenticationNavGraph) {
                    composable<LandingScreen> {
                        LandingScreen()
                    }
                    authenticationNavigation(navController = navController)
                }
            }
        }
    }
}