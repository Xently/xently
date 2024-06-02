package co.ke.xently.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ke.xently.customer.landing.LandingScreen
import co.ke.xently.customer.landing.domain.LandingScreen
import co.ke.xently.features.auth.domain.AuthenticationNavGraph
import co.ke.xently.features.auth.presentation.authenticationNavigation
import co.ke.xently.features.ui.core.presentation.App
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App {
                val navController = rememberNavController()
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