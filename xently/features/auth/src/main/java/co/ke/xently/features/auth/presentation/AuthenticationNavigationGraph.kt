package co.ke.xently.features.auth.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import co.ke.xently.features.auth.domain.AuthenticationNavGraph
import co.ke.xently.features.auth.presentation.resetpassword.RequestPasswordResetScreen
import co.ke.xently.features.auth.presentation.signin.SignInScreen
import co.ke.xently.features.auth.presentation.signup.SignUpScreen

fun NavGraphBuilder.authenticationNavigation(navController: NavHostController) {
    navigation<AuthenticationNavGraph>(startDestination = AuthenticationNavGraph.SignIn) {
        composable<AuthenticationNavGraph.SignIn> {
            SignInScreen(
                onClickBack = navController::navigateUp,
                onClickCreateAccount = { navController.navigate(AuthenticationNavGraph.SignUp) },
                onClickForgotPassword = { navController.navigate(AuthenticationNavGraph.RequestPasswordReset) },
            )
        }
        composable<AuthenticationNavGraph.SignUp> {
            SignUpScreen(
                onClickBack = navController::navigateUp,
                onClickSignIn = navController::navigateUp,
            )
        }
        composable<AuthenticationNavGraph.RequestPasswordReset> {
            RequestPasswordResetScreen(
                onClickBack = navController::navigateUp,
                onClickSignIn = navController::navigateUp,
            )
        }
    }
}