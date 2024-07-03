package co.ke.xently.features.recommendations.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import co.ke.xently.features.recommendations.domain.RecommendationNavGraph
import co.ke.xently.features.recommendations.presentation.request.RecommendationRequestScreen
import co.ke.xently.features.recommendations.presentation.response.RecommendationResponseScreen

fun NavGraphBuilder.recommendationNavigation(
    navController: NavHostController,
    viewModel: RecommendationViewModel,
) {
    navigation<RecommendationNavGraph>(startDestination = RecommendationNavGraph.RecommendationRequest) {
        composable<RecommendationNavGraph.RecommendationRequest> {
            RecommendationRequestScreen(
                viewModel = viewModel,
                onClickBack = navController::navigateUp,
                onRequestSuccess = { /*TODO*/ },
                onClickPinLocation = { /*TODO*/ },
            )
        }
        composable<RecommendationNavGraph.RecommendationResponse> {
            RecommendationResponseScreen(
                viewModel = viewModel,
                onClickBack = navController::navigateUp,
            )
        }
    }
}