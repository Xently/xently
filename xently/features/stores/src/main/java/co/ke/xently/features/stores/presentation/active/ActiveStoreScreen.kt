package co.ke.xently.features.stores.presentation.active

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import co.ke.xently.features.stores.presentation.active.components.NonNullStoreContent
import co.ke.xently.features.stores.presentation.active.components.NullStoreContent
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.image.domain.File
import co.ke.xently.libraries.data.image.domain.Progress
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.image.presentation.imageState

@Composable
fun ActiveStoreScreen(
    modifier: Modifier = Modifier,
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
        onClickEdit = onClickEdit,
        onClickMoreDetails = onClickMoreDetails,
        onClickAddStore = onClickAddStore,
        onAction = viewModel::onAction,
        topBar = topBar,
    )
}

@Composable
internal fun ActiveStoreScreen(
    state: ActiveStoreUiState,
    event: ActiveStoreEvent?,
    modifier: Modifier = Modifier,
    onClickEdit: (Store) -> Unit,
    onClickMoreDetails: (Store) -> Unit,
    onClickAddStore: () -> Unit,
    onAction: (ActiveStoreAction) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null, ActiveStoreEvent.SelectShop, ActiveStoreEvent.SelectStore -> Unit
            is ActiveStoreEvent.Error -> {
                snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                )
            }

            is ActiveStoreEvent.Success -> {
                when (event.action) {
                    is ActiveStoreAction.ProcessImageData -> Unit
                    is ActiveStoreAction.ProcessImageUpdateData -> {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.message_image_updated),
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is ActiveStoreAction.RemoveImageAtPosition -> {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.message_image_removed),
                            duration = SnackbarDuration.Short,
                        )
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
                val store = remember { Store.DEFAULT }
                NonNullStoreContent(
                    store = store,
                    isLoading = true,
                    isImageUploading = false,
                    images = emptyList(),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onClickEdit = { },
                    onClickMoreDetails = { },
                    onClickUploadImage = { },
                    withImageUpdateResult = { },
                    onClickDeleteImage = { },
                )
            }

            state.store == null -> {
                NullStoreContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    isShopSelected = state.isShopSelected,
                )
            }

            else -> {
                var imageUri by remember { mutableStateOf<Uri?>(null) }
                val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) {
                    imageUri = it
                }
                val image by imageUri.imageState()

                LaunchedEffect(image) {
                    image?.also {
                        onAction(ActiveStoreAction.ProcessImageData(it))
                        when (it) {
                            is UploadResponse, is Progress -> Unit
                            is File.Error, is UploadRequest -> imageUri = null
                        }
                    }
                }
                NonNullStoreContent(
                    store = state.store,
                    images = state.images,
                    isImageUploading = state.isImageUploading,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onClickEdit = { onClickEdit(state.store) },
                    onClickMoreDetails = { onClickMoreDetails(state.store) },
                    onClickDeleteImage = { onAction(ActiveStoreAction.RemoveImageAtPosition(it)) },
                    withImageUpdateResult = { onAction(ActiveStoreAction.ProcessImageUpdateData(it)) },
                    onClickUploadImage = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
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
            onClickEdit = {},
            onClickMoreDetails = {},
            onClickAddStore = {},
            onAction = {},
            topBar = {},
        )
    }
}
