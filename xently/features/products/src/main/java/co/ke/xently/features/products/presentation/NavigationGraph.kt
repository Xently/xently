package co.ke.xently.features.products.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import co.ke.xently.features.products.domain.EditProductNavGraph
import co.ke.xently.features.products.presentation.edit.ProductEditDetailScreen


fun NavGraphBuilder.editProductNavigation(navController: NavHostController) {
    navigation<EditProductNavGraph>(startDestination = EditProductNavGraph.EditProduct) {
        composable<EditProductNavGraph.EditProduct> {
            ProductEditDetailScreen(onClickBack = navController::navigateUp)
        }
    }
}