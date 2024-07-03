package co.ke.xently.features.recommendations.presentation.response

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.recommendations.presentation.RecommendationAction
import co.ke.xently.features.recommendations.presentation.RecommendationEvent
import co.ke.xently.features.recommendations.presentation.RecommendationUiState
import co.ke.xently.features.recommendations.presentation.RecommendationViewModel
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState

@Composable
internal fun RecommendationResponseScreen(
    viewModel: RecommendationViewModel,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val storeCategories by viewModel.storeCategories.collectAsStateWithLifecycle()
    val productCategories by viewModel.productCategories.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is RecommendationEvent.Success -> Unit
                is RecommendationEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    RecommendationResponseScreen(
        state = state,
        modifier = modifier,
        storeCategories = storeCategories,
        productCategories = productCategories,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        onClickPinLocation = { },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun RecommendationResponseScreen(
    state: RecommendationUiState,
    storeCategories: List<StoreCategory>,
    productCategories: List<ProductCategory>,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    onClickBack: () -> Unit,
    onClickPinLocation: () -> Unit,
    onAction: (RecommendationAction) -> Unit,
) {
}