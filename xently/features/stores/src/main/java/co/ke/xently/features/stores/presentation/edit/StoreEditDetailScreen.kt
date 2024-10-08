package co.ke.xently.features.stores.presentation.edit

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.location.picker.presentation.PickLocationDialog
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.openinghours.presentation.WeeklyOpeningHourInput
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.EmailError
import co.ke.xently.features.stores.data.domain.error.FieldError
import co.ke.xently.features.stores.data.domain.error.LocationError
import co.ke.xently.features.stores.data.domain.error.NameError
import co.ke.xently.features.stores.data.domain.error.PhoneError
import co.ke.xently.features.stores.data.domain.error.UnclassifiedFieldError
import co.ke.xently.features.stores.presentation.components.StoreCategoryFilterChip
import co.ke.xently.features.ui.core.presentation.components.XentlyOutlinedChipTextField
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

@Composable
fun StoreEditDetailScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    val viewModel = hiltViewModel<StoreEditDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is StoreEditDetailEvent.Success -> {
                    when (event.action) {
                        StoreEditDetailAction.ClickSave -> onClickBack()
                        StoreEditDetailAction.ClickSaveAndAddAnother -> {
                            viewModel.onAction(StoreEditDetailAction.ClearFieldsForNewStore)
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.message_store_saved),
                                duration = SnackbarDuration.Short,
                            )
                        }

                        else -> throw NotImplementedError()
                    }
                }

                is StoreEditDetailEvent.Error -> {
                    if (event.error !is FieldError) {
                        snackbarHostState.showSnackbar(
                            event.error.toUiText().asString(context = context),
                            duration = SnackbarDuration.Long,
                        )
                    }
                }
            }
        }
    }

    StoreEditDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        categories = categories,
        modifier = modifier,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
        retrieveServicesSuggestions = {
            viewModel.storeServicesSearchSuggestions.collectAsStateWithLifecycle(
                initialValue = emptyList(),
                minActiveState = Lifecycle.State.RESUMED,
            ).value
        },
        retrieveCategoriesSuggestions = {
            viewModel.storeCategoriesSearchSuggestions.collectAsStateWithLifecycle(
                initialValue = emptyList(),
                minActiveState = Lifecycle.State.RESUMED,
            ).value
        },
        retrievePaymentMethodsSuggestions = {
            viewModel.storePaymentMethodsSearchSuggestions.collectAsStateWithLifecycle(
                initialValue = emptyList(),
                minActiveState = Lifecycle.State.RESUMED,
            ).value
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreEditDetailScreen(
    state: StoreEditDetailUiState,
    snackbarHostState: SnackbarHostState,
    categories: List<StoreCategory>,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (StoreEditDetailAction) -> Unit,
    retrieveServicesSuggestions: @Composable () -> List<String> = { emptyList() },
    retrieveCategoriesSuggestions: @Composable () -> List<String> = { emptyList() },
    retrievePaymentMethodsSuggestions: @Composable () -> List<String> = { emptyList() },
) {
    val focusManager = LocalFocusManager.current

    var showLocationPicker by rememberSaveable { mutableStateOf(false) }

    if (showLocationPicker) {
        PickLocationDialog(
            location = state.location,
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = { showLocationPicker = false },
            onLocationChange = { onAction(StoreEditDetailAction.ChangeLocation(it)) },
        )
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_edit_store_details)) },
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
                        StoreCategoryFilterChip(
                            category = item,
                            onClickSelectCategory = {
                                onAction(StoreEditDetailAction.SelectCategory(item))
                            },
                            onClickRemoveCategory = {
                                onAction(StoreEditDetailAction.RemoveCategory(item))
                            },
                        )
                    }
                }
            }

            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.name,
                enabled = !state.disableFields,
                onValueChange = { onAction(StoreEditDetailAction.ChangeName(it)) },
                label = { Text(text = stringResource(R.string.text_field_label_store_name)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words,
                ),
                isError = !state.nameError.isNullOrEmpty(),
                supportingText = state.nameError?.let {
                    { Text(text = it.asString()) }
                },
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.locationString,
                onValueChange = { onAction(StoreEditDetailAction.ChangeLocationString(it)) },
                placeholder = { Text(text = stringResource(R.string.text_field_label_store_location)) },
                trailingIcon = {
                    IconButton(onClick = { focusManager.clearFocus(); showLocationPicker = true }) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.content_desc_store_pick_location),
                        )
                    }
                },
                isError = state.locationError != null,
                supportingText = {
                    val error = state.locationError
                    if (!error.isNullOrEmpty()) {
                        Text(text = error.asString())
                    } else {
                        Text(text = stringResource(R.string.text_field_supporting_text_location))
                    }
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.email,
                enabled = !state.disableFields,
                onValueChange = { onAction(StoreEditDetailAction.ChangeEmailAddress(it)) },
                label = {
                    Text(text = stringResource(R.string.text_field_label_store_email_address))
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                isError = !state.emailError.isNullOrEmpty(),
                supportingText = state.emailError?.let {
                    { Text(text = it.asString()) }
                },
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.phone,
                enabled = !state.disableFields,
                onValueChange = { onAction(StoreEditDetailAction.ChangePhoneNumber(it)) },
                label = {
                    Text(text = stringResource(R.string.text_field_label_store_phone_number))
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                ),
                isError = !state.phoneError.isNullOrEmpty(),
                supportingText = state.phoneError?.let {
                    { Text(text = it.asString()) }
                },
            )

            XentlyOutlinedChipTextField(
                enabled = !state.disableFields,
                chips = state.services,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                retrieveSuggestions = retrieveServicesSuggestions,
                onTextChange = {
                    onAction(StoreEditDetailAction.OnServiceQueryChange(it))
                },
                onSubmit = {
                    onAction(StoreEditDetailAction.AddService(it))
                },
                label = {
                    Text(text = stringResource(R.string.text_field_label_store_services))
                },
                placeholder = {
                    Text(text = stringResource(R.string.text_field_placeholder_services))
                },
                onClickTrailingIcon = {
                    onAction(StoreEditDetailAction.RemoveService(it))
                },
            )

            XentlyOutlinedChipTextField(
                enabled = !state.disableFields,
                chips = state.additionalCategories,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                retrieveSuggestions = retrieveCategoriesSuggestions,
                onTextChange = {
                    onAction(StoreEditDetailAction.OnCategoryQueryChange(it))
                },
                onSubmit = {
                    onAction(StoreEditDetailAction.AddAdditionalCategory(it))
                },
                label = {
                    Text(text = stringResource(R.string.text_field_label_store_additional_categories))
                },
                onClickTrailingIcon = {
                    onAction(StoreEditDetailAction.RemoveAdditionalCategory(it))
                },
            )

            XentlyOutlinedChipTextField(
                enabled = !state.disableFields,
                chips = state.paymentMethods,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                retrieveSuggestions = retrievePaymentMethodsSuggestions,
                onTextChange = {
                    onAction(StoreEditDetailAction.OnPaymentMethodQueryChange(it))
                },
                onSubmit = {
                    onAction(StoreEditDetailAction.AddPaymentMethod(it))
                },
                label = {
                    Text(text = stringResource(R.string.text_field_label_store_payment_methods))
                },
                onClickTrailingIcon = {
                    onAction(StoreEditDetailAction.RemovePaymentMethod(it))
                },
            )

            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.description,
                enabled = !state.disableFields,
                onValueChange = {
                    onAction(StoreEditDetailAction.ChangeDescription(it))
                },
                label = {
                    Text(text = stringResource(R.string.text_field_label_store_short_description))
                },
                minLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
                isError = !state.descriptionError.isNullOrEmpty(),
                supportingText = state.descriptionError?.let {
                    { Text(text = it.asString()) }
                },
            )

            if (state.openingHours.isNotEmpty()) {
                WeeklyOpeningHourInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    enableInteraction = !state.disableFields,
                    openingHours = state.openingHours,
                    onTimeChange = {
                        focusManager.clearFocus()
                        onAction(StoreEditDetailAction.ChangeOpeningHourTime(it))
                    },
                    onOpenStatusChange = {
                        focusManager.clearFocus()
                        onAction(StoreEditDetailAction.ChangeOpeningHourOpenStatus(it))
                    },
                )
            }
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
                    onClick = { onAction(StoreEditDetailAction.ClickSave) },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) { Text(text = stringResource(R.string.action_save)) }
                Button(
                    enabled = state.enableSaveButton,
                    onClick = { onAction(StoreEditDetailAction.ClickSaveAndAddAnother) },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) { Text(text = stringResource(R.string.action_save_and_add_another)) }
            }
        }
    }
}

private class StoreEditDetailScreenUiState(
    val state: StoreEditDetailUiState,
    val categories: List<StoreCategory> = List(10) {
        StoreCategory(
            name = "Category $it",
            selected = it < 2,
        )
    },
)

private class StoreEditDetailUiStateParameterProvider :
    PreviewParameterProvider<StoreEditDetailScreenUiState> {
    private val store = Store(
        name = "Westlands",
        shop = Shop(name = "Ranalo K'Osewe"),
        description = """Short description about the business/hotel will go here.
                |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                |
                |Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.""".trimMargin(),
        openingHours = DayOfWeek.entries.map {
            OpeningHour(
                dayOfWeek = it,
                openTime = Time(7, 0),
                closeTime = Time(17, 0),
                open = it.isoDayNumber !in setOf(6, 7),
            )
        },
    )
    override val values: Sequence<StoreEditDetailScreenUiState>
        get() = sequenceOf(
            StoreEditDetailScreenUiState(state = StoreEditDetailUiState()),
            StoreEditDetailScreenUiState(state = StoreEditDetailUiState(store = store)),
            StoreEditDetailScreenUiState(state = StoreEditDetailUiState(isLoading = true)),
            StoreEditDetailScreenUiState(
                state = StoreEditDetailUiState(
                    nameError = listOf(
                        NameError.entries.random(),
                        UnclassifiedFieldError("length must be between 0 and 500"),
                        UnclassifiedFieldError("must be a future date"),
                    ),
                    locationError = listOf(LocationError.entries.random()),
                    emailError = listOf(EmailError.entries.random()),
                    phoneError = listOf(PhoneError.entries.random()),
                ),
            ),
        )
}

@XentlyPreview
@Composable
private fun StoreEditDetailScreenPreview(
    @PreviewParameter(StoreEditDetailUiStateParameterProvider::class)
    state: StoreEditDetailScreenUiState,
) {
    XentlyTheme {
        StoreEditDetailScreen(
            state = state.state,
            snackbarHostState = rememberSnackbarHostState(),
            categories = state.categories,
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
        )
    }
}
