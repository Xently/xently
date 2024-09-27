package co.ke.xently.features.products.presentation.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.R
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.DescriptionError
import co.ke.xently.features.products.data.domain.error.NameError
import co.ke.xently.features.products.data.domain.error.PriceError
import co.ke.xently.features.products.data.domain.error.UnclassifiedFieldError
import co.ke.xently.features.products.presentation.components.ProductCategoryFilterChip
import co.ke.xently.features.products.presentation.edit.components.EditProductImagesCard
import co.ke.xently.features.ui.core.presentation.components.XentlyOutlinedChipTextField
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import kotlin.random.Random

@Composable
fun ProductEditDetailScreen(modifier: Modifier = Modifier, onClickBack: () -> Unit) {
    val viewModel = hiltViewModel<ProductEditDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is ProductEditDetailEvent.Success -> {
                    when (event.action) {
                        ProductEditDetailAction.ClickSave -> onClickBack()
                        ProductEditDetailAction.ClickSaveAndAddAnother -> {
                            viewModel.onAction(ProductEditDetailAction.ClearFieldsForNewProduct)
                            snackbarHostState.showSnackbar(
                                context.getString(R.string.message_product_saved_successfully),
                                duration = SnackbarDuration.Short,
                            )
                        }

                        else -> throw NotImplementedError()
                    }
                }

                is ProductEditDetailEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    ProductEditDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
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
    snackbarHostState: SnackbarHostState,
    categories: List<ProductCategory>,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (ProductEditDetailAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_edit_product_details)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )
                }
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
            if (categories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    items(categories, key = { it.name }) { item ->
                        ProductCategoryFilterChip(
                            category = item,
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
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                isError = !state.nameError.isNullOrEmpty(),
                supportingText = state.nameError?.let {
                    { Text(text = it.asString()) }
                },
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
                isError = !state.unitPriceError.isNullOrEmpty(),
                supportingText = state.unitPriceError?.let {
                    { Text(text = it.asString()) }
                },
            )

            XentlyOutlinedChipTextField(
                enabled = !state.disableFields,
                chips = state.additionalCategories,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onSubmit = {
                    onAction(ProductEditDetailAction.AddAdditionalCategory(it))
                },
                label = {
                    Text(text = stringResource(R.string.text_field_label_product_additional_categories))
                },
            )

            XentlyOutlinedChipTextField(
                enabled = !state.disableFields,
                chips = state.synonyms,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onSubmit = {
                    onAction(ProductEditDetailAction.AddSynonym(it))
                },
                label = {
                    Text(text = stringResource(R.string.text_field_label_product_synonyms))
                },
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
                isError = !state.descriptionError.isNullOrEmpty(),
                supportingText = state.descriptionError?.let {
                    { Text(text = it.asString()) }
                },
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
            )

            Text(
                text = stringResource(R.string.headline_upload_product_images),
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.labelLarge,
            )

            EditProductImagesCard(
                images = state.images,
                modifier = Modifier.padding(horizontal = 16.dp),
                withResult = { onAction(ProductEditDetailAction.ProcessImageData(it)) },
                onClickRemoveImage = { onAction(ProductEditDetailAction.RemoveImageAtPosition(it)) },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    enabled = state.enableSaveButton,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(ProductEditDetailAction.ClickSave) },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) { Text(text = stringResource(R.string.action_save)) }
                Button(
                    enabled = state.enableSaveButton,
                    onClick = { onAction(ProductEditDetailAction.ClickSaveAndAddAnother) },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) { Text(text = stringResource(R.string.action_save_and_add_another)) }
            }
        }
    }
}

private class ProductEditDetailScreenUiState(
    val state: ProductEditDetailUiState,
    val categories: List<ProductCategory> = List(10) {
        ProductCategory(
            name = "Category $it",
            selected = it < 2,
        )
    },
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
            ProductEditDetailScreenUiState(
                state = ProductEditDetailUiState(
                    nameError = listOf(NameError.entries.random()),
                    unitPriceError = listOf(PriceError.entries.random()),
                    descriptionError = listOf(
                        DescriptionError.TooLong(
                            Random.nextInt(100, 200)
                        ),
                        UnclassifiedFieldError("length must be between 0 and 500"),
                        UnclassifiedFieldError("must be a future date"),
                    ),
                ),
            ),
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
    XentlyTheme {
        ProductEditDetailScreen(
            state = state.state,
            snackbarHostState = rememberSnackbarHostState(),
            categories = state.categories,
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
        )
    }
}
