package co.ke.xently.features.stores.presentation.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.openinghours.presentation.WeeklyOpeningHourInput
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.presentation.components.StoreCategoryFilterChip
import co.ke.xently.features.ui.core.presentation.components.AddCategorySection
import co.ke.xently.features.ui.core.presentation.components.PrimaryButton
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.pagination.components.PaginatedContentLazyRow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

@Composable
internal fun StoreEditDetailScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickPickLocation: (Location) -> Unit,
) {
    val viewModel = hiltViewModel<StoreEditDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val categories = viewModel.categories.collectAsLazyPagingItems()

    StoreEditDetailScreen(
        state = state,
        event = event,
        modifier = modifier,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
        categories = categories,
        onClickPickLocation = onClickPickLocation,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreEditDetailScreen(
    state: StoreEditDetailUiState,
    event: StoreEditDetailEvent?,
    categories: LazyPagingItems<StoreCategory>,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickPickLocation: (Location) -> Unit,
    onAction: (StoreEditDetailAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is StoreEditDetailEvent.Error -> {
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

            StoreEditDetailEvent.Success -> onClickBack()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                CenterAlignedTopAppBar(
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
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AddCategorySection(
                name = state.categoryName,
                onNameValueChange = { onAction(StoreEditDetailAction.ChangeCategoryName(it)) },
                onAddClick = { onAction(StoreEditDetailAction.ClickAddCategory) },
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
                            StoreCategoryFilterChip(
                                item = item,
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
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.locationString,
                readOnly = true,
                enabled = false,
                onValueChange = {},
                placeholder = { Text(text = stringResource(R.string.text_field_label_store_location)) },
                trailingIcon = {
                    IconButton(onClick = { onClickPickLocation(state.location) }) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.content_desc_store_pick_location),
                        )
                    }
                },
                supportingText = {
                    Text(text = stringResource(R.string.text_field_supporting_text_location))
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(onClick = { onClickPickLocation(state.location) }),
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
            )
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = rememberSaveable(state.services) { state.services.joinToString() },
                enabled = !state.disableFields,
                onValueChange = { onAction(StoreEditDetailAction.AddService(it)) },
                label = {
                    Text(text = stringResource(R.string.text_field_label_store_services))
                },
                supportingText = {
                    Text(text = stringResource(R.string.text_field_supporting_text_services))
                },
                placeholder = {
                    Text(text = stringResource(R.string.text_field_placeholder_services))
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
            )

            if (state.openingHours.isNotEmpty()) {
                WeeklyOpeningHourInput(
                    enableInteraction = !state.disableFields,
                    openingHours = state.openingHours,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onTimeChange = { onAction(StoreEditDetailAction.ChangeOpeningHourTime(it)) },
                    onSelectedOpeningHourChange = {
                        onAction(
                            StoreEditDetailAction.ChangeOpeningHour(
                                it
                            )
                        )
                    },
                    onOpenStatusChange = {
                        onAction(
                            StoreEditDetailAction.ChangeOpeningHourOpenStatus(
                                it
                            )
                        )
                    }
                )
            }
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
            )

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                enabled = !state.disableFields,
                label = stringResource(R.string.action_submit_store_details)
                    .toUpperCase(Locale.current),
                onClick = { onAction(StoreEditDetailAction.ClickSaveDetails) },
            )
        }
    }
}

private class StoreEditDetailScreenUiState(
    val state: StoreEditDetailUiState,
    val categories: PagingData<StoreCategory> = PagingData.from(
        List(10) {
            StoreCategory(
                name = "Category $it",
                selected = it < 2,
            )
        },
    ),
)

private class StoreEditDetailUiStateParameterProvider :
    PreviewParameterProvider<StoreEditDetailScreenUiState> {
    private val store = Store(
        name = "Westlands",
        shop = Shop(name = "Ranalo K'Osewe"),
        description = "Short description about the business/hotel will go here. Lorem ipsum dolor trui loerm ipsum is a repetitive alternative place holder text for design projects.",
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
        )
}

@XentlyPreview
@Composable
private fun StoreEditDetailScreenPreview(
    @PreviewParameter(StoreEditDetailUiStateParameterProvider::class)
    state: StoreEditDetailScreenUiState,
) {
    val categories = flowOf(state.categories).collectAsLazyPagingItems()
    XentlyTheme {
        StoreEditDetailScreen(
            state = state.state,
            event = null,
            categories = categories,
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
            onClickPickLocation = {},
        )
    }
}
