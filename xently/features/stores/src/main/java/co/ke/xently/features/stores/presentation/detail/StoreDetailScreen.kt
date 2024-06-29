package co.ke.xently.features.stores.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.detail.components.QrCodeCard
import co.ke.xently.features.stores.presentation.detail.components.StoreDetailListItem
import co.ke.xently.features.stores.presentation.detail.components.StoreImagesBox
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.components.shimmer
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState

typealias StoreDetailContentScope = BoxScope

@Composable
fun StoreDetailScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickMoreDetails: (Store) -> Unit,
    allStoreProductsContent: @Composable StoreDetailContentScope.() -> Unit = {},
    recommendedProductsContent: @Composable StoreDetailContentScope.() -> Unit = {},
) {
    val viewModel = hiltViewModel<StoreDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is StoreDetailEvent.Success -> Unit
                is StoreDetailEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    StoreDetailScreen(
        state = state,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onClickBack = onClickBack,
        onClickMoreDetails = onClickMoreDetails,
        allStoreProductsContent = allStoreProductsContent,
        recommendedProductsContent = recommendedProductsContent,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreDetailScreen(
    state: StoreDetailUiState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    onClickBack: () -> Unit,
    onClickMoreDetails: (Store) -> Unit,
    allStoreProductsContent: @Composable StoreDetailContentScope.() -> Unit = {},
    recommendedProductsContent: @Composable StoreDetailContentScope.() -> Unit = {},
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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

                        TextButton(
                            shape = RoundedCornerShape(20),
                            contentPadding = PaddingValues(vertical = 12.dp),
                            onClick = { state.store?.let(onClickMoreDetails) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .shimmer(state.isLoading),
                        ) {
                            Text(
                                text = stringResource(R.string.action_more_details),
                                textDecoration = TextDecoration.Underline,
                            )
                        }
                    }

                    QrCodeCard(
                        isLoading = state.isLoading,
                        modifier = Modifier.padding(16.dp),
                        onGetPointsAndReviewClick = { /*TODO*/ },
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
                TopAppBar(
                    title = { /*TODO*/ },
                    navigationIcon = {
                        NavigateBackIconButton(onClick = onClickBack)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            var selectedContentType by rememberSaveable { mutableStateOf(StoreDetailContentType.AllStoreProducts) }
            SecondaryTabRow(selectedTabIndex = selectedContentType.ordinal) {
                StoreDetailContentType.entries.forEach { contentType ->
                    Tab(
                        selected = selectedContentType == contentType,
                        onClick = { selectedContentType = contentType },
                        text = {
                            Text(
                                text = stringResource(contentType.title),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    )
                }
            }
            val pagerState = rememberPagerState(initialPage = selectedContentType.ordinal) {
                StoreDetailContentType.entries.size
            }

            LaunchedEffect(pagerState.currentPage) {
                if (pagerState.currentPage != selectedContentType.ordinal) {
                    selectedContentType = StoreDetailContentType.entries[pagerState.currentPage]
                }
            }
            LaunchedEffect(selectedContentType) {
                if (selectedContentType.ordinal != pagerState.currentPage) {
                    pagerState.animateScrollToPage(selectedContentType.ordinal)
                }
            }

            HorizontalPager(
                state = pagerState,
                key = { StoreDetailContentType.entries[it] },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { selectedPageIndex ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (StoreDetailContentType.entries[selectedPageIndex]) {
                        StoreDetailContentType.AllStoreProducts -> allStoreProductsContent()
                        StoreDetailContentType.RecommendedProducts -> recommendedProductsContent()
                    }
                }
            }
        }
    }
}

internal class StoreDetailUiStateParameterProvider : PreviewParameterProvider<StoreDetailUiState> {
    override val values: Sequence<StoreDetailUiState>
        get() = sequenceOf(
            StoreDetailUiState(
                isLoading = true,
            ),
            StoreDetailUiState(
                store = Store.DEFAULT,
            ),
        )
}

@XentlyPreview
@Composable
private fun StoreDetailScreenPreview(
    @PreviewParameter(StoreDetailUiStateParameterProvider::class)
    state: StoreDetailUiState,
) {
    XentlyTheme {
        StoreDetailScreen(
            state = state,
            onClickBack = {},
            onClickMoreDetails = {},
        )
    }
}
