package co.ke.xently.business

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ke.xently.business.landing.LandingScreen
import co.ke.xently.business.landing.domain.EditStoreReviewCategoryScreen
import co.ke.xently.business.landing.domain.LandingScreen
import co.ke.xently.business.landing.domain.ReviewCommentListScreen
import co.ke.xently.features.auth.presentation.authenticationNavigation
import co.ke.xently.features.products.domain.EditProductNavGraph
import co.ke.xently.features.products.presentation.editProductNavigation
import co.ke.xently.features.reviewcategory.presentation.edit.ReviewCategoryEditDetailScreen
import co.ke.xently.features.reviews.presentation.comments.ReviewCommentListScreen
import co.ke.xently.features.stores.domain.EditStoreNavGraph
import co.ke.xently.features.stores.presentation.editStoreNavigation
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
                NavHost(navController = navController, startDestination = LandingScreen) {
                    composable<LandingScreen> {
                        LandingScreen(
                            onClickBack = navController::navigateUp,
                            onClickSelectShop = { /*TODO*/ },
                            onClickSelectBranch = { /*TODO*/ },
                            onClickAddStore = { navController.navigate(EditStoreNavGraph) },
                            onClickEditStore = { navController.navigate(EditStoreNavGraph) },
                            onClickAddProduct = { navController.navigate(EditProductNavGraph) },
                            onClickEditProduct = { navController.navigate(EditProductNavGraph) },
                            onClickViewComments = {
                                navController.navigate(ReviewCommentListScreen)
                            },
                            onClickAddNewReviewCategory = {
                                navController.navigate(EditStoreReviewCategoryScreen)
                            },
                        )
                    }
                    authenticationNavigation(navController = navController)
                    editStoreNavigation(navController = navController)
                    editProductNavigation(navController = navController)
                    composable<EditStoreReviewCategoryScreen> {
                        ReviewCategoryEditDetailScreen(
                            onClickBack = navController::navigateUp,
                        )
                    }
                    composable<ReviewCommentListScreen> {
                        ReviewCommentListScreen(
                            onClickBack = navController::navigateUp,
                        )
                    }
                }
            }
        }
    }
}