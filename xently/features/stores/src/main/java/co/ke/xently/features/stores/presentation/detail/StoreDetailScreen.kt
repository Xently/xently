package co.ke.xently.features.stores.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.ContentPadding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.qrcode.presentation.ScanQrCodeAlertDialog
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.detail.components.QrCodeCard
import co.ke.xently.features.stores.presentation.detail.components.StoreDetailListItem
import co.ke.xently.features.stores.presentation.detail.components.StoreImagesBox
import co.ke.xently.features.stores.presentation.detail.components.rememberUrlOpener
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.location.tracker.domain.error.LocationRequestError
import co.ke.xently.libraries.location.tracker.domain.error.PermissionError
import co.ke.xently.libraries.location.tracker.presentation.rememberEnableLocationGPSLauncher
import co.ke.xently.libraries.location.tracker.presentation.rememberLocationPermissionLauncher
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.components.shimmer
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
                Column {
                    StoreImagesBox(
                        modifier = Modifier.height(300.dp),
                        images = state.store?.images ?: emptyList(),
                    )

                    Card(shape = RectangleShape) {
                        StoreDetailListItem(
                            store = state.store ?: Store.DEFAULT,
                            isLoading = state.isLoading,
                            snackbarHostState = snackbarHostState,
                        )

                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            TextButton(
                                shape = RectangleShape,
                                contentPadding = PaddingValues(
                                    top = ContentPadding.calculateTopPadding(),
                                    bottom = ContentPadding.calculateBottomPadding()
                                ),
                                onClick = { state.store?.let(onClickMoreDetails) },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground,
                                ),
                                modifier = Modifier.shimmer(state.isLoading),
                            ) {
                                Text(
                                    text = stringResource(R.string.action_more_details),
                                    textDecoration = TextDecoration.Underline,
                                )
                            }

                            state.store?.shop?.onlineShopUrl?.takeIf { it.isNotBlank() }
                                ?.let { url ->
                                    val urlOpener = rememberUrlOpener(url)
                                    TextButton(
                                        shape = RectangleShape,
                                        contentPadding = PaddingValues(
                                            top = ContentPadding.calculateTopPadding(),
                                            bottom = ContentPadding.calculateBottomPadding()
                                        ),
                                        onClick = urlOpener::open,
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onBackground,
                                        ),
                                        modifier = Modifier.shimmer(state.isLoading),
                                    ) {
                                        Icon(Icons.Default.Link, contentDescription = null)
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = stringResource(R.string.action_visit_website),
                                            textDecoration = TextDecoration.Underline,
                                        )
                                    }
                                }
                        }
                    }

                    QrCodeCard(
                        isLoading = state.isLoading,
                        modifier = Modifier.padding(16.dp),
                        onGetPointsAndReviewClick = onGetPointsAndReviewClick,
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 16.dp),
                    )
                }
                TopAppBar(
                    title = {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = state.store?.name
                                ?: stringResource(R.string.topbar_title_store_details),
                        )
                    },
                    navigationIcon = {
                        NavigateBackIconButton(onClick = onClickBack)
                    },
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
