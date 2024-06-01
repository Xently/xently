package co.ke.xently.features.auth.presentation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import co.ke.xently.features.auth.domain.AuthenticationNavGraph
import co.ke.xently.features.auth.presentation.login.SignInScreen
import co.ke.xently.features.auth.presentation.login.SignInUiState
import co.ke.xently.features.auth.presentation.login.SignInViewModel

fun NavGraphBuilder.authenticationNavigation(onClickBack: () -> Unit) {
    navigation<AuthenticationNavGraph>(startDestination = AuthenticationNavGraph.SignIn) {
        composable<AuthenticationNavGraph.SignIn> {
            val viewModel = hiltViewModel<SignInViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val event by viewModel.event.collectAsStateWithLifecycle(null)
            SignInScreen(
                state = state,
                event = event,
                onClickBack = onClickBack,
                onAction = viewModel::onAction,
                onClickCreateAccount = {},
                onClickForgotPassword = {},
            )
        }
        composable<AuthenticationNavGraph.SignUp> {
            SignInScreen(
                state = SignInUiState(),
                event = null,
                onClickBack = onClickBack,
                onAction = { /*TODO*/ },
                onClickCreateAccount = {},
                onClickForgotPassword = {},
            )
        }
    }
}