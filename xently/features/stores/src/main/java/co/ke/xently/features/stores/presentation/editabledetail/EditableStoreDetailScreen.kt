package co.ke.xently.features.stores.presentation.editabledetail

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
import co.ke.xently.features.stores.presentation.editabledetail.components.NonNullStoreContent
import co.ke.xently.features.stores.presentation.editabledetail.components.NullStoreContent
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview

@Composable
fun EditableStoreDetailScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
    onClickEdit: () -> Unit,
    onClickMoreDetails: () -> Unit,
    onClickAddStore: () -> Unit,
) {
    val viewModel = hiltViewModel<EditableStoreDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)

    EditableStoreDetailScreen(
        state = state,
        event = event,
        modifier = modifier,
        onClickBack = onClickBack,
        onClickSelectShop = onClickSelectShop,
        onClickSelectBranch = onClickSelectBranch,
        onClickEdit = onClickEdit,
        onClickMoreDetails = onClickMoreDetails,
        onClickAddStore = onClickAddStore,
        onAction = viewModel::onAction
    )
}

@Composable
internal fun EditableStoreDetailScreen(
    state: EditableStoreDetailUiState,
    event: EditableStoreDetailEvent?,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
    onClickEdit: () -> Unit,
    onClickMoreDetails: () -> Unit,
    onClickAddStore: () -> Unit,
    onAction: (EditableStoreDetailAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is EditableStoreDetailEvent.Error -> {
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

            EditableStoreDetailEvent.Success -> onClickBack()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                    onClickEdit = onClickEdit,
                    onClickMoreDetails = onClickMoreDetails,
                    onClickUploadImage = { /*TODO*/ },
                    onClickUpdateImage = { /*TODO*/ },
                    onClickDeleteImage = { /*TODO*/ },
                )
            }
        }
    }
}

private class EditableStoreDetailUiStateParameterProvider :
    PreviewParameterProvider<EditableStoreDetailUiState> {
    private val store = Store(
        name = "Westlands",
        shop = Shop(name = "Ranalo K'Osewe"),
        description = "Short description about the business/hotel will go here. Lorem ipsum dolor trui loerm ipsum is a repetitive alternative place holder text for design projects.",
    )
    override val values: Sequence<EditableStoreDetailUiState>
        get() = sequenceOf(
            EditableStoreDetailUiState(store = store, canAddStore = true),
            EditableStoreDetailUiState(store = store, isImageUploading = true),
            EditableStoreDetailUiState(),
            EditableStoreDetailUiState(isLoading = true),
        )
}

@XentlyPreview
@Composable
private fun EditableStoreDetailScreenPreview(
    @PreviewParameter(EditableStoreDetailUiStateParameterProvider::class)
    state: EditableStoreDetailUiState,
) {
    XentlyTheme {
        EditableStoreDetailScreen(
            state = state,
            event = null,
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClickBack = {},
            onClickMoreDetails = {},
            onClickAddStore = {},
            onClickSelectBranch = {},
            onClickSelectShop = {},
            onClickEdit = {},
        )
    }
}
