package co.ke.xently.features.shops.presentation.edit

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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.merchant.data.presentation.utils.asUiText
import co.ke.xently.features.shops.R
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.presentation.utils.asUiText
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import co.ke.xently.features.merchant.data.domain.error.EmailError as MerchantEmailError
import co.ke.xently.features.merchant.data.domain.error.NameError as MerchantNameError
import co.ke.xently.features.shops.data.domain.error.NameError as ShopNameError
import co.ke.xently.features.shops.data.domain.error.WebsiteError as ShopWebsiteError

@Composable
fun ShopEditDetailScreen(modifier: Modifier = Modifier, onClickBack: () -> Unit) {
    val viewModel = hiltViewModel<ShopEditDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is ShopEditDetailEvent.Success -> {
                    when (event.action) {
                        ShopEditDetailAction.ClickSave -> onClickBack()
                        ShopEditDetailAction.ClickSaveAndAddAnother -> {
                            viewModel.onAction(ShopEditDetailAction.ClearFieldsForNewShop)
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.message_shop_saved),
                                duration = SnackbarDuration.Short,
                            )
                        }

                        else -> throw NotImplementedError()
                    }
                }
                is ShopEditDetailEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    ShopEditDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShopEditDetailScreen(
    state: ShopEditDetailUiState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (ShopEditDetailAction) -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_edit_shop_details)) },
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
            Text(
                stringResource(R.string.headline_shop_details),
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.name,
                enabled = !state.disableFields,
                onValueChange = { onAction(ShopEditDetailAction.ChangeShopName(it)) },
                label = { Text(text = stringResource(R.string.text_field_label_shop_name)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words,
                ),
                isError = state.nameError != null,
                supportingText = state.nameError?.let {
                    { Text(text = it.asUiText().asString(context = context)) }
                },
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.website,
                enabled = !state.disableFields,
                onValueChange = { onAction(ShopEditDetailAction.ChangeShopWebsite(it)) },
                label = {
                    Text(text = stringResource(R.string.text_field_label_shop_website))
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next,
                ),
                isError = state.websiteError != null,
                supportingText = state.websiteError?.let {
                    { Text(text = it.asUiText().asString(context = context)) }
                },
            )
            Text(
                stringResource(R.string.headline_merchant_details),
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.merchantFirstName,
                enabled = !state.disableFields,
                onValueChange = {
                    onAction(ShopEditDetailAction.ChangeMerchantFirstName(it))
                },
                label = {
                    Text(text = stringResource(R.string.text_field_label_merchant_first_name))
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words,
                ),
                isError = state.merchantFirstNameError != null,
                supportingText = state.merchantFirstNameError?.let {
                    { Text(text = it.asUiText().asString(context = context)) }
                },
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.merchantLastName,
                enabled = !state.disableFields,
                onValueChange = {
                    onAction(ShopEditDetailAction.ChangeMerchantLastName(it))
                },
                label = {
                    Text(text = stringResource(R.string.text_field_label_merchant_last_name))
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words,
                ),
                isError = state.merchantLastNameError != null,
                supportingText = state.merchantLastNameError?.let {
                    { Text(text = it.asUiText().asString(context = context)) }
                },
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.merchantEmailAddress,
                enabled = !state.disableFields,
                onValueChange = {
                    onAction(ShopEditDetailAction.ChangeMerchantEmailAddress(it))
                },
                label = { Text(text = stringResource(R.string.text_field_label_merchant_email)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Email,
                    capitalization = KeyboardCapitalization.None,
                ),
                isError = state.merchantEmailAddressError != null,
                supportingText = state.merchantEmailAddressError?.let {
                    { Text(text = it.asUiText().asString(context = context)) }
                },
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
                    onClick = { onAction(ShopEditDetailAction.ClickSave) },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) { Text(text = stringResource(R.string.action_save)) }
                Button(
                    enabled = state.enableSaveButton,
                    onClick = { onAction(ShopEditDetailAction.ClickSaveAndAddAnother) },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) { Text(text = stringResource(R.string.action_save_and_add_another)) }
            }
        }
    }
}

private class ShopEditDetailScreenUiState(
    val state: ShopEditDetailUiState,
)

private class ShopEditDetailUiStateParameterProvider :
    PreviewParameterProvider<ShopEditDetailScreenUiState> {
    private val shop = Shop(
        id = 1L,
        name = "Shop name",
        slug = "shop-name",
        onlineShopUrl = "https://example.com",
        links = mapOf(
            "self" to Link(href = "https://example.com"),
            "add-store" to Link(href = "https://example.com/edit"),
        ),
    )
    override val values: Sequence<ShopEditDetailScreenUiState>
        get() = sequenceOf(
            ShopEditDetailScreenUiState(state = ShopEditDetailUiState()),
            ShopEditDetailScreenUiState(
                state = ShopEditDetailUiState(
                    nameError = ShopNameError.entries.random(),
                    websiteError = ShopWebsiteError.entries.random(),
                    merchantFirstNameError = MerchantNameError.entries.random(),
                    merchantLastNameError = MerchantNameError.entries.random(),
                    merchantEmailAddressError = MerchantEmailError.entries.random(),
                ),
            ),
            ShopEditDetailScreenUiState(state = ShopEditDetailUiState(shop = shop)),
            ShopEditDetailScreenUiState(state = ShopEditDetailUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun ShopEditDetailScreenPreview(
    @PreviewParameter(ShopEditDetailUiStateParameterProvider::class)
    state: ShopEditDetailScreenUiState,
) {
    XentlyTheme {
        ShopEditDetailScreen(
            state = state.state,
            snackbarHostState = rememberSnackbarHostState(),
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
        )
    }
}
