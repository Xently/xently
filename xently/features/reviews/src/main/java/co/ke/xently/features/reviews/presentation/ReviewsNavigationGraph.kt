package co.ke.xently.features.reviews.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import co.ke.xently.features.reviews.domain.ReviewsNavGraph
import co.ke.xently.features.reviews.presentation.reviews.ReviewsScreen

fun NavGraphBuilder.reviewsNavigation(navController: NavHostController) {
    navigation<ReviewsNavGraph>(startDestination = ReviewsNavGraph.Reviews) {
        composable<ReviewsNavGraph.Reviews> {
            ReviewsScreen(
                onClickBack = navController::navigateUp,
                onClickAddNewReviewCategory = { /*TODO*/ },
            )
        }
    }
}