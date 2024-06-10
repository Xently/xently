package co.ke.xently.features.products.presentation.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.R
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.DataError
import co.ke.xently.features.products.presentation.components.ProductCategoryFilterChip
import co.ke.xently.features.products.presentation.edit.components.EditProductImagesCard
import co.ke.xently.features.ui.core.presentation.components.AddCategorySection
import co.ke.xently.features.ui.core.presentation.components.PrimaryButton
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.pagination.components.PaginatedContentLazyRow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun ProductEditDetailScreen(modifier: Modifier = Modifier, onClickBack: () -> Unit) {
    val viewModel = hiltViewModel<ProductEditDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val categories = viewModel.categories.collectAsLazyPagingItems()

    ProductEditDetailScreen(
        state = state,
        event = event,
        modifier = modifier,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
        categories = categories,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductEditDetailScreen(
    state: ProductEditDetailUiState,
    event: ProductEditDetailEvent?,
    categories: LazyPagingItems<ProductCategory>,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (ProductEditDetailAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is ProductEditDetailEvent.Error -> {
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

            ProductEditDetailEvent.Success -> onClickBack()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(text = stringResource(R.string.top_bar_title_edit_product_details)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AddCategorySection(
                name = state.categoryName,
                onNameValueChange = { onAction(ProductEditDetailAction.ChangeCategoryName(it)) },
                onAddClick = { /*TODO*/ },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth(),
            )

            if (categories.itemCount > 0) {
                PaginatedContentLazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    items = categories,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    items(
                        categories.itemCount,
                        key = { categories[it]?.name ?: ">>>$it<<<" },
                    ) { index ->
                        val item = categories[index]
                        if (item != null) {
                            ProductCategoryFilterChip(
                                item = item,
                                onClickSelectCategory = {
                                    onAction(ProductEditDetailAction.SelectCategory(item))
                                },
                                onClickRemoveCategory = {
                                    onAction(ProductEditDetailAction.RemoveCategory(item))
                                },
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.name,
                enabled = !state.disableFields,
                onValueChange = { onAction(ProductEditDetailAction.ChangeName(it)) },
                label = { Text(text = stringResource(R.string.text_field_label_product_name)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words,
                ),
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.unitPrice,
                enabled = !state.disableFields,
                onValueChange = { onAction(ProductEditDetailAction.ChangeUnitPrice(it)) },
                label = {
                    Text(text = stringResource(R.string.text_field_label_product_unit_price))
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.description,
                enabled = !state.disableFields,
                onValueChange = {
                    onAction(ProductEditDetailAction.ChangeDescription(it))
                },
                label = {
                    Text(text = stringResource(R.string.text_field_label_product_short_description))
                },
                minLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            )

            Text(
                text = stringResource(R.string.headline_upload_product_images),
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.labelLarge,
            )

            EditProductImagesCard(
                images = remember { List(10) { null } },
                onClickImage = { /*TODO*/ },
                onClickRemoveImage = { /*TODO*/ },
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                enabled = !state.disableFields,
                label = stringResource(R.string.action_submit_product_details)
                    .toUpperCase(Locale.current),
                onClick = { onAction(ProductEditDetailAction.ClickSaveDetails) },
            )
        }
    }
}

private class ProductEditDetailScreenUiState(
    val state: ProductEditDetailUiState,
    val categories: PagingData<ProductCategory> = PagingData.from(
        List(10) {
            ProductCategory(
                name = "Category $it",
                selected = it < 2,
            )
        },
    ),
)

private class ProductEditDetailUiStateParameterProvider :
    PreviewParameterProvider<ProductEditDetailScreenUiState> {
    private val product = Product(
        name = "Example product name",
        unitPrice = 1234.0,
        description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
    )
    override val values: Sequence<ProductEditDetailScreenUiState>
        get() = sequenceOf(
            ProductEditDetailScreenUiState(state = ProductEditDetailUiState()),
            ProductEditDetailScreenUiState(state = ProductEditDetailUiState(product = product)),
            ProductEditDetailScreenUiState(state = ProductEditDetailUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun ProductEditDetailScreenPreview(
    @PreviewParameter(ProductEditDetailUiStateParameterProvider::class)
    state: ProductEditDetailScreenUiState,
) {
    val categories = flowOf(state.categories).collectAsLazyPagingItems()
    XentlyTheme {
        ProductEditDetailScreen(
            state = state.state,
            event = null,
            categories = categories,
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
        )
    }
}
