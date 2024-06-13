package co.ke.xently.features.shops.presentation.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.shops.R
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.ui.core.presentation.components.PrimaryButton
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton

@Composable
fun ShopEditDetailScreen(modifier: Modifier = Modifier, onClickBack: () -> Unit) {
    val viewModel = hiltViewModel<ShopEditDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)

    ShopEditDetailScreen(
        state = state,
        event = event,
        modifier = modifier,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShopEditDetailScreen(
    state: ShopEditDetailUiState,
    event: ShopEditDetailEvent?,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (ShopEditDetailAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            ShopEditDetailEvent.Success -> onClickBack()
            is ShopEditDetailEvent.Error -> {
                snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                )
            }
        }
    }

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
            )

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                enabled = !state.disableFields,
                label = stringResource(R.string.action_submit_shop_details)
                    .toUpperCase(Locale.current),
                onClick = { onAction(ShopEditDetailAction.ClickSaveDetails) },
            )
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
            event = null,
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
        )
    }
}
