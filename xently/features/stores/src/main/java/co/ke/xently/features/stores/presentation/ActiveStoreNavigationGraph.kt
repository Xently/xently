package co.ke.xently.features.stores.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import co.ke.xently.features.stores.domain.ActiveStoreNavGraph
import co.ke.xently.features.stores.presentation.active.ActiveStoreScreen
import co.ke.xently.features.stores.presentation.edit.StoreEditDetailScreen
import co.ke.xently.features.stores.presentation.locationpickup.PickStoreLocationScreen

fun NavGraphBuilder.activeStoreNavigation(
    navController: NavHostController,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
) {
    navigation<ActiveStoreNavGraph>(startDestination = ActiveStoreNavGraph.ActiveStore) {
        composable<ActiveStoreNavGraph.ActiveStore> {
            ActiveStoreScreen(
                onClickBack = navController::navigateUp,
                onClickSelectShop = onClickSelectShop,
                onClickSelectBranch = onClickSelectBranch,
                onClickEdit = { navController.navigate(ActiveStoreNavGraph.EditStore) },
                onClickMoreDetails = { navController.navigate(ActiveStoreNavGraph.EditStore) },
                onClickAddStore = { navController.navigate(ActiveStoreNavGraph.EditStore) },
            )
        }
        composable<ActiveStoreNavGraph.EditStore> {
            StoreEditDetailScreen(
                onClickBack = navController::navigateUp,
                onClickPickLocation = { navController.navigate(ActiveStoreNavGraph.PickLocation) },
            )
        }
        composable<ActiveStoreNavGraph.PickLocation> {
            PickStoreLocationScreen(
                onClickBack = navController::navigateUp,
            )
        }
    }
}