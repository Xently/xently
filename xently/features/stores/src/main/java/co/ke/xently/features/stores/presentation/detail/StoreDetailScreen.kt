package co.ke.xently.features.stores.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.qrcode.presentation.ScanQrCodeAlertDialog
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.detail.components.StoreDetail
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.location.tracker.domain.error.LocationRequestError
import co.ke.xently.libraries.location.tracker.domain.error.PermissionError
import co.ke.xently.libraries.location.tracker.presentation.rememberEnableLocationGPSLauncher
import co.ke.xently.libraries.location.tracker.presentation.rememberLocationPermissionLauncher
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import kotlinx.coroutines.launch

typealias StoreDetailContentScope = BoxScope

@Composable
fun StoreDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: AbstractStoreDetailViewModel = hiltViewModel<StoreDetailViewModel>(),
    onClickBack: () -> Unit,
    onClickMoreDetails: (Store) -> Unit,
    onClickReviewStore: (String) -> Unit,
    content: @Composable (StoreDetailContentScope.() -> Unit) = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = rememberSnackbarHostState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val enableGPSLauncher = rememberEnableLocationGPSLauncher(snackbarHostState = snackbarHostState)
    val locationPermissionLauncher =
        rememberLocationPermissionLauncher(autoProcessStateOnRender = false) { granted ->
            if (granted) {
                viewModel.onAction(StoreDetailAction.GetPointsAndReview)
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.message_location_permission_denied),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is StoreDetailEvent.Success -> Unit
                is StoreDetailEvent.Error.Store -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }

                is StoreDetailEvent.Error.QrCode -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }

                is StoreDetailEvent.Error.LocationTracker -> {
                    val actionLabel = when (event.type) {
                        LocationRequestError.UNKNOWN,
                        LocationRequestError.NO_KNOWN_LOCATION,
                        -> null

                        PermissionError.GPS_DISABLED -> {
                            context.getString(R.string.action_enable_gps)
                        }

                        PermissionError.PERMISSION_DENIED -> {
                            context.getString(R.string.action_grant_location_permission)
                        }
                    }
                    val result = snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                        actionLabel = actionLabel,
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed -> when (event.type) {
                            LocationRequestError.UNKNOWN,
                            LocationRequestError.NO_KNOWN_LOCATION,
                            -> Unit

                            PermissionError.GPS_DISABLED -> enableGPSLauncher.launch()
                            PermissionError.PERMISSION_DENIED -> locationPermissionLauncher.launch()
                        }
                    }
                }
            }
        }
    }

    val isProcessingQrCode by produceState(false, viewModel) {
        viewModel.isProcessingQrCode.collect {
            this.value = it
        }
    }

    StoreDetailScreen(
        state = state,
        modifier = modifier,
        isProcessingQrCode = isProcessingQrCode,
        snackbarHostState = snackbarHostState,
        onClickBack = onClickBack,
        onClickMoreDetails = onClickMoreDetails,
        onAction = viewModel::onAction,
        onClickReviewStore = onClickReviewStore,
        onGetPointsAndReviewClick = { scope.launch { locationPermissionLauncher.launch() } },
        content = content,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreDetailScreen(
    state: StoreDetailUiState,
    modifier: Modifier = Modifier,
    isProcessingQrCode: Boolean = false,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    onClickBack: () -> Unit,
    onClickMoreDetails: (Store) -> Unit,
    onAction: (StoreDetailAction) -> Unit = {},
    onClickReviewStore: (String) -> Unit = {},
    onGetPointsAndReviewClick: () -> Unit = {},
    content: @Composable (StoreDetailContentScope.() -> Unit) = {},
) {
    if (isProcessingQrCode) {
        ScanQrCodeAlertDialog(
            response = state.qrCodeScanResponse,
            onDismissRequest = { onAction(StoreDetailAction.DismissQrCodeProcessingDialog) },
            onPositiveButtonClick = {
                onAction(StoreDetailAction.DismissQrCodeProcessingDialog)
                state.qrCodeScanResponse
                    ?.reviewCategoriesUrl
                    ?.let(onClickReviewStore)
            },
        )
    }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier,/*.nestedScroll(scrollBehavior.nestedScrollConnection)*/
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Box {
                StoreDetail(
                    state = state,
                    snackbarHostState = snackbarHostState,
                    onClickMoreDetails = onClickMoreDetails,
                    onGetPointsAndReviewClick = onGetPointsAndReviewClick,
                )
                TopAppBar(
                    title = {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = state.store?.name
                                ?: stringResource(R.string.topbar_title_store_details),
                        )
                    },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                )
            }
        },
    ) { innerPadding ->
        Box(
            content = content,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

internal class StoreDetailUiStateParameterProvider :
    PreviewParameterProvider<Pair<StoreDetailUiState, Boolean>> {
    override val values: Sequence<Pair<StoreDetailUiState, Boolean>>
        get() = sequenceOf(
            StoreDetailUiState(
                isLoading = true,
            ) to false,
            StoreDetailUiState(
                store = Store.DEFAULT,
            ) to false,
            StoreDetailUiState(
                isLoading = true,
            ) to true,
            StoreDetailUiState(
                store = Store.DEFAULT,
            ) to true,
        )
}

@XentlyPreview
@Composable
private fun StoreDetailScreenPreview(
    @PreviewParameter(StoreDetailUiStateParameterProvider::class)
    state: Pair<StoreDetailUiState, Boolean>,
) {
    XentlyTheme {
        StoreDetailScreen(
            state = state.first,
            isProcessingQrCode = state.second,
            onClickBack = {},
            onClickMoreDetails = {},
        )
    }
}
