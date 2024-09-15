package co.ke.xently.features.recommendations.presentation.request

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.location.picker.presentation.PickLocationDialog
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.presentation.components.ProductCategoryFilterChip
import co.ke.xently.features.recommendations.R
import co.ke.xently.features.recommendations.presentation.RecommendationAction
import co.ke.xently.features.recommendations.presentation.RecommendationEvent
import co.ke.xently.features.recommendations.presentation.RecommendationUiState
import co.ke.xently.features.recommendations.presentation.RecommendationViewModel
import co.ke.xently.features.recommendations.presentation.utils.toUiText
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.stores.presentation.components.StoreCategoryFilterChip
import co.ke.xently.features.ui.core.presentation.components.PrimaryButton
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.location.tracker.presentation.LocalLocationState
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.components.SearchBar
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState

@Composable
internal fun RecommendationRequestScreen(
    viewModel: RecommendationViewModel,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickSearch: () -> Unit,
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

    RecommendationRequestScreen(
        state = state,
        storeCategories = storeCategories,
        productCategories = productCategories,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onClickBack = onClickBack,
        onClickSearch = onClickSearch,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun RecommendationRequestScreen(
    state: RecommendationUiState,
    storeCategories: List<StoreCategory>,
    productCategories: List<ProductCategory>,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    onClickBack: () -> Unit,
    onClickSearch: () -> Unit,
    onAction: (RecommendationAction) -> Unit,
) {
    var showLocationPicker by rememberSaveable { mutableStateOf(false) }

    val isLocationUsable by remember(state.location) {
        derivedStateOf { state.location.isUsable() }
    }
    if (showLocationPicker) {
        PickLocationDialog(
            location = state.location,
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = { showLocationPicker = false },
            onLocationChange = { onAction(RecommendationAction.ChangeLocation(it)) },
        )
    } else if (!isLocationUsable) {
        // Will use previously saved location...
        val currentLocation by LocalLocationState.current
        LaunchedEffect(currentLocation) {
            currentLocation?.also {
                onAction(RecommendationAction.ChangeLocation(it))
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets),
            ) {
                var initSearch by rememberSaveable { mutableStateOf(false) }
                if (!initSearch) {
                    CenterAlignedTopAppBar(
                        windowInsets = WindowInsets.waterfall,
                        navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                        title = { Text(text = stringResource(R.string.topbar_title_recommendation_request)) },
                        actions = {
                            androidx.compose.animation.AnimatedVisibility(state.locationQuery.isNotBlank()) {
                                TooltipBox(
                                    state = rememberTooltipState(),
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(text = stringResource(R.string.action_label_pick_location))
                                        }
                                    },
                                ) {
                                    IconButton(onClick = { showLocationPicker = true }) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = stringResource(R.string.action_label_pick_location),
                                        )
                                    }
                                }
                            }
                        },
                    )
                    AnimatedVisibility(state.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                SearchBar(
                    query = state.locationQuery,
                    exitSearchIcon = Icons.Default.Close,
                    clearSearchQueryIcon = Icons.AutoMirrored.Filled.Backspace,
                    placeholder = stringResource(R.string.search_placeholder_location),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onExpandedChange = { initSearch = it },
                    onQueryChange = { onAction(RecommendationAction.ChangeLocationQuery(it)) },
                    onSearch = { onAction(RecommendationAction.SearchLocation(it)) },
                    blankQueryIcon = {
                        IconButton(onClick = { showLocationPicker = true }) {
                            Icon(
                                Icons.Default.AddLocationAlt,
                                contentDescription = stringResource(R.string.action_label_pick_location),
                            )
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val focusManager = LocalFocusManager.current

            OutlinedTextField(value = state.productName,
                enabled = !state.disableFields,
                onValueChange = { onAction(RecommendationAction.ChangeProductName(it)) },
                label = { Text(text = stringResource(R.string.text_field_label_product_name)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                trailingIcon = {
                    IconButton(
                        enabled = remember(state.productName) {
                            derivedStateOf { state.productName.isNotBlank() }
                        }.value,
                        onClick = { onAction(RecommendationAction.AddProductName) },
                    ) {
                        Icon(
                            Icons.Default.PostAdd,
                            contentDescription = stringResource(R.string.content_desc_add_to_shopping_list),
                        )
                    }
                })

            if (remember(state.shoppingList) { derivedStateOf { state.shoppingList.isNotEmpty() } }.value) {
                Column {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.headline_shopping_list).toUpperCase(Locale.current),
                    )
                    for (name in state.shoppingList) {
                        ListItem(
                            headlineContent = { Text(text = name) },
                            trailingContent = {
                                IconButton(
                                    onClick = {
                                        onAction(RecommendationAction.RemoveProductName(name))
                                    },
                                ) {
                                    Icon(
                                        Icons.Default.Close, contentDescription = stringResource(
                                            R.string.content_desc_remove_from_shopping_list, name
                                        )
                                    )
                                }
                            },
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.Sell, contentDescription = null)
                Text(
                    text = stringResource(R.string.headline_price_range),
                    fontWeight = FontWeight.Bold,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                val minimumPrice = remember(state.minimumPrice) {
                    state.minimumPrice?.removeSuffix(".0") ?: ""
                }
                OutlinedTextField(
                    value = minimumPrice,
                    onValueChange = { onAction(RecommendationAction.ChangeMinimumPrice(it)) },
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    isError = !state.minimumPriceError.isNullOrEmpty(),
                    supportingText = state.minimumPriceError?.let {
                        { Text(text = it.toUiText()) }
                    },
                    label = {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = stringResource(R.string.text_field_label_min_price),
                        )
                    },
                    prefix = { Text(text = "KES") },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        },
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = remember(state.maximumPrice) {
                            derivedStateOf {
                                if (state.maximumPrice.isNullOrBlank()) {
                                    ImeAction.Next
                                } else {
                                    KeyboardOptions.Default.imeAction
                                }
                            }
                        }.value,
                    ),
                )

                val maximumPrice = remember(state.maximumPrice) {
                    state.maximumPrice?.removeSuffix(".0") ?: ""
                }
                OutlinedTextField(
                    value = maximumPrice,
                    onValueChange = { onAction(RecommendationAction.ChangeMaximumPrice(it)) },
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    isError = !state.maximumPriceError.isNullOrEmpty(),
                    supportingText = state.maximumPriceError?.let {
                        { Text(text = it.toUiText()) }
                    },
                    label = {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = stringResource(R.string.text_field_label_max_price),
                        )
                    },
                    prefix = { Text(text = "KES") },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        },
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = remember(state.minimumPrice) {
                            derivedStateOf {
                                if (state.minimumPrice.isNullOrBlank()) {
                                    ImeAction.Next
                                } else {
                                    KeyboardOptions.Default.imeAction
                                }
                            }
                        }.value,
                    ),
                )
            }

            if (productCategories.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.headline_product_categories),
                    fontWeight = FontWeight.Bold,
                )

                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (category in productCategories) {
                        ProductCategoryFilterChip(
                            category = category,
                            onClickSelectCategory = {
                                onAction(RecommendationAction.ProductSelectCategory(category))
                            },
                            onClickRemoveCategory = {
                                onAction(RecommendationAction.ProductRemoveCategory(category))
                            },
                        )
                    }
                }
            }

            if (storeCategories.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.headline_store_categories),
                    fontWeight = FontWeight.Bold,
                )

                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (category in storeCategories) {
                        StoreCategoryFilterChip(
                            category = category,
                            onClickSelectCategory = {
                                onAction(RecommendationAction.StoreSelectCategory(category))
                            },
                            onClickRemoveCategory = {
                                onAction(RecommendationAction.StoreRemoveCategory(category))
                            },
                        )
                    }
                }
            }

            PrimaryButton(
                onClick = onClickSearch,
                enabled = state.enableSearchButton,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp),
                label = stringResource(state.searchButtonLabel),
            )
        }
    }
}

private class RecommendationRequestScreenUiState(
    val state: RecommendationUiState,
    val storeCategories: List<StoreCategory> = List(10) {
        StoreCategory(
            name = "Category $it",
            selected = it < 2,
        )
    },
    val productCategories: List<ProductCategory> = List(10) {
        ProductCategory(
            name = "Category $it",
            selected = it < 2,
        )
    },
)

private class RecommendationUiStateParameterProvider :
    PreviewParameterProvider<RecommendationRequestScreenUiState> {
    override val values: Sequence<RecommendationRequestScreenUiState>
        get() = sequenceOf(
            RecommendationRequestScreenUiState(state = RecommendationUiState()),
            RecommendationRequestScreenUiState(state = RecommendationUiState(isLoading = true)),
            RecommendationRequestScreenUiState(
                state = RecommendationUiState(
                    shoppingList = List(3) { "Product $it" },
                ),
            ),
            RecommendationRequestScreenUiState(
                state = RecommendationUiState(
                    isLoading = true,
                    shoppingList = List(3) { "Product $it" },
                ),
            ),
        )
}


@XentlyPreview
@Composable
private fun RecommendationRequestScreenPreview(
    @PreviewParameter(RecommendationUiStateParameterProvider::class) state: RecommendationRequestScreenUiState,
) {
    XentlyTheme {
        RecommendationRequestScreen(
            state = state.state,
            storeCategories = state.storeCategories,
            productCategories = state.productCategories,
            onClickBack = {},
            onClickSearch = {},
            onAction = {},
        )
    }
}