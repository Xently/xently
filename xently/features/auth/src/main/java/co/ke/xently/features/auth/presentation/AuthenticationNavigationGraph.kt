package co.ke.xently.features.auth.presentation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import co.ke.xently.features.auth.domain.AuthenticationNavGraph
import co.ke.xently.features.auth.presentation.signin.SignInScreen
import co.ke.xently.features.auth.presentation.signin.SignInViewModel
import co.ke.xently.features.auth.presentation.signup.SignUpScreen
import co.ke.xently.features.auth.presentation.signup.SignUpViewModel

fun NavGraphBuilder.authenticationNavigation(navController: NavHostController) {
    navigation<AuthenticationNavGraph>(startDestination = AuthenticationNavGraph.SignIn) {
        composable<AuthenticationNavGraph.SignIn> {
            val viewModel = hiltViewModel<SignInViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val event by viewModel.event.collectAsStateWithLifecycle(null)
            SignInScreen(
                state = state,
                event = event,
                onClickBack = navController::navigateUp,
                onAction = viewModel::onAction,
                onClickCreateAccount = { navController.navigate(AuthenticationNavGraph.SignUp) },
                onClickForgotPassword = {},
            )
        }
        composable<AuthenticationNavGraph.SignUp> {
            val viewModel = hiltViewModel<SignUpViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val event by viewModel.event.collectAsStateWithLifecycle(null)
            SignUpScreen(
                state = state,
                event = event,
                onClickBack = navController::navigateUp,
                onAction = viewModel::onAction,
                onClickSignIn = navController::navigateUp,
            )
        }
    }
}