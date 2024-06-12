package co.ke.xently.features.stores.presentation.active

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.presentation.active.components.NonNullStoreContent
import co.ke.xently.features.stores.presentation.active.components.NullStoreContent
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview

@Composable
fun ActiveStoreScreen(
    modifier: Modifier = Modifier,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
    onClickEdit: (Store) -> Unit,
    onClickMoreDetails: (Store) -> Unit,
    onClickAddStore: () -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val viewModel = hiltViewModel<ActiveStoreViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)

    ActiveStoreScreen(
        state = state,
        event = event,
        modifier = modifier,
        onClickSelectShop = onClickSelectShop,
        onClickSelectBranch = onClickSelectBranch,
        onClickEdit = onClickEdit,
        onClickMoreDetails = onClickMoreDetails,
        onClickAddStore = onClickAddStore,
        topBar = topBar,
    )
}

@Composable
internal fun ActiveStoreScreen(
    state: ActiveStoreUiState,
    event: ActiveStoreEvent?,
    modifier: Modifier = Modifier,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
    onClickEdit: (Store) -> Unit,
    onClickMoreDetails: (Store) -> Unit,
    onClickAddStore: () -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null, ActiveStoreEvent.Success -> Unit
            is ActiveStoreEvent.Error -> {
                val result = snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                    actionLabel = if (event.type is DataError.Network) {
                        context.getString(R.string.action_retry)
                    } else {
                        null
                    },
                )

                when (result) {
                    SnackbarResult.Dismissed -> {

                    }

                    SnackbarResult.ActionPerformed -> {

                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = topBar,
        floatingActionButton = {
            if (state.canAddStore) {
                ExtendedFloatingActionButton(
                    onClick = onClickAddStore,
                    text = { Text(text = stringResource(R.string.action_add_store)) },
                    icon = {
                        Icon(
                            Icons.Default.AddBusiness,
                            contentDescription = stringResource(R.string.action_add_store),
                        )
                    },
                )
            }
        },
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            }

            state.store == null -> {
                NullStoreContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    isShopSelected = state.isShopSelected,
                    onClickSelectShop = onClickSelectShop,
                    onClickSelectBranch = onClickSelectBranch,
                )
            }

            else -> {
                NonNullStoreContent(
                    store = state.store,
                    isImageUploading = state.isImageUploading,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onClickEdit = { onClickEdit(state.store) },
                    onClickMoreDetails = { onClickMoreDetails(state.store) },
                    onClickUploadImage = { /*TODO*/ },
                    onClickUpdateImage = { /*TODO*/ },
                    onClickDeleteImage = { /*TODO*/ },
                )
            }
        }
    }
}

private class ActiveStoreUiStateParameterProvider : PreviewParameterProvider<ActiveStoreUiState> {
    private val store = Store(
        name = "Westlands",
        shop = Shop(name = "Ranalo K'Osewe"),
        description = """Short description about the business/hotel will go here.
                |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                |
                |Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.""".trimMargin(),
    )
    override val values: Sequence<ActiveStoreUiState>
        get() = sequenceOf(
            ActiveStoreUiState(store = store, canAddStore = true),
            ActiveStoreUiState(store = store, isImageUploading = true),
            ActiveStoreUiState(),
            ActiveStoreUiState(isLoading = true),
        )
}

@XentlyPreview
@Composable
private fun ActiveStoreScreenPreview(
    @PreviewParameter(ActiveStoreUiStateParameterProvider::class)
    state: ActiveStoreUiState,
) {
    XentlyTheme {
        ActiveStoreScreen(
            state = state,
            event = null,
            modifier = Modifier.fillMaxSize(),
            onClickSelectShop = {},
            onClickSelectBranch = {},
            onClickEdit = {},
            onClickMoreDetails = {},
            onClickAddStore = {},
        )
    }
}
