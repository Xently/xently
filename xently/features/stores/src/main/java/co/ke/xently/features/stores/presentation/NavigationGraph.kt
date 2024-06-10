package co.ke.xently.features.stores.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import co.ke.xently.features.stores.domain.EditStoreNavGraph
import co.ke.xently.features.stores.presentation.edit.StoreEditDetailScreen
import co.ke.xently.features.stores.presentation.locationpickup.PickStoreLocationScreen


fun NavGraphBuilder.editStoreNavigation(navController: NavHostController) {
    navigation<EditStoreNavGraph>(startDestination = EditStoreNavGraph.EditStore) {
        composable<EditStoreNavGraph.EditStore> {
            StoreEditDetailScreen(
                onClickBack = navController::navigateUp,
                onClickPickLocation = { navController.navigate(EditStoreNavGraph.PickLocation) },
            )
        }
        composable<EditStoreNavGraph.PickLocation> {
            PickStoreLocationScreen(
                onClickBack = navController::navigateUp,
            )
        }
    }
}