package co.ke.xently.business

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ke.xently.business.landing.LandingScreen
import co.ke.xently.business.landing.domain.LandingScreen
import co.ke.xently.features.auth.domain.AuthenticationScreen
import co.ke.xently.features.auth.presentation.AuthenticationScreen
import co.ke.xently.features.ui.core.presentation.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = AuthenticationScreen) {
                    composable<LandingScreen> {
                        LandingScreen()
                    }
                    composable<AuthenticationScreen> {
                        AuthenticationScreen()
                    }
                }
            }
        }
    }
}